package workshop.dataset;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.util.Collector;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * This example shows an implementation of WordCount without using the Tuple2 type, but a custom
 * class.
 */
@SuppressWarnings("serial")
public class S012_WordCountPojo {

    // Debugging , all components runs in same JVM process that runs java application
    // With IDE, we can easily debug flink code
    // we run Flink in embedded mode, ie non cluster environment
    // This app is called Flink Client component
    // Then we have 1 Job manager [schdule, run the data flow graph]
    // then we have N number of Task managers [runs the task, parallelism]

    /**
     * This is the POJO (Plain Old Java Object) that is being used for all the operations. As long
     * as all fields are public or have a getter/setter, the system can handle them
     */
    public static class Word {

        // fields
        private String word;
        private int frequency;

        // constructors
        public Word() {}

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Word(String word, int i) {
            this.word = word;
            this.frequency = i;
        }

        @Override
        public String toString() {
            return "Word=" + word + " freq=" + frequency;
        }
    }

    public static void main(String[] args) throws Exception {
        // Red dot on left pane is break point, debugger shall stop here..
        // set up the execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();


        // get input data
        DataSet<String> text2 = env.fromElements(new String[]{
                "To be, or not to be,--that is the question:--",
                "Whether 'tis nobler in the mind to suffer",
                "The slings and arrows of outrageous fortune",
                "Or to take arms against a sea of troubles,",
                "And by opposing end them?--To die,--to sleep,--",});

        DataSet<String> text = env.fromElements(new String[]{
                "apple",
                "orange",
                "apple",
                "apple",
                "orange"
        });

        // Lazy Data Flow Graph , no code is executed, no data is loaded, it build a flow graph
        DataSet<Word> counts =
                // split up the lines into Word objects (with frequency = 1)
                text.flatMap(new Tokenizer())
                        // group by the field word and sum up the frequency
                        // from domain object, we pick the key attribute
                        .groupBy(new KeySelector<Word, String>() {
                            public String getKey(Word  word) {
                                return word.word;
                            }
                        })
                       // .groupBy( "word") // FIXME
                        // reducer is useful to custom aggregate functionalities
                        .reduce(
                                new ReduceFunction<Word>() {
                                    // acc - accumulator, accumulator result over time on each input
                                    // value2 is input from grouped Tokeneizer Word
                                    // Input
                                    /*
                                        (apple, 1) <- first input apple , reduce won't be called, instead the first entry shall be used for initialization
                                        (orange, 1) <- first input orange, reduce won't be called, instead the first entry shall be used for initialization
                                        (apple, 1) <- apple is second time, we already have acc (apple, 1), now reduce shall be called
                                                        // first parameter to reduce shall be acc value,  apple   1 [group 1]
                                                        // second parameer is from input  (apple, 1)

                                                        // acc.freq + value.freq [ 1 + 1] = 2 output shall be (apple, 2)
                                                        now acc shall be set with (apple, 2)
                                        (apple, 1) <- apple is 3rd time, we already have acc (apple ,2)
                                                        now reducer called first param is acc (apple ,2), second param is value from input (apple, 1)
                                                        acc.freq + value.freq = 3 output shall (apple, 3)
                                                        // updated to acc = (apple , 3)

                                         (orange, 1) , now reducer shall be called with acc (orange, 1), value (orange, 1)
                                                     returns (orange, 2) updated in accumulator
                                     */

                                    // assume, a like table
                                    //   word   acc/frequency
                                    //  apple   2 [group 1] [ was (apple   1)]
                                    //  orange  1 [group 2]

                                    // below reduce shall be called

                                    @Override
                                    public Word reduce(Word acc, Word value2) throws Exception {
                                        System.out.println("Reduce acc " + acc);
                                        System.out.println("Reduce value2 " + value2);

                                        return new Word(
                                                acc.word, acc.frequency + value2.frequency);
                                    }
                                });

        System.out.println("Printing result to stdout. Use --output to specify output path.");
        // action
        // action functions like execute, executeSQL, sqlQuery , collect etc..
        // shall submit data flow to job manager, then job manager shall schedule the tasks
        // execute the tasks in task manager
        // all print statements shall be printed on task manager JVM , in production we cant see print output in your editor or
        // client stdout
        // loading from csv//tablets, kafka etc.. process separately..
        counts.print();



        System.out.println("---------------------------");
        // this is different action,
        // again load csv/table/json etc, and again process teh data
        // again join , may perform expensive computation..
        // every action shall create a data flow graph, submit to job clusters and get results
        counts.print();
        System.out.println("---------------------------");

        // separate action method, now new data flow graph generated, submitted to flink cluster..
        List<Word> wordList =  counts.collect();

    }


    // Tokenizer collect Word object with 2 memeber variables, word, frequency
    public static final class Tokenizer implements FlatMapFunction<String, Word> {

        @Override
        public void flatMap(String value, Collector<Word> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Word(token, 1));
                }
            }
        }
    }
}