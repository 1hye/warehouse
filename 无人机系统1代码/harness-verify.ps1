$ErrorActionPreference = 'Stop'

$errors = 0
$checks = 0

function Check-File {
  param([string]$Path)
  $script:checks++
  if (-not (Test-Path -Path $Path -PathType Leaf)) {
    Write-Host "❌ Missing file: $Path" -ForegroundColor Red
    $script:errors++
  } else {
    Write-Host "✅ $Path" -ForegroundColor Green
  }
}

function Check-Dir {
  param([string]$Path)
  $script:checks++
  if (-not (Test-Path -Path $Path -PathType Container)) {
    Write-Host "❌ Missing directory: $Path" -ForegroundColor Red
    $script:errors++
  } else {
    Write-Host "✅ $Path/" -ForegroundColor Green
  }
}

Write-Host ""
Write-Host "=================================================="
Write-Host "  Harness Template Verification (PowerShell)"
Write-Host "=================================================="
Write-Host ""

# 1. Trae rules (primary, current IDE)
Write-Host "[1/7] Trae Rules" -ForegroundColor Yellow
Check-File ".trae/rules/java-engineering-standards.md"
Check-File ".trae/rules/testing-quality-standards.md"
Check-File ".trae/rules/api-doc-sync-protocol.md"
Check-File ".trae/rules/ai-collaboration-protocol.md"
Check-File ".trae/rules/project-lifecycle.md"
Write-Host ""

# 2. Cursor rules (legacy, backward compat)
Write-Host "[2/7] Cursor Rules (Legacy)" -ForegroundColor Yellow
Check-File ".cursor/rules/java-engineering-standards.mdc"
Check-File ".cursor/rules/testing-quality-standards.mdc"
Check-File ".cursor/rules/api-doc-sync-protocol.mdc"
Check-File ".cursor/rules/ai-collaboration-protocol.mdc"
Check-File ".cursor/rules/project-lifecycle.mdc"
Write-Host ""

# 3. Maven config
Write-Host "[3/7] Maven Build Config" -ForegroundColor Yellow
Check-File "pom.xml"
Check-File "config/checkstyle/checkstyle.xml"
Check-File "config/checkstyle/checkstyle-strict.xml"
Check-File "config/spotbugs/exclude.xml"
Write-Host ""

# 4. CI config
Write-Host "[4/7] CI Config" -ForegroundColor Yellow
Check-File ".github/workflows/ci-verify.yml"
Write-Host ""

# 5. harness-collab docs
Write-Host "[5/7] harness-collab Docs" -ForegroundColor Yellow
Check-File "harness-collab/README.md"
Check-File "harness-collab/AGENTS.md"
Check-File "harness-collab/func.md"
Check-Dir "harness-collab/01-product-specs/templates"
Check-File "harness-collab/01-product-specs/templates/product-spec-template.md"
Check-Dir "harness-collab/02-design-docs/templates"
Check-File "harness-collab/02-design-docs/templates/design-doc-template.md"
Check-Dir "harness-collab/03-exec-plans/templates"
Check-File "harness-collab/03-exec-plans/templates/exec-plan-template.md"
Check-Dir "harness-collab/04-api-docs/templates"
Check-File "harness-collab/04-api-docs/templates/api-doc-template.md"
Check-File "harness-collab/05-methodology/architecture-constraints.md"
Check-File "harness-collab/05-methodology/dev-workflow.md"
Check-File "harness-collab/05-methodology/ai-delivery-playbook.md"
Check-File "harness-collab/06-adapters/bootstrap-guide.md"
Check-File "harness-collab/06-adapters/retrofit-guide.md"
Write-Host ""

# 6. root docs
Write-Host "[6/7] Root Docs" -ForegroundColor Yellow
Check-File "README.md"
Check-File "AGENTS.md"
Write-Host ""

# 7. Rule frontmatter verification
Write-Host "[7/7] Rules Frontmatter (alwaysApply: true)" -ForegroundColor Yellow
$rulesCount = 0

# Check Trae rules (primary)
$traeRuleFiles = Get-ChildItem ".trae/rules/*.md" -ErrorAction SilentlyContinue
foreach ($f in $traeRuleFiles) {
  $checks++
  $raw = Get-Content $f.FullName -Raw
  if ($raw -match 'alwaysApply:\s*true') {
    Write-Host "✅ $($f.FullName) contains alwaysApply: true" -ForegroundColor Green
    $rulesCount++
  } else {
    Write-Host "❌ $($f.FullName) missing alwaysApply: true" -ForegroundColor Red
    $errors++
  }
}

# Also check Cursor rules (legacy)
$cursorRuleFiles = Get-ChildItem ".cursor/rules/*.mdc" -ErrorAction SilentlyContinue
foreach ($f in $cursorRuleFiles) {
  $checks++
  $raw = Get-Content $f.FullName -Raw
  if ($raw -match 'alwaysApply:\s*true') {
    Write-Host "✅ $($f.FullName) contains alwaysApply: true" -ForegroundColor Green
    $rulesCount++
  } else {
    Write-Host "❌ $($f.FullName) missing alwaysApply: true" -ForegroundColor Red
    $errors++
  }
}
Write-Host ""

Write-Host "=================================================="
if ($errors -eq 0) {
  Write-Host "✅ Verification passed! Completed $checks checks." -ForegroundColor Green
  Write-Host "   Rules count: $rulesCount (all include alwaysApply: true)" -ForegroundColor Green
  Write-Host ""
  Write-Host "Harness template is ready to use in Trae IDE."
  exit 0
}

Write-Host "❌ Verification failed! Completed $checks checks, found $errors issues." -ForegroundColor Red
Write-Host "Please fix missing files/directories and run again."
exit 1