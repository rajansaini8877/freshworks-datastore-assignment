package freshworks;

public interface CrdInterface {

  public boolean createDataStore(String db) throws DataStoreException;
  public boolean createDataStore(String db, String path) throws DataStoreException;
  public boolean createData(String db, String path, String key, String value) throws DataStoreException;
  public String readData(String db, String path, String key) throws DataStoreException;
  public boolean deleteData(String db, String path, String key) throws DataStoreException;

}
