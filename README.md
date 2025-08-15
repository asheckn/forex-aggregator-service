
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


