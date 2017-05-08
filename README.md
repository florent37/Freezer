# RxAndroidOrm

# Download

[ ![Download](https://api.bintray.com/packages/florent37/maven/rxandroidorm-compiler/images/download.svg) ](https://bintray.com/florent37/maven/rxandroidorm-compiler/_latestVersion)
```java
dependencies {
    compile 'com.github.florent37:rxandroidorm:1.0.0'
    provided 'com.github.florent37:rxandroidorm-annotations:1.0.0'
    annotationProcessor 'com.github.florent37:freezer-compiler:1.0.0'
}
```

A simple & fluent Android ORM, how can it be easier ?
And it's compatible with RxJava2 !

```java
Observable.fromArray(
                new Computer(Computer.WINDOWS, "MasterRace", Arrays.asList(new Software("Photoshop"))),
                new Computer(Computer.WINDOWS, "Gamerz"),
                new Computer(Computer.LINUX, "MasterRace", Arrays.asList(new Software("Gimp"))))
                .flatMap(computerDb::add)
                .subscribe();

        Observable.just(new Computer(Computer.MAC, "Mac Mini"))
                .flatMap(computerDb::add)
                .doOnNext(computer -> computer.getSoftwares().add(new Software("Photoshop")))
                .flatMap(computerDb::update)
                .subscribe();

        computerDb.select()
                .label().equalsTo("MasterRace")
                .or()
                .softwares(SoftwareDatabase.where().name().equalsTo("Photoshop"))

                .asObservable()
                .subscribe(computers -> Log.d(TAG, computers.toString()));
```

# Initialize your app

Don't forget to initialise Freezer in your applicarion:

```java
public class MyApplicarion extends Applicarion {

    @Override public void onCreate() {
        super.onCreate();
        RxAndroidOrm.onCreate(this);
    }

}
```

# Annotate your models

Use Annotations to mark classes to be persisted:

```java
@Model
public class Computer {

    @Id long id;
    String name;
   
    List<Software> softwares;
}
```

## Logging

You can log all SQL queries from entities managers:

```java
computerDb.logQueries((query, datas) -> Log.d(TAG, query) }
```

# TODO

## Enum support

License
--------

    Copyright 2017 Florent37, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

