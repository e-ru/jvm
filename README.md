# Root project for jvm based languages

## Project configuration

* Run `./gradlew eclipse` to apply formating rules to eclipse
* Setup checkstyle with `jvm/config/checkstyle/checkstyle.xml` in eclipse settings
* (Optional) Setup format on save

## travis

### matrix example

result in 2 separate builds, usefull for multi language builds

```
language: java
jdk: openjdk11
cache:
  directories:
    - .gradle
matrix:
  include:
    - before_script:
        - "cd users-service-jpa"
      script:
        - "../gradlew test"
        - "../gradlew compileJava"
    - before_script:
        - "cd users-service-jpa"
      script:
        - "../gradlew test"
        - "../gradlew compileJava"
on:
  branch: master
```

### output logs

```
  - "cd build/reports/tests/test/classes"
  - "ls"
  - "cat eu.rudisch.authorizationserver.TokenControllerTest.html"
```
