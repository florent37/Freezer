package cat;

public enum CatColumns {
  shortName("shortName");

  private final String column_name;

  CatColumns(String name) {
    this.column_name = name;
  }

  public String getName() {
    return this.column_name;
  }
}
