#rabbitmq-server
chmod +x ./wait_for_it.sh
bash ./wait_for_it.sh -h rabbitmq_server -p 15672 --strict -- mvn exec:java -D exec.mainClass=MessageQueue.ServicesMQ