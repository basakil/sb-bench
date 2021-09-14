#!/bin/sh

echo "starting service-aware-entrypoint"

beginswith() { case $2 in "$1"*) true;; *) false;; esac; }

if [ ! -z "$CHECK_SERVICE_ENDPOINT_LIST" ] ; then
 echo "CHECK_SERVICE_ENDPOINT_LIST=$CHECK_SERVICE_ENDPOINT_LIST . Processing service list before continueing..."
 while sleep 3  ; do
  IN="$CHECK_SERVICE_ENDPOINT_LIST"
  unset i
  while [ "$IN" != "$i" ] ;do
    i=${IN%%,*}
    IN="${IN#$i,}"
    echo "checking for service endpoint $i"
    RETVAL=$( curl -s "$i" ) || RETVAL="false"
    beginswith "true" "$RETVAL" || continue 2
  done
  break
 done
fi  ## check $CHECK_SERVICE_ENDPOINT_LIST

echo "EXECUTING_APP"

echo "finished service-aware-entrypoint"
