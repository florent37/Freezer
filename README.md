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
userORM.add(new User(25, "florent", new Cat("Java"), Arrays.asList(new Dog("Medor"), new Dog("Milou"))));
```

#Querying

##Simple
```java  
List<User> allUsers = userORM.selectWhere()
                             .asList()
                             .toString()
```

##Complex

Android-ORM query engine uses a Fluent interface to construct multi-clause queries

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

##Aggregation

```java
float agesSum      = userORM.selectWhere().sum(UserColumns.age);
float agesAverage  = userORM.selectWhere().average(UserColumns.age);
float ageMin       = userORM.selectWhere().min(UserColumns.age);
float ageMax       = userORM.selectWhere().max(UserColumns.age);
int count          = userORM.count();
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

#Contributing
This project was first developed by Xebia and has been open-sourced since. We will continue working and investing on it.
We encourage the community to contribute to the project by opening tickets and/or pull requests.

[![logo xebia](https://raw.githubusercontent.com/florent37/Android-ORM/master/logo_xebia.jpg)](http://www.xebia.fr/)

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

