#!/bin/bash

# Dependencies
mvn install:install-file \
   -Dfile=${ROOT_DIR}/lib/jflex-1.8.2/lib/jflex-full-1.8.2.jar \
   -DgroupId=de.jflex \
   -DartifactId=jflex \
   -Dversion=1.8.2 \
   -Dpackaging=jar \
   -DgeneratePom=true
# Create jar
mvn package -Dmaven.repo.local=${ROOT_DIR}/lib package

zip -r source-$(uuidgen).zip lib src pom.xml xic-build
