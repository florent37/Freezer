package cat;

import fr.xebia.android.freezer.DataBaseModel;

public class CatEntity extends Cat implements DataBaseModel {
  long _id;

  public long getDatabaseModelId() {
    return _id;
  }

  public void setDatabaseModelId(long id) {
    this._id = id;
  }
}
