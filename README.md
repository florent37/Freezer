# Freezer

Simply add @Model on your object

```java
@Model
public class User {

    int age;
    String name;

    public User() {
    }
    
    ...
}
```

Will generate Daos
```java
UserDAO userDAO = new UserDAO();

userDAO.add(new User(3, "florent"));
userDAO.add(new User(20, "kévin"));
userDAO.add(new User(10, "alex"));

List<User> allUsers = userDAO.selectWhere()
                .asList());

List<User> users = userDAO.selectWhere()
                .ageEquals(3)
                .or()
                .nameEquals("kévin")
                .asList();
```
