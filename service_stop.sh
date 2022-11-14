#!/bin/sh

P_ID1=$(ps -ef |grep "dwsurvey-oss-vue" |grep " $1 " |grep -v "grep" |awk '{print $2}')

DATA=$(date +"%Y-%m-%d-%H%M")
echo $DATA

if [ "$P_ID1" == "" ]; then
  echo "dwsurvey-oss-vue process not exists"
else
  echo "dwsurvey-oss-vue process pid is: $P_ID1, kill it"
  kill $P_ID1
fi

P_ID2=$(ps -ef |grep "python" |grep "app.py" |grep " $1 " |grep -v "grep" |awk '{print $2}')
if [ "$P_ID2" == "" ]; then
  echo "flask process not exists"
else
  echo "flask process pid is: $P_ID2, kill it"
    kill $P_ID2
fi
