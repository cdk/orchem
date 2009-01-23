#!/bin/sh

# WARNING: This file is created by the Configuration Wizard.
# Any changes to this script may be lost when adding extensions to this configuration.

# --- Start Functions ---

stopAll()
{
	# We separate the stop commands into a function so we are able to use the trap command in Unix (calling a function) to stop these services
	if [ "X${ALREADY_STOPPED}" != "X" ] ; then
		exit
	fi
	# STOP POINTBASE (only if we started it)
	if [ "${POINTBASE_FLAG}" = "true" ] ; then
		echo "Stopping PointBase server..."
		${WL_HOME}/common/bin/stopPointBase.sh -port=${POINTBASE_PORT} -name=${POINTBASE_DBNAME}  >"${DOMAIN_HOME}/pointbaseShutdown.log" 2>&1 

		echo "PointBase server stopped."
	fi

	ALREADY_STOPPED="true"
	# Restore IP configuration the node manager starts IP Migration
	if [ "${SERVER_IP}" != "" ] ; then
		${WL_HOME}/common/bin/wlsifconfig.sh -removeif "${IFNAME}" "${SERVER_IP}"
	fi
}

# --- End Functions ---

# *************************************************************************
# This script is used to start WebLogic Server for this domain.
# 
# To create your own start script for your domain, you can initialize the
# environment by calling @USERDOMAINHOME/setDomainEnv.
# 
# setDomainEnv initializes or calls commEnv to initialize the following variables:
# 
# BEA_HOME       - The BEA home directory of your WebLogic installation.
# JAVA_HOME      - Location of the version of Java used to start WebLogic
#                  Server.
# JAVA_VENDOR    - Vendor of the JVM (i.e. BEA, HP, IBM, Sun, etc.)
# PATH           - JDK and WebLogic directories are added to system path.
# WEBLOGIC_CLASSPATH
#                - Classpath needed to start WebLogic Server.
# PATCH_CLASSPATH - Classpath used for patches
# PATCH_LIBPATH  - Library path used for patches
# PATCH_PATH     - Path used for patches
# WEBLOGIC_EXTENSION_DIRS - Extension dirs for WebLogic classpath patch
# JAVA_VM        - The java arg specifying the VM to run.  (i.e.
#                - server, -hotspot, etc.)
# USER_MEM_ARGS  - The variable to override the standard memory arguments
#                  passed to java.
# PRODUCTION_MODE - The variable that determines whether Weblogic Server is started in production mode.
# POINTBASE_HOME - Point Base home directory.
# POINTBASE_CLASSPATH
#                - Classpath needed to start PointBase.
# 
# Other variables used in this script include:
# SERVER_NAME    - Name of the weblogic server.
# JAVA_OPTIONS   - Java command-line options for running the server. (These
#                  will be tagged on to the end of the JAVA_VM and
#                  MEM_ARGS)
# 
# For additional information, refer to the WebLogic Server Administration
# Console Online Help(http://e-docs.bea.com/wls/docs103/ConsoleHelp/startstop.html).
# *************************************************************************

# Call setDomainEnv here.

DOMAIN_HOME="/home/markr/.jdeveloper/system11.1.1.0.31.51.88/DefaultDomain"

. ${DOMAIN_HOME}/bin/setDomainEnv.sh $*

SAVE_JAVA_OPTIONS="${JAVA_OPTIONS}"

SAVE_CLASSPATH="${CLASSPATH}"

# Start PointBase

PB_DEBUG_LEVEL="0"

if [ "${POINTBASE_FLAG}" = "true" ] ; then
	${WL_HOME}/common/bin/startPointBase.sh -port=${POINTBASE_PORT} -debug=${PB_DEBUG_LEVEL} -console=false -background=true -ini=${DOMAIN_HOME}/pointbase.ini  >"${DOMAIN_HOME}/pointbase.log" 2>&1 

fi

JAVA_OPTIONS="${SAVE_JAVA_OPTIONS}"

SAVE_JAVA_OPTIONS=""

CLASSPATH="${SAVE_CLASSPATH}"

SAVE_CLASSPATH=""

trap 'stopAll' 1 2 3 15


if [ "${PRODUCTION_MODE}" = "true" ] ; then
	WLS_DISPLAY_MODE="Production"
else
	WLS_DISPLAY_MODE="Development"
fi

if [ "${WLS_USER}" != "" ] ; then
	JAVA_OPTIONS="${JAVA_OPTIONS} -Dweblogic.management.username=${WLS_USER}"
fi

if [ "${WLS_PW}" != "" ] ; then
	JAVA_OPTIONS="${JAVA_OPTIONS} -Dweblogic.management.password=${WLS_PW}"
