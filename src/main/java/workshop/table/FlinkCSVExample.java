package workshop.table;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import workshop.util.SqlText;

// https://www1.nseindia.com/content/equities/EQUITY_L.csv

public class FlinkCSVExample {
    public static void main(String[] args) throws  Exception  {
        // set up the Java DataStream API
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // set up the Java Table API
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // Company Name,Industry,Symbol,Series,ISIN Code
        String csvSql = SqlText.getSQL("sql/SectorsCSV.sql");
        System.out.println(csvSql);

        tableEnv.executeSql(csvSql);

        // union the two tables
        final Table result =  tableEnv.sqlQuery("SELECT *, 'NIFTY' as index  FROM Sectors");

        result.printSchema();

        result.execute().print();

        // env.execute();

    }
}