# freshworks-datastore-assignment

* Works on any OS if it supports JDK 8.
* Implementation is single threaded.
* Doesn't support file locking mechanism. Hence database can be accessed by more than one processes at a time.
* Doesn't support TTL feature.

# Usage guide

Download the freshworks folder and import freshworks package in your java code.

Get instance of DataStore

```
DataStore dataStore = DataStore.getInstance();
```

Following methods are defined under the DataStore instance

```
//Create a database with name "db" in present working directory. Returns true if successfull else false or throws custom Exception
public boolean createDataStore(String db) throws DataStoreException;

//Create a database with name "db" in given path "path". Returns true if successfull else false or throws custom Exception
public boolean createDataStore(String db, String path) throws DataStoreException;

//Add "key:value" data to a database with name "db" present at path "path". Returns true if successfull else false or throws custom Exception
public boolean createData(String db, String path, String key, String value) throws DataStoreException;

//Reads "value" data for given "key" from a database with name "db" present at path "path". Returns value if successfull else throws custom Exception
public String readData(String db, String path, String key) throws DataStoreException;

//Deletes given "key:value" from a database with name "db" present at path "path". Returns true if successfull else false or throws custom Exception
public boolean deleteData(String db, String path, String key) throws DataStoreException;
```
