
set BASEDIR=%1
set JARDIR=%BASEDIR%public_html\WEB-INF\lib
set CLASSDIR=%BASEDIR%public_html\WEB-INF\classes
set SRCDIR=%BASEDIR%src

set CLASSPATH=%CLASSDIR%
set CLASSPATH=%CLASSPATH%;%BASEDIR%\public_html\WEB-INF\lib\cdk.jar
set CLASSPATH=%CLASSPATH%;%BASEDIR%\public_html\WEB-INF\lib\ojdbc5.jar
set CLASSPATH=%CLASSPATH%;%BASEDIR%\public_html\WEB-INF\lib\vecmath.jar

ant -buildfile %BASEDIR%\build.xml compile

