package cat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import fr.xebia.android.freezer.Freezer;
import fr.xebia.android.freezer.QueryLogger;

public class CatEntityManager {
  QueryLogger logger;

  public CatEntityManager() {
  }

  public static String[] create() {
    return new String[]{"create table CAT (_id integer primary key autoincrement, shortName text)"};
  }

  public static String update() {
    return "";
  }

  public CatQueryBuilder select() {
    return new CatQueryBuilder(false,logger);
  }

  public static CatQueryBuilder where() {
    return new CatQueryBuilder(true,null);
  }

  public long add(Cat object) {
    SQLiteDatabase database = Freezer.getInstance().open().getDatabase();
    long objectId = CatCursorHelper.insert(database,object);
    Freezer.getInstance().close();
    return objectId;
  }

  public void add(List<Cat> objects) {
    for(Cat object : objects) add(object);
  }

  public void delete(Cat object) {
    Freezer.getInstance().open().getDatabase().delete("CAT", "_id = ?", new String[]{String.valueOf(((fr.xebia.android.freezer.DataBaseModel)object).getDatabaseModelId())});
    Freezer.getInstance().close();
  }

  public void deleteAll() {
    Freezer.getInstance().open().getDatabase().execSQL("delete from CAT");
    Freezer.getInstance().close();
  }

  public int count() {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select count(*) from CAT",null);
    cursor.moveToFirst();
    int recCount = cursor.getInt(0);
    cursor.close();
    Freezer.getInstance().close();
    return recCount;
  }

  public void logQueries(QueryLogger logger) {
    this.logger = logger;
  }
}
