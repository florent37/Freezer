import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import fr.xebia.android.freezer.Freezer;
import fr.xebia.android.freezer.QueryBuilder;
import fr.xebia.android.freezer.QueryLogger;

public class CatQueryBuilder extends QueryBuilder {
  public CatQueryBuilder() {
    super();
  }

  public CatQueryBuilder(boolean named, QueryLogger logger) {
    this();
    this.named = named;
    this.logger = logger;
  }

  public QueryBuilder.StringSelector<CatQueryBuilder> shortName() {
    return new QueryBuilder.StringSelector<CatQueryBuilder>(this, CatColumns.shortName.getName());
  }

  public QueryBuilder.StringSelector<CatQueryBuilder> name() {
    return new QueryBuilder.StringSelector<CatQueryBuilder>(this, CatColumns.name.getName());
  }

  public QueryBuilder.NumberSelector<CatQueryBuilder> nana() {
    return new QueryBuilder.NumberSelector<CatQueryBuilder>(this, CatColumns.nana.getName());
  }

  public QueryBuilder.ListNumberSelector<CatQueryBuilder> ages() {
    return new QueryBuilder.ListNumberSelector<CatQueryBuilder>(this, CatColumns.ages.getName());
  }

  public CatQueryBuilder or() {
    super.appendOr();
    return this;
  }

  public CatQueryBuilder and() {
    super.appendAnd();
    return this;
  }

  public CatQueryBuilder beginGroup() {
    super.appendBeginGroup();
    return this;
  }

  public CatQueryBuilder endGroup() {
    super.appendEndGroup();
    return this;
  }

  public List<Cat> asList() {
    return execute();
  }

  public Cat first() {
    List<Cat> objects = asList();
    if(objects.isEmpty()) return null;
    else return objects.get(0);
  }

  public CatQueryBuilder sortAsc(CatColumns column) {
    super.appendSortAsc(" CAT .",column.getName());
    return this;
  }

  public CatQueryBuilder sortDesc(CatColumns column) {
    super.appendSortDesc(" CAT .",column.getName());
    return this;
  }

  public float sum(CatColumns column) {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select sum(CAT." + column.getName() + ") from CAT " + constructQuery(), constructArgs());
    cursor.moveToNext();
    float value = cursor.getFloat(0);
    cursor.close();
    Freezer.getInstance().close();
    return value;
  }

  public float min(CatColumns column) {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select min(CAT." + column.getName() + ") from CAT " + constructQuery(), constructArgs());
    cursor.moveToNext();
    float value = cursor.getFloat(0);
    cursor.close();
    Freezer.getInstance().close();
    return value;
  }

  public float max(CatColumns column) {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select max(CAT." + column.getName() + ") from CAT " + constructQuery(), constructArgs());
    cursor.moveToNext();
    float value = cursor.getFloat(0);
    cursor.close();
    Freezer.getInstance().close();
    return value;
  }

  public float average(CatColumns column) {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select avg(CAT." + column.getName() + ") from CAT " + constructQuery(), constructArgs());
    cursor.moveToNext();
    float value = cursor.getFloat(0);
    cursor.close();
    Freezer.getInstance().close();
    return value;
  }

  public int count() {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    Cursor cursor = db.rawQuery("select count(distinct(CAT._id)) from CAT " + constructQuery(), constructArgs());
    cursor.moveToNext();
    int value = cursor.getInt(0);
    cursor.close();
    Freezer.getInstance().close();
    return value;
  }

  private List<Cat> execute() {
    SQLiteDatabase db = Freezer.getInstance().open().getDatabase();
    String query = "select distinct CAT.* from CAT " + constructQuery();
    String[] args = constructArgs();
    if(logger != null) logger.onQuery(query,args);
    Cursor cursor = db.rawQuery(query, args);
    List<Cat> objects = CatCursorHelper.get(cursor,db);
    cursor.close();
    Freezer.getInstance().close();
    return objects;
  }
}
