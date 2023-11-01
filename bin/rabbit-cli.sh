#!/bin/bash
declare _java;
# find executable java
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    printf 'pick java from JAVA_HOME env variable';
    _java="$JAVA_HOME/bin/java"
  elif  type -p java &>/dev/null ; then
    _java=java
  else
    printf "no jdk found!"
    exit 1;
fi
# check java version
if  $_java -version &>/dev/null ; then
    java_version=$($_java -version |& awk -F '"' '/version/ {print $2}')
    printf 'java version is %s \n' "$java_version"
    major_version=$(cut -d. -f1 <<< "$java_version")
    if [ "$major_version" -lt 11 ]; then
        printf 'rabbit-cli needs java version 11 or above!'
        exit 1;
    fi
fi
abs=$(dirname $(realpath "$0"))
jar_abs=$(realpath "$abs/../src/"rabbit-client-?.?.?.jar)
$_java -jar "$jar_abs" "$@"