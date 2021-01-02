package freshworks;

import freshworks.libraries.org.json.simple.*;
import freshworks.libraries.org.json.simple.parser.*;
import java.io.*;
import java.util.*;

public class DataStore implements CrdInterface {

  public static DataStore sDataStore = null;

  public static DataStore getInstance() {
    if(sDataStore==null) {
      sDataStore = new DataStore();
    }
    return sDataStore;
  }

  private DataStore() {

  }

  public boolean createDataStore(String db) throws DataStoreException {
    File file = new File(db);
    return createDB(file);
  }

  public boolean createDataStore(String db, String path) throws DataStoreException {
    File file = new File(path, db);
    return createDB(file);
  }

  public boolean createData(String db, String path, String key, String value) throws DataStoreException {

    if(key.length()==0 || value.length()==0) {
      throw new DataStoreException("Key or value can't be empty!");
    }

    if(key.length()>32) {
      throw new DataStoreException("Key can't be longer than 32 characters!");
    }

    File databaseFolder = new File(path, db);

    if(!checkDatabase(databaseFolder)) {
      return false;
    }

    JSONParser parser = new JSONParser();

    JSONObject valueObj = null;
    try {
      valueObj = (JSONObject)parser.parse(value);
    } catch(ParseException e) {
      throw new DataStoreException("Value is not a valid JSON!");
    }

    long valueSize = 0;
    if(valueObj.toString().getBytes().length>(16*1024)) {
      throw new DataStoreException("Value can't be greater than 16KB!");
    }
    else {
      valueSize = valueObj.toString().getBytes().length;
    }

    String s = readFile(databaseFolder, "master.json");
    long size = 0L;
    JSONArray masterObj = null;
    JSONObject masterObj_metadata = null;
    JSONObject masterObj_data = null;
    try {
      masterObj = (JSONArray)parser.parse(s);
      masterObj_metadata = (JSONObject)masterObj.get(0);
      size = (Long)masterObj_metadata.get("size");
      masterObj_data = (JSONObject)masterObj.get(1);
    } catch(Exception e) {
      throw new DataStoreException("Database is corrupted!");
    }

    size += valueSize;
    if((size)>(1*1024*1024*1024)) {
      throw new DataStoreException("Size of database can't exceed limit of 1GB!");
    }

    UUID uid = UUID.randomUUID();

    if(masterObj_data.containsKey(key)) {
      throw new DataStoreException("Key already exists!");
    }

    masterObj_data.put(key, uid.toString());
    masterObj_metadata.put("size", size);
    masterObj.set(1, masterObj_data);

    createFile(databaseFolder, uid.toString(), valueObj.toString());
    new File(databaseFolder, "master.json").delete();
    createFile(databaseFolder, "master.json", masterObj.toString());

    return true;
  }

  public String readData(String db, String path, String key) throws DataStoreException {

    File databaseFolder = new File(path, db);

    if(!checkDatabase(databaseFolder)) {
      throw new DataStoreException("Could not access database!");
    }

    String s = readFile(databaseFolder, "master.json");

    String uid = new String();
    JSONParser parser = new JSONParser();
    JSONArray masterObj = null;
    JSONObject masterObj_metadata = null;
    JSONObject masterObj_data = null;
    try {
      masterObj = (JSONArray)parser.parse(s);
      masterObj_metadata = (JSONObject)masterObj.get(0);
      masterObj_data = (JSONObject)masterObj.get(1);
      uid = (String)masterObj_data.get(key);
    } catch(Exception e) {
      throw new DataStoreException("Database is corrupted!");
    }

    if(uid==null) {
      throw new DataStoreException("Key doesn't exist!");
    }

    return readFile(databaseFolder, uid);
  }

  public boolean deleteData(String db, String path, String key) throws DataStoreException {
    File databaseFolder = new File(path, db);

    if(!checkDatabase(databaseFolder)) {
      throw new DataStoreException("Could not access database!");
    }

    String s = readFile(databaseFolder, "master.json");

    String uid = new String();
    JSONParser parser = new JSONParser();
    JSONArray masterObj = null;
    JSONObject masterObj_metadata = null;
    JSONObject masterObj_data = null;
    try {
      masterObj = (JSONArray)parser.parse(s);
      masterObj_metadata = (JSONObject)masterObj.get(0);
      masterObj_data = (JSONObject)masterObj.get(1);
      uid = (String)masterObj_data.get(key);
    } catch(Exception e) {
      throw new DataStoreException("Database is corrupted!");
    }

    if(uid==null) {
      throw new DataStoreException("Key doesn't exist!");
    }

    try {
      String temp = readFile(databaseFolder, uid);
      long valueSize = temp.getBytes().length;
      long size = (Long)masterObj_metadata.get("size");
      masterObj_metadata.put("size", size-valueSize);
      masterObj_data.remove(key);
      masterObj.set(0, masterObj_metadata);
      masterObj.set(1, masterObj_data);
      new File(databaseFolder, "master.json").delete();
      createFile(databaseFolder, "master.json", masterObj.toString());
      File file = new File(databaseFolder, uid);
      file.delete();
    } catch(Exception e) {
      throw new DataStoreException("Operation failed!");
    }

    return true;

  }

  private boolean createDB(File file) throws DataStoreException {

    if(file.mkdir()) {
      File f = new File(file, "master.json");
      try {
        JSONArray masterObj = new JSONArray();
        JSONObject masterObj_metadata = new JSONObject();
        masterObj_metadata.put("size", new Long(0));
        JSONObject masterObj_data = new JSONObject();
        masterObj.add(masterObj_metadata);
        masterObj.add(masterObj_data);

        createFile(file, "master.json", masterObj.toString());

        return true;
      } catch (Exception e) {
        file.delete();
        return false;
      }
    }

    throw new DataStoreException("Either a folder with same name exists or permissions denied!");
  }

  private boolean checkDatabase(File databaseFolder) throws DataStoreException {

    if(!databaseFolder.exists()) {
      throw new DataStoreException("Database doesn't exist or permissions denied");
    }

    File masterFile = new File(databaseFolder, "master.json");

    if(!masterFile.exists()) {
      throw new DataStoreException("Database doesn't exist or permissions denied");
    }

    return true;
  }

  private String readFile(File databaseFolder, String name) throws DataStoreException {

    StringBuilder sb = new StringBuilder();

    File file = new File(databaseFolder, name);

    RandomAccessFile raf = null;

    try {
      raf = new RandomAccessFile(file, "r");

      while(raf.getFilePointer() < raf.length()) {
        sb.append(raf.readLine());
      }
    } catch(IOException e) {
      throw new DataStoreException("Failed to access the database!");
    }
    finally {
      if(raf!=null) {
        try {
          raf.close();
        }catch(IOException e) {
          //Do nothing
        }
      }
    }

    return sb.toString();
  }

  private void createFile(File databaseFolder, String name, String data) throws DataStoreException {

    File file = new File(databaseFolder, name);
    RandomAccessFile raf = null;

    try {
      raf = new RandomAccessFile(file, "rw");
      raf.writeBytes(data);
      file.createNewFile();
    } catch(IOException e) {
      throw new DataStoreException("Some error occured!");
    } finally {
      if(raf!=null) {
        try {
          raf.close();
        } catch(IOException e) {
          //Do nothing
        }
      }
    }
  }

}
