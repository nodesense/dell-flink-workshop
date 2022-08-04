package workshop.analytics;


import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import workshop.functions.TickWindowDemo;
import workshop.models.Candle;
import workshop.models.TrueDataTick;
import workshop.util.SqlText;

import java.time.Duration;

public class TrueDataCandleMain {
    public static void main(String[] args) throws  Exception {
        // set up the Java DataStream API
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // set up the Java Table API
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String TrueDataTickKafkaSourceJson = SqlText.getSQL("sql/TrueDataTickKafkaSourceJson.sql");
        System.out.println(TrueDataTickKafkaSourceJson);
        tableEnv.executeSql(TrueDataTickKafkaSourceJson);
        // Stream tables are called dynamic tables
        //stream tables shall have change log, newly added reocrd/modified records are published
        final Table result =  tableEnv.sqlQuery("SELECT * FROM TrueDataTicks WHERE Volume > 30000");
       // result.execute().print();
        // type cast Table Row into Java POJO

        DataStream<TrueDataTick> tickDataStream = tableEnv.toDataStream(result, TrueDataTick.class);
        // This creates a flow graph
        // Kafka as source and printfunction as sink
        // lazy evaluation, we need to execute it on job manager
        // tickDataStream.print();

        // watermark
        // processing time flink time when the data processed
        // event time - when the data proceduced
//        WatermarkStrategy<TrueDataTick> watermarkStrategy =  WatermarkStrategy
//        .<TrueDataTick>forBoundedOutOfOrderness(Duration.ofSeconds(2))
//        .withTimestampAssigner((event, timestamp) -> event.Timestamp.getTime());

        WatermarkStrategy<TrueDataTick> watermarkStrategy = new WatermarkStrategy<TrueDataTick>() {
            @Override
            public WatermarkGenerator<TrueDataTick> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                return new BoundedOutOfOrdernessWatermarks<TrueDataTick>(Duration.ofSeconds(20)) {
                    @Override
                    public void onEvent(TrueDataTick event, long eventTimestamp, WatermarkOutput output) {
                       // System.out.println("Inside water marke geneeator " +  event);
                        // custom code that force the window to process the data next
                        super.onEvent(event, eventTimestamp, output);
                        super.onPeriodicEmit(output);
                    }
                };
            }
        }
        .withTimestampAssigner((event, timestamp) -> event.Timestamp.getTime());

        // payload, special tag, attribute, that may say that process the message
        // start transaction itself an event
        //   followed by messages based on tracsaction ..
        // end transaction , another event

        DataStream<TrueDataTick>  dataValueStreamWithWaterMark = tickDataStream
                .assignTimestampsAndWatermarks(watermarkStrategy);// crete

        dataValueStreamWithWaterMark.print();




        SingleOutputStreamOperator<Candle> candleStream = dataValueStreamWithWaterMark
                .keyBy( (tick) -> tick.Symbol )
                //  .window(TumblingEventTimeWindows.of(Time.seconds(10), Time.seconds(1))) // results producerd  1 sec offset past time interval like 11 instead of 10
                // .window(TumblingEventTimeWindows.of(Time.seconds(2))) // rounded to wall clock 5, 10, 15, ...
                // .process(new TAWindow("SMA", 5))
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .process(new TickWindowDemo())
                ;


        candleStream.print(); // no collect is used, won't print anything
        // get the graph and submit to job manager
        env.execute();


    }

    }
