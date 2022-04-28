#!/bin/sh

echo "The application will starting..."
exec unoserver & java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar "${HOME}/app.war" "$@"
