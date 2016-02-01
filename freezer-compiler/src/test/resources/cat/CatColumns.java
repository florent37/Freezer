public enum CatColumns {
  shortName("shortName"),

  name("name"),

  ages("ages"),

  nana("nana");

  private final String column_name;

  CatColumns(String name) {
    this.column_name = name;
  }

  public String getName() {
    return this.column_name;
  }
}
