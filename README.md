# Android ORM

Use Annotations to mark classes to be Persisted

```java
@Model
public class User {
    int age;
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
}
```

#Persist datas

Persist your data easily

```java
UserORM userORM = new UserORM();

User user = ... // Create a new object
userORM.add(user);
```

#Querying

##Simple
```java  
List<User> allUsers = userORM.select()
                             .asList();
                             
User user3 = userORM.select()
                    .age().equalsTo(3)
                    .first();
```

##Complex

Android-ORM query engine uses a Fluent interface to construct multi-clause queries

```java  
List<User> allUsers = userORM.select()
                                .name().equalsTo("florent")
                             .or()
                                .cat(CatORM.where().shortName().equalsTo("Java"))
                             .or()
                                .dogs(DogORM.where().name().equalsTo("Sasha"))
                             .asList();
```

##Selectors

```java
//strings
     .name().equalsTo("florent")
     .name().notEqualsTo("kevin")
     .name().contains("flo")
//numbers
     .age().equalsTo(10)
     .age().notEqualsTo(30)
     .age().greatherThan(5)
     .age().between(10,20)
//booleans
     .hacker().equalsTo(true)
     .hacker().isTrue()
     .hacker().isFalse()
```

##Aggregation

```java
float agesSum      = userORM.select().sum(UserColumns.age);
float agesAverage  = userORM.select().average(UserColumns.age);
float ageMin       = userORM.select().min(UserColumns.age);
float ageMax       = userORM.select().max(UserColumns.age);
int count          = userORM.select().count();
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

#Logging

```java
userORM.logQueries((query, datas) -> Log.d(TAG, query) }
```

#TODO

- Adding Observable support
- Update an entry
- Adding SqlLiterHelper onUpgrade
- Adding some selectors operations (like, ...)
- Adding array support (int[], List<Integer>, ...)

#Contributing
This project was first developed by Xebia and has been open-sourced since. We will continue working and investing on it.
We encourage the community to contribute to the project by opening tickets and/or pull requests.

License
--------

    Copyright 2015 Xebia, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

