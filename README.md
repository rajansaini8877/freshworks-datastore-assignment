# freshworks-datastore-assignment

* Works on any OS if it supports JDK 8.
* Implementation is single threaded.
* Doesn't support file locking mechanism. Hence database can be accessed by more than one processes at a time.
* Doesn't support TTL feature.
* Doesn't strictly follow the ACID properties of DBMS transactions.

# Usage guide

Download the freshworks folder and import freshworks package in your java code.

Get instance of DataStore

```
DataStore dataStore = DataStore.getInstance();
```

Following methods are defined under the DataStore instance

```
//Create a database with name "db" in present working directory
//Returns true if successful else false or throws custom Exception
public boolean createDataStore(String db) throws DataStoreException;

//Create a database with name "db" in given path "path"
//Returns true if successful else false or throws custom Exception
public boolean createDataStore(String db, String path) throws DataStoreException;

//Add "key:value" data to a database with name "db" present at path "path"
//Returns true if successful else false or throws custom Exception
public boolean createData(String db, String path, String key, String value) throws DataStoreException;

//Reads "value" data for given "key" from a database with name "db" present at path "path"
//Returns value if successful else throws custom Exception
public String readData(String db, String path, String key) throws DataStoreException;

//Deletes given "key:value" from a database with name "db" present at path "path"
//Returns true if successful else false or throws custom Exception
public boolean deleteData(String db, String path, String key) throws DataStoreException;
```

* Below is a sample program that uses this custom DBMS. This "Test.java" file is located in the parent directory of "freshworks" folder

```
import java.util.*;
import java.io.*;
import freshworks.*;

class Test {
  public static void main(String[] args) {
    DataStore d = DataStore.getInstance();
    try {
      d.createDataStore("MyDatabase", "C:\\Users\\rajan\\Desktop");
      d.createData("MyDatabase", "C:\\Users\\rajan\\Desktop", "def", "{\"num\":100, \"is_vip\":true, \"name\":\"foo\"}");
      d.createData("MyDatabase", "C:\\Users\\rajan\\Desktop", "abc", "{\"num\":100, \"is_vip\":true, \"name\":\"foo\"}");
      System.out.println(d.readData("MyDatabase", "C:\\Users\\rajan\\Desktop", "def"));
      d.deleteData("MyDatabase", "C:\\Users\\rajan\\Desktop", "abc");
    } catch(DataStoreException e) {
      System.out.println(e.getMessage());
    }
  }
}
```
