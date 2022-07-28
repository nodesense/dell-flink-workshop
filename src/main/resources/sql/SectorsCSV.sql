CREATE TABLE Sectors (
  company STRING,
  industry STRING,
  asset STRING,
  series  STRING,
  isbn STRING
) WITH (
 'connector' = 'filesystem',
 'format' = 'csv',
 'path' = '/home/rps/flink-workshop/data/ind_nifty50list.csv',
 'csv.ignore-parse-errors' = 'true',
  'csv.allow-comments' = 'true'
);