To create executable jar use command 'gradle bootJar' (tested on gradle 5.0)
Then in build/libs there will be test-task-1.0-SNAPSHOT.jar that can be run via comand 'java -jar test-task-1.0-SNAPSHOT.jar'
Swagger UI is located on http://localhost:8080/swagger-ui.html

Application uses h2 db located in file main-db.mv.db that is automatically created in current directory if absent.