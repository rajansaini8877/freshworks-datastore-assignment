package fireworks;

import fireworks.libraries.org.json.simple.*;
import fireworks.libraries.org.json.simple.parser.*;
import java.io.*;

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

    String s = readMasterFile(databaseFolder);
    JSONObject masterObj = null;
    try {
      masterObj = (JSONObject)parser.parse(s);
    } catch(ParseException e) {
      throw new DataStoreException("Database is corrupted!");
    }

    UUID uid = UUID.randomUUID();

    System.out.println(s);

    return true;


  }

  public String readData(String db, String path, String key) throws DataStoreException {
    return null;
  }

  public boolean deleteData(String db, String path, String key) throws DataStoreException {
    return false;
  }

  private boolean createDB(File file) throws DataStoreException {

    if(file.mkdir()) {
      File f = new File(file, "master.json");
      try {
        f.createNewFile();
        return true;
      } catch (IOException e) {
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

  private String readMasterFile(File databaseFolder) throws DataStoreException {

    StringBuilder sb = new StringBuilder();

    File file = new File(databaseFolder, "master.json");

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

}
