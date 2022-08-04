https://github.com/nodesense/dell-flink-workshop


https://demo.firepad.io/#rYFGTgNy2F

https://github.com/nodesense/flink-workshop/blob/main/src/main/java/workshop/models/TrueDataTick.java
https://github.com/nodesense/flink-workshop/blob/main/src/main/java/workshop/models/Candle.java




node publish-tick.js nse-live-ticks-simulate4  data/2022-07-07.json INFY

node publish-tick.js nse-live-ticks-simulate4  data/2022-07-07.json

https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/kafka/

git clone https://github.com/nodesense/fastdata-stack
https://github.com/nodesense/flink-workshop

Copy
https://github.com/nodesense/flink-workshop/blob/main/src/main/resources/sql/TrueDataTickKafkaSourceJson.sql
https://github.com/nodesense/flink-workshop/blob/main/src/main/java/workshop/analytics/TrueDataCandleMain.java


kafka-topics  --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic nse-live-ticks-simulate4
kafka-console-consumer --bootstrap-server localhost:9092 --topic nse-live-ticks-simulate4

kafka-topics  --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic candles-live4
kafka-console-consumer --bootstrap-server localhost:9092 --topic candles-live4

curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash

open new terminal

nvm install 16.16




https://github.com/nodesense/kafka

https://github.com/nodesense/kafka/blob/main/notes/kafka-001-create-topics.md

https://github.com/nodesense/kafka/blob/main/notes/kafka-003-partitions-with-key-value.md



docker system prune  --volumes --all

sudo aa-remove-unknown

confluent local destroy
confluent local start
confluent local stop


sudo apt remove mysql-server



https://github.com/nodesense/fastdata-stack

package workshop.models;

public class Sector {
public String company;
public String industry;
public String asset;
public String series;
public String isbn;

    @Override
    public String toString() {
        return "Sector{" +
                "company='" + company + '\'' +
                ", industry='" + industry + '\'' +
                ", asset='" + asset + '\'' +
                ", series='" + series + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}










https://nightlies.apache.org/flink/flink-docs-master/


https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/dev/datastream/overview/


1. Create a package called stream under workshop package
2. Create a Java Class S031_ReadNumbersFromFile
3. Refer the documentation https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/dev/datastream/overview/
4. Create a Stream environment
5. Create a file in data directory called "mynumbers.txt" which has each line with number
   10
   20
6. Now read the string numbers as Data Stream use readTextFile api
7. Apply map function as per document that convert string to number
8. Multiply numbers by 10
9. print the result
10.



https://www1.nseindia.com/content/equities/EQUITY_L.csv



## Team Excercise 1

```
MovieLensTuple use as example

but in place of Tuple, use Pojo class
Read data using tuple, then 
use map function, that transform tuple to POJO object

class Movie {
    movieId, title, genres
}

class RAting {
 movieId, rating
}

use MovieRatingJoinResult in place Tuple3 

class MovieRatingJoinResult {
String movieTitle
    String genres; one of the genres
    String rating;
}

class GenresRating {
String genres;
Doouble rating;
}

```

.groupBy(new KeySelector() {
public String getKey(Word  word) {
return word.word;
}
})


DataSet text = env.fromElements(new String[]{
"apple",
"orange",
"apple",
"apple"
});
 