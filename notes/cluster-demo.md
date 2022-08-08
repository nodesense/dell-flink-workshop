



wget  -P mount/jars https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-kafka/1.15.1/flink-sql-connector-kafka-1.15.1.jar

wget  -P mount/jars https://repo1.maven.org/maven2/org/apache/flink/flink-parquet/1.15.1/flink-parquet-1.15.1.jar


wget  -P mount/jars https://repo1.maven.org/maven2/org/apache/flink/flink-orc/1.15.1/flink-orc-1.15.1.jar







open new terminal

docker volume create portainer_data
docker run -d -p 8000:8000 -p 9443:9443 --name portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce:latest

https://localhost:9443/

username: admin
password: Training@123


git clone https://github.com/nodesense/fastdata-stack  fastdata

cd fastdata/flink

https://github.com/nodesense/fastdata-stack



sudo apt install maven

mvn clean install

mvn clean package
