
call init.bat %4

echo ___________________________________
echo
echo "DROPJAVA..(ignore warnings)"
echo

set DBUSER=%1
set DBPASS=%2
set DBINST=%3



call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\vecmath.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jama-1.0.2.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\commons-collections-3.2.1.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jgrapht-0.6.0.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\log4j-1.2.15.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\nestedVm.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\inchi_102.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\cdk.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jcp.jar
call dropjava -nowarn -user %DBUSER%/%DBPASS%@%DBINST% c:\orchem.jar


echo
echo "LOADJAVA"
echo

call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\vecmath.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jama-1.0.2.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\commons-collections-3.2.1.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jgrapht-0.6.0.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\log4j-1.2.15.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\nestedVm.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\inchi_102.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\cdk.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% %JARDIR%\jcp.jar
call loadjava   -verbose -user %DBUSER%/%DBPASS%@%DBINST% c:\orchem.jar

