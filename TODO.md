1. Flink Sink with HTTP REST API [POST]

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

use MovieRatingJoinResult in place Tuple3<StringValue, StringValue, DoubleValue> 

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