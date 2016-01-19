# Freezer

Simply add @Model on your objects

```java
@Model
public class User {

    String name;
    List<Car> cars;
    
}
```

```java
@Model
public class Car {

    int color;
    
}
```

Will generate Fridges
```java
UserFridge userFridge = new UserFridge();

userFridge.add(new User("florent", new Car(Color.RED));

List<User> allUsers = userDAO.selectWhere()
                .asList());

User userFlorent = userDAO.selectWhere()
                .nameEquals("florent")
                .first();
```

#Accepted types

##Primitives
- int
- float
- boolean
- String

##One To One

```java
@Model
public class User {

    Car car;

}

```

##One To Many

```java
@Model
public class User {

    List<Car> cars;

}

```


