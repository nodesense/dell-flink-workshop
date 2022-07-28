package workshop.dataset;


import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;

public class S014_DataSetPojoJoin {

    /** POJO class representing a user. */
    public static class User {
        public int userIdentifier;
        public String name;

        public User() {}

        public User(int userIdentifier, String name) {
            this.userIdentifier = userIdentifier;
            this.name = name;
        }

        public String toString() {
            return "User{userIdentifier=" + userIdentifier + " name=" + name + "}";
        }
    }

    /** POJO for an EMail. */
    public static class EMail {
        public int userId;
        public String subject;
        public String body;

        public EMail() {}

        public EMail(int userId, String subject, String body) {
            this.userId = userId;
            this.subject = subject;
            this.body = body;
        }

        public String toString() {
            return "eMail{userId=" + userId + " subject=" + subject + " body=" + body + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        // initialize a new Collection-based execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();

        // create objects for users and emails
        User[] usersArray = {new User(1, "Peter"), new User(2, "John"), new User(3, "Bill")};

        EMail[] emailsArray = {
                new EMail(1, "Re: Meeting", "How about 1pm?"),
                new EMail(1, "Re: Meeting", "Sorry, I'm not available"),
                new EMail(3, "Re: Re: Project proposal", "Give me a few more days to think about it.")
        };

        // convert objects into a DataSet
        DataSet<User> users = env.fromElements(usersArray);
        DataSet<EMail> emails = env.fromElements(emailsArray);

        // join the two DataSets
        // conventions used in flink
        // join, sql, tables , data set etc
        // when we mention column, left one refer left table, right refers to right table
        // Data Flow, Transformation, Lazy, will not join here.. it will be joined when we use actions
        // return value is tuple
        DataSet<Tuple2<User, EMail>> joined =
                users.join(emails).where("userIdentifier").equalTo("userId");

        // retrieve the resulting Tuple2 elements into a ArrayList.
        // Collect is action, collect will execute the data flow on job manager,,....task manager
        // get the results back to Flint client program
        // bad practices to use collect except debug/learning
        // when you bring data back to client, client is not in cluster mode
        List<Tuple2<User, EMail>> result = joined.collect();

        // when we have result, result must be stored to Data Sink [Target] from the task manager to Target Data Sink
        // Task manager to HDFS, S3, ADLS, KAFKA, MySQL etc [Good practice]
        // don't bring data to client, then store data to target [bad practice]

        System.out.println("Join result count " + result.size());

        // Do some work with the resulting ArrayList (=Collection).
        for (Tuple2<User, EMail> t : result) {
            System.err.println("Result = " + t);
            // f0 means field 0, first memebr of tuple which is User
            // f1 means field 1, second member of tuple which is Email
            System.err.println("User = " + t.f0);
            System.err.println("Email  = " + t.f1);

        }
    }
}