@echo off
echo ========================================
echo    TESTS D'INTEGRATION UEMOA QR CODE
echo ========================================
echo.

echo [1/5] Compilation du projet...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)
echo ✅ Compilation réussie
echo.

echo [2/5] Tests unitaires UEMOA...
call mvn test -Dtest=UemoaQRModuleTest
if %errorlevel% neq 0 (
    echo ❌ Erreur dans les tests unitaires
    pause
    exit /b 1
)
echo ✅ Tests unitaires réussis
echo.

echo [3/5] Démarrage de l'application...
start /B mvn spring-boot:run
echo ⏳ Attente du démarrage de l'application...
timeout /t 15 /nobreak > nul

echo [4/5] Test de l'endpoint de santé...
curl -X GET http://localhost:8080/api/uemoa-qr/health
if %errorlevel% neq 0 (
    echo ❌ L'application n'est pas accessible
    echo Vérifiez que l'application est démarrée sur le port 8080
    pause
    exit /b 1
)
echo ✅ Endpoint de santé accessible
echo.

echo [5/5] Test de génération de QR code...
curl -X POST http://localhost:8080/api/uemoa-qr/generate-static ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\": 1000, \"merchantName\": \"TEST MERCHANT\"}"
if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la génération de QR code
    pause
    exit /b 1
)
echo ✅ Génération de QR code réussie
echo.

echo ========================================
echo    ✅ INTEGRATION UEMOA VALIDEE
echo ========================================
echo.
echo Résumé des tests :
echo - ✅ Compilation réussie
echo - ✅ Tests unitaires passés
echo - ✅ Application démarrée
echo - ✅ Endpoints accessibles
echo - ✅ Génération de QR codes fonctionnelle
echo.
echo L'application est en cours d'exécution sur http://localhost:8080
echo Appuyez sur une touche pour arrêter l'application...
pause

echo Arrêt de l'application...
taskkill /F /IM java.exe > nul 2>&1
echo ✅ Application arrêtée
