@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  dingo-bot startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and DINGO_BOT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\dingo-bot.jar;%APP_HOME%\lib\Discord4J-2.8.2.jar;%APP_HOME%\lib\junit-4.12.jar;%APP_HOME%\lib\slf4j-api-1.7.24.jar;%APP_HOME%\lib\httpcore-4.4.6.jar;%APP_HOME%\lib\httpclient-4.5.3.jar;%APP_HOME%\lib\httpmime-4.5.3.jar;%APP_HOME%\lib\websocket-client-9.4.3.v20170317.jar;%APP_HOME%\lib\typetools-0.4.8.jar;%APP_HOME%\lib\commons-lang3-3.5.jar;%APP_HOME%\lib\jackson-module-afterburner-2.8.7.jar;%APP_HOME%\lib\jna-4.3.0.jar;%APP_HOME%\lib\mp3spi-1.9.5-2.jar;%APP_HOME%\lib\jorbis-0.0.17.jar;%APP_HOME%\lib\jflac-1.3.jar;%APP_HOME%\lib\tritonus-share-0.3.7-3.jar;%APP_HOME%\lib\tritonus-dsp-0.3.6.jar;%APP_HOME%\lib\emoji-java-3.2.0.jar;%APP_HOME%\lib\koloboke-impl-common-jdk8-1.0.0.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\jetty-util-9.4.3.v20170317.jar;%APP_HOME%\lib\jetty-io-9.4.3.v20170317.jar;%APP_HOME%\lib\jetty-client-9.4.3.v20170317.jar;%APP_HOME%\lib\websocket-common-9.4.3.v20170317.jar;%APP_HOME%\lib\jackson-core-2.8.7.jar;%APP_HOME%\lib\jackson-databind-2.8.7.jar;%APP_HOME%\lib\jlayer-1.0.1-2.jar;%APP_HOME%\lib\json-20140107.jar;%APP_HOME%\lib\koloboke-api-jdk8-1.0.0.jar;%APP_HOME%\lib\jetty-http-9.4.3.v20170317.jar;%APP_HOME%\lib\websocket-api-9.4.3.v20170317.jar;%APP_HOME%\lib\jackson-annotations-2.8.0.jar;%APP_HOME%\lib\commons-io-2.5.jar

@rem Execute dingo-bot
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DINGO_BOT_OPTS%  -classpath "%CLASSPATH%" engine.DingoEngine %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DINGO_BOT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DINGO_BOT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
