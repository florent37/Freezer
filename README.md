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

public class MainActivity extends AppCompatActivity {

    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO();

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
    }
}

```
