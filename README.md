1. Надо запустить kafka, для этого испрользуем встроенный zookeeper  (https://kafka.apache.org/quickstart первые три степа здесь)
   >1)bin\windows\zookeeper-server-start.bat config/zookeeper.properties
   >2)bin\windows\kafka-server-start.bat config\server.properties
2. Также создать Topic alerts. На этом с kafka все
   >3)bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic alerts

3. Теперь к сабмиту(на Windows надо скачать winutils https://github.com/cdarlint/winutils я использовал 2.7, ну и конечно apache spark с официального сайта) 
   >bin\spark-submit --class Main --master local[2] <path to the jar>
