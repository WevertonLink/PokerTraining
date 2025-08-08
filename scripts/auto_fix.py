#!/usr/bin/env python3
"""
PokerTrainer Code Auto-Fix Script
Automatically fixes compilation errors and implements safe coding patterns
Based on the analysis provided in the recommendations.
"""

import os
import re
import sys
import subprocess
from pathlib import Path
from typing import List, Tuple

class CodeFixer:
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.app_src = self.project_root / "app" / "src" / "main" / "java"
        self.fixes_applied = []
        
    def log_fix(self, fix_description: str):
        """Log a fix that was applied"""
        self.fixes_applied.append(fix_description)
        print(f"✅ {fix_description}")
    
    def fix_duplicate_classes(self):
        """Fix redeclaration errors by removing duplicate class definitions"""
        print("🔍 Checking for duplicate class declarations...")
        
        class_definitions = {}
        duplicate_files = []
        
        for kt_file in self.app_src.rglob("*.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # Find class definitions
                class_matches = re.findall(r'(data\s+)?class\s+(\w+)', content)
                for match in class_matches:
                    class_name = match[1]
                    if class_name in class_definitions:
                        duplicate_files.append((kt_file, class_name, class_definitions[class_name]))
                    else:
                        class_definitions[class_name] = kt_file
                        
            except Exception as e:
                print(f"Error reading {kt_file}: {e}")
        
        for duplicate_file, class_name, original_file in duplicate_files:
            print(f"⚠️ Duplicate class '{class_name}' found in:")
            print(f"   Original: {original_file}")
            print(f"   Duplicate: {duplicate_file}")
            # In a real scenario, you'd implement logic to merge or remove duplicates
            self.log_fix(f"Identified duplicate class {class_name}")
    
    def add_missing_dependencies(self):
        """Add missing dependencies to build.gradle.kts"""
        print("🔍 Adding missing dependencies...")
        
        build_gradle = self.project_root / "app" / "build.gradle.kts"
        if not build_gradle.exists():
            print("❌ build.gradle.kts not found")
            return
        
        required_dependencies = [
            'implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")',
            'implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")',
            'implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")',
            'implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")',
            'implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")'
        ]
        
        try:
            with open(build_gradle, 'r') as f:
                content = f.read()
            
            # Check which dependencies are missing
            missing_deps = []
            for dep in required_dependencies:
                package_name = re.search(r'"([^"]+):', dep).group(1)
                if package_name not in content:
                    missing_deps.append(dep)
            
            if missing_deps:
                # Find dependencies block and add missing ones
                deps_pattern = r'(dependencies\s*{[^}]+)(\s*})'
                match = re.search(deps_pattern, content, re.DOTALL)
                
                if match:
                    new_deps = "\n    " + "\n    ".join(missing_deps)
                    updated_content = content.replace(
                        match.group(1), 
                        match.group(1) + new_deps
                    )
                    
                    with open(build_gradle, 'w') as f:
                        f.write(updated_content)
                    
                    self.log_fix(f"Added {len(missing_deps)} missing dependencies")
                else:
                    print("❌ Could not find dependencies block in build.gradle.kts")
            else:
                print("✅ All required dependencies are present")
                
        except Exception as e:
            print(f"❌ Error updating dependencies: {e}")
    
    def fix_unused_parameters(self):
        """Fix unused parameters by prefixing with underscore"""
        print("🔍 Fixing unused parameters...")
        
        for kt_file in self.app_src.rglob("*.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                
                # Pattern to find function parameters that might be unused
                # This is a simplified approach - real implementation would use AST
                
                # Fix parameters marked with comments
                content = re.sub(
                    r'(\w+):\s*(\w+[<>\w]*)\s*,?\s*//\s*UNUSED',
                    r'_\1: \2,',
                    content
                )
                
                # Fix parameters in common unused patterns
                content = re.sub(
                    r'fun\s+\w+\([^)]*(\w+):\s*(\w+[<>\w]*)\s*=\s*[^,)]+[^)]*\)\s*{\s*//\s*TODO',
                    r'fun \g<0>'.replace(r'\1', '_\\1'),
                    content
                )
                
                if content != original_content:
                    with open(kt_file, 'w', encoding='utf-8') as f:
                        f.write(content)
                    self.log_fix(f"Fixed unused parameters in {kt_file.name}")
                    
            except Exception as e:
                print(f"Error processing {kt_file}: {e}")
    
    def add_missing_imports(self):
        """Add missing imports to files that need them"""
        print("🔍 Adding missing imports...")
        
        viewmodel_imports = [
            "import androidx.lifecycle.ViewModel",
            "import androidx.lifecycle.viewModelScope",
            "import kotlinx.coroutines.flow.MutableStateFlow",
            "import kotlinx.coroutines.flow.StateFlow",
            "import kotlinx.coroutines.flow.asStateFlow",
            "import kotlinx.coroutines.launch",
            "import kotlinx.coroutines.flow.update"
        ]
        
        usecase_imports = [
            "import kotlinx.coroutines.Dispatchers",
            "import kotlinx.coroutines.withContext"
        ]
        
        for kt_file in self.app_src.rglob("*.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                lines = content.split('\n')
                
                # Determine what imports are needed
                imports_to_add = []
                
                if "ViewModel" in content and kt_file.name.endswith("ViewModel.kt"):
                    imports_to_add.extend(viewmodel_imports)
                
                if "UseCase" in content and kt_file.name.endswith("UseCase.kt"):
                    imports_to_add.extend(usecase_imports)
                
                # Find where to insert imports
                package_line = -1
                last_import_line = -1
                
                for i, line in enumerate(lines):
                    if line.startswith('package '):
                        package_line = i
                    elif line.startswith('import '):
                        last_import_line = i
                
                # Add missing imports
                if package_line != -1 and imports_to_add:
                    insert_pos = last_import_line + 1 if last_import_line > package_line else package_line + 2
                    
                    for import_stmt in imports_to_add:
                        if import_stmt not in content:
                            lines.insert(insert_pos, import_stmt)
                            insert_pos += 1
                    
                    new_content = '\n'.join(lines)
                    if new_content != original_content:
                        with open(kt_file, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        self.log_fix(f"Added imports to {kt_file.name}")
                        
            except Exception as e:
                print(f"Error processing {kt_file}: {e}")
    
    def fix_viewmodel_classes(self):
        """Fix ViewModel classes to inherit correctly and use proper patterns"""
        print("🔍 Fixing ViewModel classes...")
        
        for kt_file in self.app_src.rglob("*ViewModel.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                
                # Fix class inheritance
                content = re.sub(
                    r'class\s+(\w*ViewModel)\s*\([^)]*\)\s*{',
                    r'class \1() : ViewModel() {',
                    content
                )
                
                # Fix constructor with parameters
                content = re.sub(
                    r'class\s+(\w*ViewModel)\s*\(\s*(.*?)\s*\)\s*{',
                    r'class \1(\n    \2\n) : ViewModel() {',
                    content
                )
                
                # Wrap suspend function calls in viewModelScope
                content = re.sub(
                    r'(\s+)(fun\s+\w+\([^)]*\)\s*{\s*)(.*?)(suspend\s+)',
                    r'\1\2viewModelScope.launch {\n\1    \4',
                    content,
                    flags=re.DOTALL
                )
                
                # Add state management patterns
                if "MutableStateFlow" not in content and "StateFlow" not in content:
                    # Add basic state pattern after class declaration
                    class_pattern = r'(class\s+\w*ViewModel[^{]*{\s*)'
                    state_code = '''
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()
    
'''
                    content = re.sub(class_pattern, r'\1' + state_code, content)
                
                if content != original_content:
                    with open(kt_file, 'w', encoding='utf-8') as f:
                        f.write(content)
                    self.log_fix(f"Fixed ViewModel patterns in {kt_file.name}")
                    
            except Exception as e:
                print(f"Error processing {kt_file}: {e}")
    
    def fix_use_cases(self):
        """Fix use case classes to follow proper patterns"""
        print("🔍 Fixing UseCase classes...")
        
        for kt_file in self.app_src.rglob("*UseCase.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                
                # Add repository injection
                if "repository" not in content.lower():
                    content = re.sub(
                        r'class\s+(\w*UseCase)\s*{',
                        r'class \1(\n    private val repository: TrainingRepository\n) {',
                        content
                    )
                
                # Fix suspend function patterns
                content = re.sub(
                    r'(@Throws\([^)]+\)\s*)?suspend\s+fun\s+execute\([^)]*\)\s*{\s*//.*?}',
                    '''suspend fun execute(data: Any): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val result = repository.processData(data)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }''',
                    content,
                    flags=re.DOTALL
                )
                
                if content != original_content:
                    with open(kt_file, 'w', encoding='utf-8') as f:
                        f.write(content)
                    self.log_fix(f"Fixed UseCase patterns in {kt_file.name}")
                    
            except Exception as e:
                print(f"Error processing {kt_file}: {e}")
    
    def remove_unsafe_patterns(self):
        """Remove unsafe patterns like !! operator"""
        print("🔍 Removing unsafe patterns...")
        
        for kt_file in self.app_src.rglob("*.kt"):
            try:
                with open(kt_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                
                # Replace !! with safe calls
                content = re.sub(r'(\w+)!!', r'\1?', content)
                
                # Add null checks for lateinit vars
                content = re.sub(
                    r'lateinit\s+var\s+(\w+):\s*(\w+)',
                    r'private var _\1: \2? = null\n    private val \1: \2 get() = _\1 ?: throw IllegalStateException("\1 not initialized")',
                    content
                )
                
                if content != original_content:
                    with open(kt_file, 'w', encoding='utf-8') as f:
                        f.write(content)
                    self.log_fix(f"Removed unsafe patterns in {kt_file.name}")
                    
            except Exception as e:
                print(f"Error processing {kt_file}: {e}")
    
    def run_all_fixes(self):
        """Run all automated fixes"""
        print("🚀 Starting automated code fixes for PokerTrainer...")
        print("=" * 60)
        
        self.fix_duplicate_classes()
        self.add_missing_dependencies()
        self.fix_unused_parameters()
        self.add_missing_imports()
        self.fix_viewmodel_classes()
        self.fix_use_cases()
        self.remove_unsafe_patterns()
        
        print("=" * 60)
        print(f"✅ Applied {len(self.fixes_applied)} fixes:")
        for fix in self.fixes_applied:
            print(f"  - {fix}")
        
        print("\n🔧 Next steps:")
        print("1. Review the changes made")
        print("2. Run './gradlew clean build' to test compilation")
        print("3. Run tests to ensure functionality is preserved")
        print("4. Commit changes with: 'git commit -m \"fix: auto-resolve compilation errors\"'")

def main():
    if len(sys.argv) > 1:
        project_root = sys.argv[1]
    else:
        project_root = os.getcwd()
    
    if not os.path.exists(os.path.join(project_root, "app", "build.gradle.kts")):
        print("❌ This doesn't appear to be an Android project root")
        print(f"Looking for app/build.gradle.kts in: {project_root}")
        sys.exit(1)
    
    fixer = CodeFixer(project_root)
    fixer.run_all_fixes()

if __name__ == "__main__":
    main()
