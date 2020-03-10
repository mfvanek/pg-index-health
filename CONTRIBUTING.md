## Building and testing

Java >= 8 is required.

1. Clone the repository

       git clone --recursive https://github.com/mfvanek/pg-index-health.git
       cd pg-index-health

1. Build with Gradle
    * On Linux and MacOS: `./gradlew build`
    * On Windows: `.\gradlew.bat build`
    
   This will build the project and run tests.
    
By default [embedded PostgreSQL 10.11 from zonkyio](https://github.com/zonkyio/embedded-postgres) is used to run tests. 
Set `TEST_PG_VERSION` environment variable to use any of other available PostgreSQL version. 
List of all available builds for zonkyio embedded database can be found [here](https://mvnrepository.com/artifact/io.zonky.test.postgres/embedded-postgres-binaries-bom). 
In case of errors with initializing embedded database, please, consult [Troubleshooting section of Embedded Postgres project](https://github.com/zonkyio/embedded-postgres#troubleshooting).  
