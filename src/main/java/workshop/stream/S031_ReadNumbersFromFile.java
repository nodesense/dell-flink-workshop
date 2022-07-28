package workshop.stream;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class S031_ReadNumbersFromFile {
    //
    public static void main(String[] args) throws  Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> texts = env.readTextFile("/home/rps/flink-workshop/data/mynumbers.txt");

        // action
        texts.print(); // it use PrintFunction internally connector called print

        // filter collects the data when the filter function returns true
        DataStream<String> filtered = texts.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                System.out.println("Filter " + s);
                if (s.trim().isEmpty())
                    return false; // this string shall not be passed to map function below

                return true; // string shall be passed to map function below
            }
        });

        DataStream<Integer> parsed = filtered.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String value) {
                System.out.println("Parse " + value);
                return Integer.parseInt(value.trim());
            }
        });

        parsed.print();

        // The right most one is return value, others are input parameter
        DataStream<Integer> multiplyBy10 = parsed.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) {
                System.out.println("Multiply " + value);
                return value * 10;
            }
        });

        multiplyBy10.print();
        // client program exited faster than data flow execution
        env.execute(); // run the data flow graph at job manager etc..
    }
}
