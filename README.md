[![Java CI with Maven](https://github.com/amal-stack/notebooks-api/actions/workflows/maven.yml/badge.svg)](https://github.com/amal-stack/notebooks-api/actions/workflows/maven.yml)

# Notebooks API

A REST web service for the [NotebooksFX](https://github.com/amal-stack/notebooksfx) app written in Spring Boot.


### Running the API locally
#### On the command line
1. Clone the project using `git clone` and go to the project directory:
```
git clone https://github.com/amal-stack/notebooks-api.git
cd notebooks-api
```
2. Use the Maven `package` command to build and test the project:
```
mvnw package
```
3. Use the `java -jar` command to run the generated jar from the `target` subdirectory (Java 18 or newer):
```
java -jar target/*.jar
```

