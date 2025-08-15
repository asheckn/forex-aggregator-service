
# Forex-aggregator-service
Forex Aggregator

## Running the Project

### Prerequisites
- Java 17 or higher
- Maven 3.9.4 or higher
- Docker (optional, for containerized deployment)
- PostgreSQL database (for production use)
- Make sure you have postgres running with the DB wiremit and default postgres username and password for running the service directly or alternatively use the docker setup

### Running Directly
1. Clone the repository:
   ```bash
   git clone https://github.com/asheckn/forex-aggregator-service.git
   cd forex-aggregator-service
   

2. Build the project:
   ```bash
   mvn clean package
   ```
   

3. Run the application:
   ```bash
   java -jar target/forex-aggregator-service.jar
   ```

4. Access the application at `http://localhost:8080/swagger-ui/index.html#/`.

### Running with Docker
1. Build the Docker image and run:
   ```bash
   docker docker compose up --build
   ```

2. Access the application at `http://localhost:8080/swagger-ui/index.html#/`.


### Additional Notes
- Ensure port `8080` is available on your system.
- Modify the `dockerfile` or application properties if custom configurations are needed.


### Credentials
- Default username: `admin@mail.com`
- Default password: `admin123#`

- You can register a new user to test client authentication.
- For Historical Forex Rates, use YYYY-MM-DD format for the `startDate` and `endDate` parameters .

### Authentication Flow 
- The application uses JWT for authentication.
- Users can register and log in to obtain a JWT token.
- The token must be included in the `Authorization` header for protected endpoints with Bearer prefix
- There are two roles: `CLIENT` and `ADMIN`.
- `CLIENT` can access public endpoints and their own data.
- `ADMIN` can access all endpoints and manage users they can create and add currencies and markups
- 


### Rate Aggregation Logic 
- The application aggregates forex rates from multiple providers namely frankfurter ,freecurrencyapi and exchangerate-api, the first does not require an API key is open while the other require API keys which can be set in the application properties file.
- The rate fetching logic is scheduled to run every 10000 minutes
- When triggered it checks to see if there are any rates in the Database first then check if any of the rates are not valid , 
- Invalid rates are those that are older than 24 hours or have not been updated in the last 24 hours
- it then get those that need updating and attempts to fetch from the API ,
- when fetching it get a combined Map of rates at once while minimizing the number of API calls made to avoid rate limits
- when  fetched it will then try and match thr rates for each curency pair using a number of methods ,
- First method is direct Method eg, USD to GBP, as usd is base currency the rate should be available directly
- Second is the inverse rate eg if i know USD to ZAR then I know ZAR to USD
- Lastly cross rating eg if i know USD to GBP and GBP to ZAR then I can calculate USD to ZAR
- The application will then save the rates to the database with the added mark up and return the rates to the user

### Design Decisions
- Currencies and Markups are stored in the database for persistence. and to make it easier to expand and add more currencies and markups
- For markup the most recent markup is used as the effective markup
- The application uses a layered architecture with controllers, services, and repositories to separate concerns. the services are split into interface and implementation 
-  Spring Boot for rapid development and ease of configuration.
Swagger is used for API documentation and testing.
and PostgreSQL as the database for persistence.
- 





