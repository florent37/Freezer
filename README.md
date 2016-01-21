# Android ORM

Simply add @Model on your objects

```java
@Model
public class User {
    String name;
    Cat cat;
    List<Dog> dogs;
}
```

```java
@Model
public class Dog {
    String name;
}
```

```java
@Model
public class Cat {
    String shortName;
```

#Add Data
```java
UserORM userORM = new UserORM();
userORM.add(new User("florent", new Cat("Java"), Arrays.asList(new Dog("Loulou"))));
```

#Add Datas
```java
userORM.add(Arrays.asList(
                new User("florent", new Cat("Java"), Arrays.asList(new Dog("Loulou"))),
                new User("kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty"))),
                new User("alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")))
        )
);
```

#Querying

##Simple
```java  
List<User> allUsers = userORM.selectWhere()
                             .asList()
                             .toString()
```

##Complex

```java  
List<User> allUsers = userORM.selectWhere()
                                .nameEquals("florent")
                             .or()
                                .cat(CatORM.where().shortNameEquals("Java"))
                             .or()
                                .dogs(DogORM.where().nameEquals("Sasha"))
                             .asList()
                             .toString()
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
    Cat cat;
}

```

##One To Many

```java
@Model
public class User {
    List<Dog> dogs;
}

```


