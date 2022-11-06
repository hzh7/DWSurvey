#!/bin/sh

P_ID1=$(ps -ef |grep "dwsurvey-oss-vue" |grep " $1 " |grep -v "grep" |awk '{print $2}')

DATA=$(date +"%Y-%m-%d-%H%M")

if [ "$P_ID1" == "" ]; then
  echo "dwsurvey-oss-vue process not exists"
  mv /data/dwsurvey/log/8080.log /data/dwsurvey/log/$DATA-8080.log
  nohup java  -Dfile.encoding=utf-8 -jar dwsurvey-oss-vue-v.*.*.jar \
  --spring.profiles.active=prod --server.port=8080 \
  --spring.datasource.username=user_00 \
  --spring.datasource.password=bO5oL8tA4bK4 \
  >> /data/dwsurvey/log/8080.log &
else
  echo "dwsurvey-oss-vue process pid is: $P_ID1"
fi

P_ID2=$(ps -ef |grep "python" |grep "app.py" |grep " $1 " |grep -v "grep" |awk '{print $2}')
if [ "$P_ID2" == "" ]; then
  echo "flask process not exists"
  mv /data/dwsurvey/log/8082.log /data/dwsurvey/log/$DATA-8082.log
  nohup /usr/bin/python3.9 app.py >> /data/dwsurvey/log/8082.log &
else
  echo "flask process pid is: $P_ID2"
fi
