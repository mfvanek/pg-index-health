## Building and testing

Java >= 11 is required.

1. Clone the repository

       git clone --recursive https://github.com/mfvanek/pg-index-health.git
       cd pg-index-health

2. Build with Gradle
    * On Linux and macOS: `./gradlew build`
    * On Windows: `.\gradlew.bat build`
    
   This will build the project and run tests.
    
By default, [PostgreSQL 15.3 from Testcontainers](https://www.testcontainers.org/) is used to run tests.  
Set `TEST_PG_VERSION` environment variable to use any of other available PostgreSQL version:
```
TEST_PG_VERSION=11.20-alpine
```
List of all available PostgreSQL versions can be found [here](https://hub.docker.com/_/postgres).
