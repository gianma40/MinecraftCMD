@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  RimuoviMetaInf.bat
rem
rem  Trascina un file .jar sopra questo .bat (oppure lancialo e
rem  digita il percorso quando richiesto): il jar verra' estratto
rem  in una cartella temporanea, la cartella META-INF/ al suo
rem  interno verra' cancellata, e tutto il resto verra' ricompattato
rem  in un nuovo jar chiamato "<nomeoriginale>-noMetaInf.jar" nella
rem  stessa cartella del jar di partenza. Il file originale NON
rem  viene toccato/cancellato.
rem
rem  Serve per quei jar il cui META-INF (es. un Main-Class nel
rem  manifest, o l'attributo Sealed) confonde il rilevamento della
rem  classe di avvio nel launcher.
rem ============================================================

set "JARFILE=%~1"

if "%JARFILE%"=="" (
    set /p JARFILE="Trascina qui il file .jar e premi invio (oppure scrivi il percorso completo): "
)

rem Toglie eventuali virgolette che Windows aggiunge col drag&drop
set "JARFILE=%JARFILE:"=%"

if not exist "%JARFILE%" (
    echo.
    echo File non trovato: "%JARFILE%"
    echo.
    pause
    exit /b 1
)

echo Cerco un jar/javac utilizzabile...

where jar >nul 2>nul
if %errorlevel%==0 (
    set "JARTOOL=jar"
    goto :found
)

if exist "C:\Program Files (x86)\Java\jdk1.8.0_202\bin\jar.exe" (
    set "JARTOOL=C:\Program Files (x86)\Java\jdk1.8.0_202\bin\jar.exe"
    goto :found
)

echo.
echo Non ho trovato jar.exe (lo strumento del JDK per gestire i file .jar^).
echo Installa il JDK con jdk-8u202-windows-i586.exe (incluso in questa cartella^)
echo oppure aggiungi il tuo jar.exe al PATH di sistema, poi rilancia questo file.
echo.
pause
exit /b 1

:found
echo Uso: %JARTOOL%
echo.

rem --- Prepara i percorsi ---
for %%F in ("%JARFILE%") do (
    set "JARDIR=%%~dpF"
    set "JARNAME=%%~nF"
)

set "TEMPDIR=%TEMP%\rimuovi_metainf_%RANDOM%"
set "OUTPUT=%JARDIR%%JARNAME%-noMetaInf.jar"

echo Jar di partenza : "%JARFILE%"
echo Cartella temp   : "%TEMPDIR%"
echo Jar di uscita    : "%OUTPUT%"
echo.

mkdir "%TEMPDIR%"
if errorlevel 1 (
    echo Impossibile creare la cartella temporanea.
    pause
    exit /b 1
)

echo Estraggo il jar...
pushd "%TEMPDIR%"
"%JARTOOL%" xf "%JARFILE%"
popd

if not exist "%TEMPDIR%\META-INF" (
    echo.
    echo Questo jar non contiene una cartella META-INF: niente da rimuovere.
    echo Copio comunque il contenuto cosi' com'e' in "%OUTPUT%".
) else (
    echo Rimuovo META-INF...
    rmdir /s /q "%TEMPDIR%\META-INF"
)

echo Ricompatto in "%OUTPUT%"...
pushd "%TEMPDIR%"
"%JARTOOL%" cf "%OUTPUT%" *
popd

echo Pulizia della cartella temporanea...
rmdir /s /q "%TEMPDIR%"

echo.
echo Fatto. Nuovo file creato:
echo   "%OUTPUT%"
echo Il jar originale non e' stato modificato.
echo.
pause
