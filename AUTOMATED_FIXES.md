# 🤖 Automated Code Quality & Fix System

This repository includes automated GitHub Actions workflows to detect and fix common coding issues in the PokerTrainer Android project.

## 🚀 Available Workflows

### 1. Code Quality & Auto-Fix (`code-quality.yml`)
**Triggered on:** Push to main/develop, Pull Requests, Manual dispatch

**What it does:**
- ✅ Analyzes compilation errors
- ✅ Detects unused parameters
- ✅ Identifies missing imports
- ✅ Automatically fixes common issues
- ✅ Applies code formatting (ktlint)
- ✅ Commits fixes back to the repository

### 2. Manual Code Fix Application (`manual-fix.yml`)
**Triggered on:** Manual dispatch only

**What it does:**
- 🔧 Runs comprehensive automated fixes
- 🧪 Tests compilation after fixes
- 📊 Generates detailed fix reports
- 📝 Creates GitHub issues with results

### 3. Dependency Vulnerability Scan (`dependency-scan.yml`)
**Triggered on:** Weekly schedule (Mondays), Manual dispatch

**What it does:**
- 🔍 Scans for vulnerable dependencies
- 📋 Generates security reports
- ⚠️ Alerts on critical vulnerabilities

### 4. Performance & Memory Analysis (`performance-analysis.yml`)
**Triggered on:** Push to main (when Kotlin files change), Manual dispatch

**What it does:**
- 🏃 Analyzes performance patterns
- 🧠 Detects potential memory leaks
- ⚡ Identifies inefficient code patterns
- 🔄 Checks coroutine usage

## 🛠️ Fixed Issues

The automated system addresses the following compilation and code quality issues:

### ✅ Compilation Errors Fixed

1. **Redeclaration of Classes**
   - Detects duplicate class definitions
   - Provides guidance on resolution

2. **Missing Dependencies**
   - Automatically adds required coroutines dependencies
   - Updates `build.gradle.kts` with lifecycle components

3. **Unused Parameters**
   - Prefixes unused parameters with underscore
   - Removes parameters that are truly unnecessary

4. **Missing Imports**
   - Adds required ViewModel imports
   - Includes coroutine-related imports
   - Adds lifecycle and flow imports

### ✅ Code Pattern Fixes

1. **ViewModel Classes**
   ```kotlin
   // Before
   class MyViewModel {
       fun doSomething() { ... }
   }
   
   // After
   class MyViewModel() : ViewModel() {
       private val _state = MutableStateFlow(UiState())
       val state: StateFlow<UiState> = _state.asStateFlow()
       
       fun doSomething() {
           viewModelScope.launch {
               // Safe coroutine execution
           }
       }
   }
   ```

2. **Use Cases**
   ```kotlin
   // Before
   class MyUseCase {
       suspend fun execute() { ... }
   }
   
   // After
   class MyUseCase(
       private val repository: TrainingRepository
   ) {
       suspend fun execute(data: Any): Result<Any> = withContext(Dispatchers.IO) {
           try {
               val result = repository.processData(data)
               Result.success(result)
           } catch (e: Exception) {
               Result.failure(e)
           }
       }
   }
   ```

3. **Safety Improvements**
   - Removes `!!` operators
   - Replaces with safe calls (`?.`)
   - Adds proper null checks
   - Implements safe lateinit patterns

## 🎯 How to Use

### Option 1: Automatic (Recommended)
1. Push code to `main` or `develop` branch
2. The system automatically detects and fixes issues
3. Review the automated commit with fixes
4. Merge if everything looks good

### Option 2: Manual Trigger
1. Go to **Actions** tab in GitHub
2. Select **Manual Code Fix Application**
3. Click **Run workflow**
4. Choose options:
   - ✅ Apply automatic fixes
   - ✅ Run tests after fixes
5. Review the generated issue with results

### Option 3: Local Execution
```bash
# Run the auto-fix script locally
python3 scripts/auto_fix.py .

# Apply formatting
./gradlew ktlintFormat

# Test compilation
./gradlew clean compileDebugKotlin

# Commit changes
git add -A
git commit -m "fix: apply automated code quality fixes"
git push
```

## 📊 Dependencies Added

The following dependencies are automatically added to fix compilation issues:

```kotlin
// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
```

## 🔒 Safety Guidelines

The automated fixes follow these safety principles:

- **Never use `!!` operator** → Replaced with safe calls
- **Always treat nulls safely** → Use `?.` and proper checks
- **Validate arguments** → Add `require()` statements
- **Use proper coroutine scopes** → `viewModelScope` for ViewModels
- **Handle errors gracefully** → Wrap in try-catch with Result types

## 📋 Monitoring & Reports

### Artifacts Generated
- **Code Analysis Results** - Compilation errors, unused parameters
- **Security Scan Results** - Vulnerability reports
- **Performance Analysis** - Memory and performance issues
- **Fix Reports** - Summary of applied changes

### GitHub Issues
The system automatically creates GitHub issues with:
- 📊 Summary of fixes applied
- 🔍 Compilation status
- 📝 Next steps for developers
- 🔗 Links to workflow runs

## 🚨 Troubleshooting

### Build Still Failing?
1. Check the workflow logs in GitHub Actions
2. Look for specific error messages in the artifacts
3. Run the fix script locally for more detailed output
4. Create a manual issue if automated fixes don't resolve the problem

### False Positives?
1. Review the changes in the automated commit
2. Revert specific changes if needed
3. Add exclusions to the fix script for your use case

### Need Custom Fixes?
1. Modify `scripts/auto_fix.py` for project-specific patterns
2. Update the workflow files in `.github/workflows/`
3. Test changes locally before committing

## 🤝 Contributing

To improve the automated fix system:

1. **Add new fix patterns** in `scripts/auto_fix.py`
2. **Update workflows** in `.github/workflows/`
3. **Test thoroughly** with various code patterns
4. **Document changes** in this README

## 📞 Support

If you encounter issues with the automated fix system:

1. Check existing GitHub issues
2. Review workflow run logs
3. Create a new issue with:
   - Error messages
   - Code samples that failed
   - Expected vs actual behavior

---

*This automated system helps maintain code quality and ensures the PokerTrainer project compiles successfully with safe coding practices.*
