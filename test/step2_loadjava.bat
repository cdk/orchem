
call init.bat %4

echo ___________________________________
echo
echo "DROPJAVA..(ignore warnings)"
echo

set DBUSER=%1
set DBPASS=%2
set DBINST=%3

call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\vecmath.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\commons-collections-3.2.1.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jgrapht-0.6.0.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\log4j-1.2.15.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\cdk-1.1.2.ora11proof.fp.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% c:\orchem.jar


echo
echo "LOADJAVA"
echo
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\vecmath.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\commons-collections-3.2.1.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jgrapht-0.6.0.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\log4j-1.2.15.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\cdk-1.1.2.ora11proof.fp.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% c:\orchem.jar

del c:\temp\orchem.jar