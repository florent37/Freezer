import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.xebia.android.freezer.PrimitiveCursorHelper;

public final class CatCursorHelper {
  public static Cat fromCursor(Cursor cursor, SQLiteDatabase db) {
    return fromCursor(cursor,db,0);
  }

  public static Cat fromCursor(Cursor cursor, SQLiteDatabase db, int start) {
    Cat object = new CatEntity();
    ((fr.xebia.android.freezer.DataBaseModel)object).setDatabaseModelId(cursor.getLong(start));
    object.shortName = cursor.getString(cursor.getColumnIndex("shortName"));
    object.name = cursor.getString(start+2);
    object.nana = cursor.getLong(start+3);
    object.ages = PrimitiveCursorHelper.getLongsArray(db,((fr.xebia.android.freezer.DataBaseModel)object).getDatabaseModelId(),"ages");

    return object;
  }

  public static ContentValues getValues(Cat object, String name) {
    ContentValues values = new ContentValues();
    if(name != null) values.put("_field_name",name);
    values.put("shortName",object.shortName);
    values.put("name",object.name);
    values.put("nana",object.nana);
    return values;
  }

  public static List<Cat> get(Cursor cursor, SQLiteDatabase db) {
    List<Cat> objects = new ArrayList<Cat>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
        Cat object = fromCursor(cursor,db);
        objects.add(object);
        cursor.moveToNext();
    }
    return objects;
  }

  public static long insert(SQLiteDatabase database, Cat object) {
    long objectId = database.insert("CAT", null, getValues(object,null));
    if(object.ages != null) PrimitiveCursorHelper.addLongs(database,objectId,"ages",object.ages);
    return objectId;
  }

  public static void insert(SQLiteDatabase database, List<Cat> objects) {
    for(Cat object : objects) insert(database,object);
  }

  public static void insertForUser(SQLiteDatabase database, Cat child, long parentId, String variable) {
    if(child != null) {
      long objectId = insert(database,child);
      database.insert("USER_CAT", null, getUSER_CATValues(parentId, objectId, variable));
    }
  }

  public static void insertForUser(SQLiteDatabase database, List<Cat> objects, long parentId, String variable) {
    if(objects != null) {
      for(Cat child : objects) {
        insertForUser(database,child, parentId, variable);
      }
    }
  }

  public static ContentValues getUSER_CATValues(long objectId, long secondObjectId, String name) {
    ContentValues values = new ContentValues();
    values.put("user_id",objectId);
    values.put("cat_id",secondObjectId);
    values.put("_field_name",name);
    return values;
  }
}
