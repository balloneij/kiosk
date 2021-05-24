@echo off
setlocal ENABLEEXTENSIONS
set KEY_NAME="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
::
:: get the current version
::
FOR /F "usebackq skip=2 tokens=3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
    set ValueValue=%%A
)
if defined ValueValue (
    if "%ValueValue%" == "1.8" (
        start java -jar kiosk.jar
        goto end
    ) else (
        @echo The current Java runtime is  %ValueValue%. Please install Java 1.8.
        pause
        goto end
    )
) else (
    @echo %KEY_NAME%\%VALUE_NAME% not found.
    pause
    goto end
)
:end
