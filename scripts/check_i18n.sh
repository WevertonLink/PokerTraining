#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

violations=()

note() { echo -e "==> $*"; }
fail() { echo -e "❌ $*"; }
ok() { echo -e "✅ $*"; }

cd "$ROOT_DIR"

# Procurar strings hardcoded em Compose: Text("..."), Toast, AlertDialog, setTitle/setMessage
# Ignorar linhas anotadas com i18n-ignore e @Preview
note "Verificando strings hardcoded em arquivos Kotlin (Compose/UI)..."

compose_text_hits=$(grep -R --include='*.kt' -nE 'Text\("([^"\\]|\\.)+"\)' app/src || true)
compose_text_hits=$(echo "$compose_text_hits" | grep -vE 'i18n-ignore|@Preview' || true)
[[ -n "${compose_text_hits}" ]] && violations+=("Compose Text hardcoded:
${compose_text_hits}")

toast_hits=$(grep -R --include='*.kt' -nE 'Toast\.makeText\([^,]+,\s*"([^"\\]|\\.)+"' app/src || true)
toast_hits=$(echo "$toast_hits" | grep -vE 'i18n-ignore|@Preview' || true)
[[ -n "${toast_hits}" ]] && violations+=("Toast hardcoded:
${toast_hits}")

dialog_hits=$(grep -R --include='*.kt' -nE 'setTitle\("([^"\\]|\\.)+"\)|setMessage\("([^"\\]|\\.)+"\)' app/src || true)
dialog_hits=$(echo "$dialog_hits" | grep -vE 'i18n-ignore|@Preview' || true)
[[ -n "${dialog_hits}" ]] && violations+=("Alert/Dialog hardcoded:
${dialog_hits}")

# Sugerir uso de stringResource(R.string.*) em Compose
suggest_hits=$(grep -R --include='*.kt' -nE 'Text\([^)]*R\.string\.' app/src || true)
# isso não é violação, mas ajuda a depurar:
[[ -n "${suggest_hits}" ]] && note "Referências a R.string detectadas (ok): $(echo "$suggest_hits" | wc -l)"

if (( ${#violations[@]} > 0 )); then
  fail "Foram encontradas strings hardcoded. Converta para stringResource(R.string.*) ou adicione a strings.xml."
  echo "----- DETALHES -----"
  for v in "${violations[@]}"; do
    echo "$v"
    echo
  done
  echo "Para ignorar conscientemente um caso específico, adicione o comentário // i18n-ignore na linha."
  exit 1
else
  ok "Nenhuma string hardcoded encontrada nas verificações."
fi