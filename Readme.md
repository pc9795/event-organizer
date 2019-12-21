## event-organizer
A project to learn about full stack web development.

**Abstract**

A web application where users can create events and can share with other users. User can also archive past events. 

**Technical Learnings**
* Implemented authentication and authorization using Spring security.
* Created REST resources using Spring boot.
* Used Spring-Data and Hibernate for database related things.
* Used Swagger for API documentation.
* Implemented front end using Angular
* Enabled CORS configuration using Spring security.

**Project directories**
* `backend` - The backend spring boot project
* `frontend` - The frontend angular project

**Could be better**
* The alert component. Could provide a close button and automatic fading after sometimes.
* Unit tests

**Tech stack**
* Spring boot
* Angular
* Postgres

**How to run**
* Pull the repo.
* By default application uses the default database **postgres** if you want to use another one then create one and update
the details in src/main/resources/application.properties.
* Uncomment spring.jpa.hibernate.ddl-auto=create so that it will create all the tables for you. NOTE: After first time 
comment it back else on next run it will again create and wipe all the data.
* We can configure the front end url in the `backend/src/main/resources/custom.properties`. It will remove the CORS
error.
* Run the main spring boot application by running `backend/src/main/java/service.event_org.Main` class. It will start
at a tomcat server at port 8080.
* Go to the `frontend` folder and run `ng serve`.
* We will be notified that a server is started at `http://localhost:4200/`. And we can access this application from the
 browser.
* As this application make use of port 8080 and 4200 so make sure that nothing is running on these ports.
* Check the available API endpoints at http://localhost:8080/swagger-ui.html.
