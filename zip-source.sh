#!/bin/bash

ROOT_DIR=`dirname "$0"`

# Dependencies
mvn install:install-file \
   -Dfile=${ROOT_DIR}/lib/jflex-1.8.2/lib/jflex-full-1.8.2.jar \
   -DgroupId=de.jflex \
   -DartifactId=jflex \
   -Dversion=1.8.2 \
   -Dpackaging=jar \
   -DgeneratePom=true \
   -DlocalRepositoryPath=${ROOT_DIR}/lib
mvn install:install-file \
   -Dfile=${ROOT_DIR}/lib/java_cup.jar \
   -DgroupId=cup \
   -DartifactId=cup \
   -Dversion=1.0 \
   -Dpackaging=jar \
   -DgeneratePom=true \
   -DlocalRepositoryPath=${ROOT_DIR}/lib

# Create jar
mvn package -Dmaven.repo.local=${ROOT_DIR}/lib

zip -r source-$(uuidgen).zip lib src pom.xml xic-build cup
