@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo Cerco un javac utilizzabile...

where javac >nul 2>nul
if %errorlevel%==0 (
    set "JAVAC=javac"
    goto :found
)

if exist "C:\Program Files (x86)\Java\jdk1.8.0_202\bin\javac.exe" (
    set "JAVAC=C:\Program Files (x86)\Java\jdk1.8.0_202\bin\javac.exe"
    goto :found
)

echo.
echo Non ho trovato javac (il compilatore Java^).
echo Installa il JDK con jdk-8u202-windows-i586.exe (incluso in questa cartella^)
echo oppure aggiungi il tuo javac al PATH di sistema, poi rilancia questo file.
echo.
pause
exit /b 1

:found
echo Uso: %JAVAC%
echo.
"%JAVAC%" -encoding UTF-8 FakeAppletStub.java
"%JAVAC%" -encoding UTF-8 MinecraftAppletWrapper.java
"%JAVAC%" -encoding UTF-8 MinecraftCMDLauncherGUI.java

if %errorlevel%==0 (
    echo.
    echo Compilazione completata con successo.
    echo Ora puoi avviare il launcher con bat\Minecraft-Launcher.bat
) else (
    echo.
    echo Errore durante la compilazione, controlla i messaggi sopra.
)

echo.
pause
