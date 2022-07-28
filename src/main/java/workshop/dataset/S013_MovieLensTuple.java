package workshop.dataset;

import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.DoubleValue;
import org.apache.flink.types.StringValue;
import org.apache.flink.util.Collector;

// https://files.grouplens.org/datasets/movielens/ml-latest-small.zip

import java.util.List;

public class S013_MovieLensTuple {
    public static void main(String[] args) throws  Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // DataSet , TupleX, POJO - low level code

        DataSet<Tuple3<Long, String, String>> movies = env.readCsvFile("data/ml-latest-small/movies.csv")
                .ignoreFirstLine() // skip the header line
                .parseQuotedStrings('"') // double quote used as escape
                .ignoreInvalidLines() // ifnore parse error,s empty lines
                    // Column 0 is movieId Long Data type,  Column 1 is movie title, string type, column 2 is genres, string type
                .types(Long.class, String.class, String.class); // data type to read for tuple

        // read from ratings
        DataSet<Tuple2<Long, Double>> ratings = env.readCsvFile("data/ml-latest-small/ratings.csv")
                .ignoreFirstLine()
                // we can ignore or select the field we need from csv from left to right
                // true means including that column, false means not to include the column
                // we pick movieId, rating columns, we ignore userId, timestamp columns
                .includeFields(false, true, true, false)
                // type for userid, rating
                .types(Long.class, Double.class);

        List<Tuple3<Long, String, String>> allMovies = movies.collect();
        System.out.println("Total Movies " +  allMovies.size());
        System.out.println("First movie " + allMovies.get(0));

        movies.print();
        // TODO: print rating data, pick 1 record etc

        // make join, then also perform analytics
        // join is based on movieId, analytics based on individual genres category Animal, Comedy, Romance, Drama....
        // analytics based on average rating..
        List<Tuple2<String, Double>> distribution = movies.join(ratings)
                // take first column from left and first column from right [inner join]
                .where(0) // from left
                .equalTo(0) // from right
                // first arg movieDataSet Tuple3<Long, String, String> is movie data set
                // second arg is rating data set, Tuple2<Long, Double>
                // 3rd is return type -right most of the time   , return Tuple3<StringValue, StringValue, DoubleValue>
                .with(new JoinFunction<Tuple3<Long, String, String>,Tuple2<Long, Double>, Tuple3<StringValue, StringValue, DoubleValue>>() {
                    // StringValue, DoubleValue, IntegerValue............. are Flink Data Types, used by DataSEt, Streams, Tables SQL etc
                    private StringValue name = new StringValue();

                    private StringValue genre = new StringValue();
                    private DoubleValue score = new DoubleValue();
                    private Tuple3<StringValue, StringValue, DoubleValue> result = new Tuple3<>(name,genre,score);
                    // we do join projection, after where condition, whare are the fields we expect come from
                    // we return movie title from movies dataset, genres first entry of genres column
                    // rating
                    @Override
                    public Tuple3<StringValue, StringValue, DoubleValue> join(Tuple3<Long, String, String> movie,Tuple2<Long, Double> rating) throws Exception {
                        // movie title from movies
                        name.setValue(movie.f1); // movie title
                        genre.setValue(movie.f2.split("\\|")[0]); // first part of genres
                        score.setValue(rating.f1); // rating from rating data
                        return result;
                    }
                })
                // group by field 1 which is genres part 1 like Comedy or Adventure or somethign else..
                .groupBy(1)
                // reduce will take individual element from group and perform operations, it means, if we have 1 million records, reduce function called 1 million - 1 times
                // reduceGroup will give iterator which include the complete dataset** of that group..
                .reduceGroup(new GroupReduceFunction<Tuple3<StringValue,StringValue,DoubleValue>, Tuple2<String, Double>>() {
                    // iterable group of elements
                    // sum the total ratings 1.0, 1.5, 2.0, sum all the ratings of genres type, 1.0 + 1.5 + 2.0 = 4.5 [not average]
                    @Override
                    public void reduce(Iterable<Tuple3<StringValue,StringValue,DoubleValue>> iterable, Collector<Tuple2<String, Double>> collector) throws Exception {
                        StringValue genre = null;
                        int count = 0;
                        double totalScore = 0;
                        for(Tuple3<StringValue,StringValue,DoubleValue> movie: iterable){
                            genre = movie.f1;
                            totalScore += movie.f2.getValue();
                            count++;
                        }

                        // here we do average 4.5/3 = 1.5 as avg
                        // output of this reduce function is analytics operation, avg rating of movie with specific genres
                        collector.collect(new Tuple2<>(genre.getValue(), totalScore/count));
                    }
                })
                .collect(); // action


        System.out.println("Join results " + distribution.size());
        System.out.println("Join record 1 " + distribution.get(0));

        for (Tuple2<String, Double> r: distribution) {
            System.out.println(r);
        }


    }
}