fi

CLASSPATH="${CLASSPATH}${CLASSPATHSEP}${MEDREC_WEBLOGIC_CLASSPATH}"
CLASSPATH="${CLASSPATH}/home/markr/oracle/Middleware/wlserver_10.3/lib/log4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/xwork-2.0.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts-core-1.3.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/xml-apis-1.0.b2.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/tiles-jsp-2.0.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/tiles-core-2.0.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/tiles-api-2.0.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-tiles-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-struts1-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-spring-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-sitemesh-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-sitegraph-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-plexus-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-pell-multipart-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-jsf-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-jfreechart-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-jasperreports-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-core-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-config-browser-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/struts2-codebehind-plugin-2.0.14.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/spring-web-2.0.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/spring-core-2.0.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/spring-context-2.0.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/spring-beans-2.0.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/sitemesh-2.2.1.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/plexus-utils-1.2.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/plexus-container-default-1.0-alpha-10.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/oro-2.0.8.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/ognl-2.6.11.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/freemarker-2.3.8.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-validator-1.3.0.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-logging-api-1.1.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-logging-1.0.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-digester-1.8.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-collections-2.1.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-chain-1.1.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/commons-beanutils-1.7.0.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/classworlds-1.1.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/aopalliance-1.0.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/struts2/antlr-2.7.2.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/velocity/velocity-tools-1.4/lib/velocity-1.5.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/velocity/velocity-tools-1.4/lib/velocity-dvsl-1.0.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/velocity/velocity-tools-1.4/lib/velocity-tools-1.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/velocity/velocity-tools-1.4/lib/velocity-tools-generic-1.4.jar"
CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/velocity/velocity-tools-1.4/lib/velocity-tools-view-1.4.jar"
#CLASSPATH="${CLASSPATH}:/home/markr/oracle/Middleware/wlserver_10.3/lib/orchem.jar"


echo "."

echo "."

echo "JAVA Memory arguments: ${MEM_ARGS}"

echo "."

echo "WLS Start Mode=${WLS_DISPLAY_MODE}"

echo "."

echo "CLASSPATH=${CLASSPATH}"

echo "."

echo "PATH=${PATH}"

echo "."

echo "***************************************************"

echo "*  To start WebLogic Server, use a username and   *"

echo "*  password assigned to an admin-level user.  For *"

echo "*  server administration, use the WebLogic Server *"

echo "*  console at http://hostname:port/console        *"

echo "***************************************************"

# Set up IP Migration related variables.

# Set interface name.

if [ "${Interface}" != "" ] ; then
	IFNAME="${Interface}"
else
	IFNAME=""
fi

# Set IP Mask.

if [ "${NetMask}" != "" ] ; then
	IPMASK="${NetMask}"
else
	IPMASK=""
fi

# Perform IP Migration if SERVER_IP is set by node manager.

if [ "${SERVER_IP}" != "" ] ; then
	${WL_HOME}/common/bin/wlsifconfig.sh -addif "${IFNAME}" "${SERVER_IP}" "${IPMASK}"
fi

# START WEBLOGIC

echo "starting weblogic with Java version:"

${JAVA_HOME}/bin/java ${JAVA_VM} -version

if [ "${WLS_REDIRECT_LOG}" = "" ] ; then
	echo "Starting WLS with line:"
	echo "${JAVA_HOME}/bin/java ${JAVA_VM} ${MEM_ARGS} ${JAVA_OPTIONS} -Dweblogic.Name=${SERVER_NAME} -Djava.security.policy=${WL_HOME}/server/lib/weblogic.policy  ${PROXY_SETTINGS} ${SERVER_CLASS}"
	${JAVA_HOME}/bin/java ${JAVA_VM} ${MEM_ARGS} ${JAVA_OPTIONS} -Dweblogic.Name=${SERVER_NAME} -Djava.security.policy=${WL_HOME}/server/lib/weblogic.policy ${PROXY_SETTINGS} ${SERVER_CLASS}
else
	echo "Redirecting output from WLS window to ${WLS_REDIRECT_LOG}"
	${JAVA_HOME}/bin/java ${JAVA_VM} ${MEM_ARGS} ${JAVA_OPTIONS} -Dweblogic.Name=${SERVER_NAME} -Djava.security.policy=${WL_HOME}/server/lib/weblogic.policy ${PROXY_SETTINGS} ${SERVER_CLASS}  >"${WLS_REDIRECT_LOG}" 2>&1 
fi

stopAll

popd

# Exit this script only if we have been told to exit.

if [ "${doExitFlag}" = "true" ] ; then
	exit
fi

