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

# declare help function
function usage() {
    echo "Usage: $0 <command> <options>"
    echo "supported commands: {publish|produce,consume}"
    echo "e.g. : $0 consume -q queueName"
    exit "$1"
}
# user wants help
getopts ":h" opt
if [ "$opt" == "h" ]; then
    usage 0
fi

# Parse the command and arguments
COMMAND="$1"
shift

while getopts ":q:e:r:p:" opt; do
    case "$opt" in
        q)
            echo "set the queue name to: $OPTARG"
            queue=${OPTARG}
            ;;
        e)
            echo "set the exchange name(used only for publish) to: $OPTARG"
            exchange=${OPTARG}
            ;;
        r)
            echo "set the routing-key(used only for publish) to: $OPTARG "
            routingKey=${OPTARG}
            ;;
        p)
            echo "set the payload(used only for publish) to: $OPTARG"
            payload=${OPTARG}
            ;;
        *)
            usage 1
            ;;
    esac
done

# Find the jar absolute path
abs=$(dirname "$(realpath "$0")")
jar_abs=$(realpath "$abs/../src/"rabbit-client-?.?.?.jar)
log_abs=$(realpath "$abs/../log/")

case "$COMMAND" in
    "consume")
        if [ -n "$queue" ]; then
          $_java -Dlog.file.path="$log_abs" -Dapplication.mode=consumer -Damqp.queue.listener="$queue" -jar "$jar_abs"
        else
          $_java -Dlog.file.path="$log_abs" -Dapplication.mode=consumer -jar "$jar_abs"
        fi
        ;;
    "publish" | "produce")
        $_java -Dlog.file.path="$log_abs" -Dapplication.mode=producer -jar "$jar_abs" ${queue:+--queue=$queue} --exchange="$exchange" ${routingKey:+--routing-key=$routingKey} "$payload"
        ;;
    *)
        echo "Invalid command${COMMAND:+: $COMMAND}"
        usage 1
        ;;
esac