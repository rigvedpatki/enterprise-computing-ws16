# Reimbursement Backend Application

## Run the app locally

1. Install Java 8 and Maven 3
2. Run `mvn clean package` to build the package
3. Run `java -Dserver.port=8080 -jar target/reimbursement-backend-0.1.jar` to start the app
4. Access the running app in a browser at http://localhost:8080

## Deploy to Elastic Beanstalk

1. Clean up the project's directory `mvn clean`
2. Zip the whole project directory `zip -r app.zip backend/*`
3. Upload the archive to Elastic Beanstalk (over the management console)
4. The API is available at http://reimbursement-backend.elasticbeanstalk.com/
