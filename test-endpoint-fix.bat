@echo off
echo ========================================
echo Test de l'endpoint client-info corrige
echo ========================================
echo.

echo 1. Demarrage de l'application...
start "Spring Boot App" mvn spring-boot:run

echo 2. Attente du demarrage (15 secondes)...
timeout /t 15 /nobreak > nul

echo 3. Test de l'endpoint de sante...
curl -X GET "http://localhost:8080/api/uemoa-workflow/health" -H "Content-Type: application/json"
echo.
echo.

echo 4. Test de generation QR code...
curl -X POST "http://localhost:8080/api/uemoa-workflow/generate" ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\": 5000, \"merchantName\": \"TEST MERCHANT\", \"clientInfo\": \"123\", \"qrType\": \"STATIC\"}"
echo.
echo.

echo 5. Test de l'endpoint client-info (avec un ID client valide)...
curl -X GET "http://localhost:8080/api/uemoa-workflow/client-info/MTIzOjUwMDA6MjAyNS0wOS0wMlQxNzo0MzowMi44Njg0MTg0MDA=" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 6. Test avec un ID client inexistant...
curl -X GET "http://localhost:8080/api/uemoa-workflow/client-info/INVALID_ID" ^
  -H "Content-Type: application/json"
echo.
echo.

echo ========================================
echo Tests termines
echo ========================================
echo.
echo Appuyez sur une touche pour fermer l'application...
pause > nul

echo Fermeture de l'application...
taskkill /F /IM java.exe > nul 2>&1
echo Application fermee.

