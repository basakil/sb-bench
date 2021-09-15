#!/bin/sh

## This script checks/iterates comma seperated CHECK_SERVICE_ENDPOINT_LIST, if not empty,
## then executes the final (jib) service if all endpoints in it return "true".

## formats and prints debug string to stdout..
debugp() {
  echo "$(date +%FT%H:%M:%S) $1"
}

debugp "starting service-aware-entrypoint."

## returns true if $1 begins with $2, false otherwise:
beginswith() {
  case $2 in "$1"*) true;; *) false;; esac;
}

## Do not do any checks if env variable is not set or empty.
if [ ! -z "$CHECK_SERVICE_ENDPOINT_LIST" ] ; then

 SLEEP_SECONDS=${CHECK_SERVICE_SLEEP_INTERVAL:-10}
 REQUEST_TIMEOUT_SECONDS=${CHECK_SERVICE_TIMEOUT_SECONDS:-5}
 debugp "CHECK_SERVICE_ENDPOINT_LIST=$CHECK_SERVICE_ENDPOINT_LIST . Processing service list before continueing..."

 ## iterate untill all endpoints succeed
 while sleep $SLEEP_SECONDS  ; do
  IN="$CHECK_SERVICE_ENDPOINT_LIST"
  unset i
  while [ "$IN" != "$i" ] ;do
    i=${IN%%,*}
    IN="${IN#$i,}"
    RETVAL=$( curl -s -m $REQUEST_TIMEOUT_SECONDS "$i" ) || RETVAL="failed"
    debugp "checked for service endpoint $i result: $RETVAL"

    ## if any one fails,
    beginswith "true" "$RETVAL" || continue 2
  done
  break
 done ## while sleep

fi  ## check $CHECK_SERVICE_ENDPOINT_LIST

debugp "executing java jib path"

exec java $JAVA_OPTS -cp $( cat /app/jib-classpath-file ) $( cat /app/jib-main-class-file )

RETURN_CODE=$?

debugp "finished service-aware-entrypoint"

exit $RETURN_CODE
## EOL

