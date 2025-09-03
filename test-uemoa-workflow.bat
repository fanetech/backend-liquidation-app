@echo off
echo ========================================
echo Test du Workflow UEMOA QR Code
echo ========================================
echo.

echo 1. Verification de la sante du workflow...
curl -s http://localhost:8080/api/uemoa-workflow/health
echo.
echo.

echo 2. Verification du statut du workflow...
curl -s http://localhost:8080/api/uemoa-workflow/status
echo.
echo.

echo 3. Generation d'un QR code statique...
curl -s -X POST http://localhost:8080/api/uemoa-workflow/generate ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\": 5000, \"clientInfo\": \"123\", \"qrType\": \"STATIC\"}"
echo.
echo.

echo 4. Generation d'un QR code dynamique...
curl -s -X POST http://localhost:8080/api/uemoa-workflow/generate ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\": 10000, \"clientInfo\": \"456\", \"qrType\": \"DYNAMIC\", \"transactionReference\": \"TXN-001\"}"
echo.
echo.

echo 5. Test de validation - montant invalide...
curl -s -X POST http://localhost:8080/api/uemoa-workflow/generate ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\": 0, \"clientInfo\": \"123\"}"
echo.
echo.

echo ========================================
echo Tests termines
echo ========================================
pause
