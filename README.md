// client attached mode

$FLINK_HOME/bin/flink run -m localhost:8282 -c workshop.analytics.TrueDataCandleMain \
./target/flink-workshop-1.0-SNAPSHOT.jar

// client detached mode, once job submitted, flink client shall not wait..

$FLINK_HOME/bin/flink run -m localhost:8282 --detached -c workshop.analytics.TrueDataCandleMain \
./target/flink-workshop-1.0-SNAPSHOT.jar



$FLINK_HOME/bin/flink run -m localhost:8282 --detached -c workshop.analytics.TrueDataCandleMain \
./target/flink-workshop-1.0-SNAPSHOT.jar

#  on docker compose cluster

Flink client, parsing of sql, flow graph builder, optimizer, flow graph generation, 
all hapends in local machine/ or the machine where we run flink command..

$FLINK_HOME/bin/flink run -m localhost:8181 -c workshop.analytics.TrueDataCandleMain \
./target/flink-workshop-1.0-SNAPSHOT.jar