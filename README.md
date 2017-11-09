# Ignite World Database Usage Sample

The project uses a dump of the word database altered for Ignite DDL syntax and shows how to:

* Create SQL schema and preload data using prepared ignite_world.sql script.
* Process the loaded data using SQL queries.
* Process the loaded data using key-value operations.
* Execute computations in the cluster.

## SQL Schema Creation and Data Preloading

Download Apache Ignite 2.3 or later version:
https://ignite.apache.org/download.cgi#binaries

Connect to Ignite cluster and execute the SQL script:

1. Start one or multiple cluster nodes using command below (use `ignite.bat` for  Windows):
```shell
{ignite_folder}/bin/ignite.sh
```

2. Connect to the cluster from SQLLine using this command (use sqlline.bat for Windows):
```shell
{ignite_folder}/bin/sqlline.sh --color=true --verbose=true -u jdbc:ignite:thin://127.0.0.1/
```

3. Create SQL schema and preload data executing using SQLLine syntax:
```shell
!run {path_to_the_project}/ignite_world.sql
```
Refer to this page to see SQLLine commands that work for Ignite: https://apacheignite-sql.readme.io/docs/sqlline

## Running Java Code Samples

The project includes the following examples to run after the cluster is started and pre-loaded:
* `SqlDataProcessing` - running SQL queries using Java APIs.
* `KeyValueDataProcessing` - processing data with key-value APIs.
* `KeyValueBinaryDataProcessing` - processing data with key-value APIs avoid objects deserialization on the cluster side.