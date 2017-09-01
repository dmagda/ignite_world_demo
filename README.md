# Ignite World Database Usage Sample

The project uses a dump of the word database altered for Ignite DDL syntax and shows how to:

* Create SQL schema and preload data using prepared ignite_world.sql script.
* Process the loaded data using SQL queries.
* Process the loaded data using key-value operations.

## SQL Schema Creation and Data Preloading

Download Apache Ignite 2.1 or later version:
https://ignite.apache.org/download.cgi#binaries

Download SQLLine tool:
https://github.com/julianhyde/sqlline

Set up SQLLine doing the following:
1. Copy `{ignite_folder}/libs/ignite-core.jar` file to `{sqlline_folder}/bin` directory.
2. Download `ignite_world.sql` from the root of this project and paste into `{sqlline_folder}/bin` directory.

Connect to Ignite cluster and execute the SQL script:

1. Start one or multiple cluster nodes using command below (use `ignite.bat` for  Windows):
```shell
{ignite_folder}/bin/ignite.sh
```

2. Connect to the cluster from SQLLine using this command (use sqlline.bat for Windows):
```shell
{sqlline_folder}/bin/sqlline -d org.apache.ignite.IgniteJdbcThinDriver --color=true --verbose=true --showWarnings=true --showNestedErrs=true -u jdbc:ignite:thin://127.0.0.1/
```

3. Create SQL schema and preload data executing using SQLLine syntax:
```shell
!run ignite_world.sql
```
Refer to this page to see SQLLine commands that work for Ignite: https://cwiki.apache.org/confluence/display/IGNITE/Overview+sqlline+tool

## Running Java Code Samples

Download this project and start `SqlDataProcessing`.