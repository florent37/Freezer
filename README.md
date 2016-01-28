# Freezer

[![Build Status](https://travis-ci.org/florent37/Freezer.svg?branch=master)](https://travis-ci.org/florent37/Freezer)

[![logo freezer](https://raw.githubusercontent.com/florent37/Freezer/master/freezer-logo.png)](https://github.com/florent37/Freezer)

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
UserEntityManager userEntityManager = new UserEntityManager();

User user = ... // Create a new object
userEntityManager.add(user);
```

#Querying

Freezer query engine uses a Fluent interface to construct multi-clause queries.

##Simple

To find all users you would write :
```java  
List<User> allUsers = userEntityManager.select()
                             .asList();
```
                                                  
To find an user with 3 years you would write:             
```java                              
User user3 = userEntityManager.select()
                    .age().equalsTo(3)
                    .first();
```

##Complex

To find a user named "florent", or having a cat short named "Java" or having a dog named "Sasha" you would write:             
```java  
List<User> allUsers = userEntityManager.select()
                                .name().equalsTo("florent")
                             .or()
                                .cat(CatEntityManager.where().shortName().equalsTo("Java"))
                             .or()
                                .dogs(DogEntityManager.where().name().equalsTo("Sasha"))
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

The QueryBuilder offers various aggregation methods

```java
float agesSum      = userEntityManager.select().sum(UserColumns.age);
float agesAverage  = userEntityManager.select().average(UserColumns.age);
float ageMin       = userEntityManager.select().min(UserColumns.age);
float ageMax       = userEntityManager.select().max(UserColumns.age);
int count          = userEntityManager.select().count();
```

#Entities

Freezer made it possible, yes you can design your entities as your wish

```java
@Model
public class MyEntity {

    //primitives
    [ int / float / boolean / String ] field;

    //arrays
    [ int[] / float[] / boolean[] / String[] ] array; 
    
    //collections
    [ List<Integer> / List<Float> / List<Boolean> / List<String> ] collection;
    
    //One To One
    MySecondEntity child;
    
    //One To Many
    List<MySecondEntity> childs;
}
```

#Logging

You can log all SQL queries from Entities Managers

```java
userEntityManager.logQueries((query, datas) -> Log.d(TAG, query) }
```

#It's always better with a context

Don't forget to attach Freezer to your application

```java
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Freezer.onCreate(this);
    }

}
```

#TODO

- Update an entry
- Adding SqlLiterHelper onUpgrade
- Adding some selectors operations (like, ...)
- Adding Observable support
- Provide an Asynchronous API
- Support dates
- Adding @Ignore annotation
- Unit tests

#A project initiated by Xebia

This project was first developed by Xebia and has been open-sourced since. We will continue working and investing on it.
We encourage the community to contribute to the project by opening tickets and/or pull requests.

[![logo xebia](https://raw.githubusercontent.com/florent37/Freezer/master/logo_xebia.jpg)](http://www.xebia.fr/)

#Download

[ ![Download](https://api.bintray.com/packages/florent37/maven/freezer-compiler/images/download.svg) ](https://bintray.com/florent37/maven/freezer-compiler/_latestVersion)
```java
buildscript {
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  provided 'com.xebia.android.freezer:freezer-annotations:1.0.0'
  apt 'com.xebia.android.freezer:freezer-compiler:1.0.0'
}
```

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

