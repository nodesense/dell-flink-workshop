package workshop.dataset;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class S011_WordCountTuple {

    public static void main(String[] args) throws Exception {

        // set up the execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // get input data, batch data, finite data/bounded data
        DataSet<String> text = env.fromElements(new String[]{
                "To be, or not to be,--that is the question:--",
                "Whether 'tis nobler in the mind to suffer",
                "The slings and arrows of outrageous fortune",
                "Or to take arms against a sea of troubles,",
                "And by opposing end them?--To die,--to sleep,--",});

        // Tuple - pair of elements, 2 or 3 or more... each element may have its own data type
        // Tuple - Key/Value, key is string, value is Integer

        // I/O - Read line
        // Transformation - Tokenizer class => split the line/words into word array
        // Transfomration - assign pair (word, 1) ie (apple, 1), (orange, 1), (apple, 1), we dont aggregate here..
        // Transformation should not do aggregation
        // group  the words by word
        // sum the words
        // Tokenizer is custom operator class // function
        // every input in the text data set, each string shall be passed to tokernizer as parameter/input
        // tokenizer return word array as output
        // flatMap - this converts a list/array into flatten element
        //   flatMap - input: 1 array  ["apple", "orange", "mango"] , output shall be 3 elements "apple", "orange", "mango"
        DataSet<Tuple2<String, Integer>> counts =
                // split up the lines in pairs (2-tuples) containing: (word,1)
                text.flatMap(new Tokenizer())
                        // group by the tuple field "0" which is word  and sum up tuple field "1" which is 1
                        // output of flatMap collector shall be passed to group by
                        // 0 here means, 1st element in tuple pair [Key]
                        .groupBy(0)
                        // build in aggregate function,
                        // 1 means the value in the tuple, key, integer (mango, 1), like array index
                        .sum(1);

        System.out.println("Printing result to stdout. Use --output to specify output path.");
        // display the output
        counts.print();

    }

    // FlatMapFunction<String, Tuple2<String, Integer> - left of data types shall be input, right most shall be output
    // input is string, line by line
    // Output shall be many with pair (word, 1) (apple, 1) (orange, 1)
    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple2<String, Integer>> {

        // value is string input, as whole line
        // Collector<Type> is output data, most useful function in flink to generate output from operators, process functions
        // Collector can output as many results based use case
        //
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            // regex to split words by space, W+ [a-zA-Z0-9], remove all special chars, returns word array
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            // for each element
            for (String token : tokens) {
                if (token.length() > 0) { // don't include empty word,
                    // bringing a structure to unstructured data, word , initial value
                    out.collect(new Tuple2<>(token, 1)); // pair (word, 1)
                }
            }

            // use out.collect to return output to flink
            // whatever we produce in out.collect shall be passed next operator in the data flow graph

            // flink doesn't have return functionality like spark in many operators
            // spark map (input => must return output)

        }
    }
}