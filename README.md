# Global Shipping Platform

A lightweight platform to manage customers, ports, vessels, sailings, and port calls for global shipping operations.

## V1 Features
- Customer Management
- Port Management
- Vessel Management
- Sailing Management
- Port Call Management

## Business Workflow
1. Create Sailing
2. Assign Vessel
3. Add Port Calls
4. Schedule Sailing

## Validation Rules
- Duplicate IMO prevented
- Duplicate LOCODE prevented
- Duplicate Sailing Number prevented
- Duplicate Port Sequence prevented
- Arrival must be after Departure
- Continuous PortCall sequence enforced
- Vessel required before scheduling
- Minimum two Port Calls required before scheduling

## REST APIs
- CRUD APIs
- Global Exception Handling
- DTO pattern
- Service layer
- Repository layer

## Getting Started
Prerequisites:
- Java 11+ (or project-specific JDK)
- Maven or Gradle (depending on project config)
- PostgreSQL (or configured RDBMS)

Quick start (example):
1. Configure database settings in application.properties or application.yml
2. Build the project:
   - Maven: `mvn clean install`
   - Gradle: `./gradlew build`
3. Run the application:
   - `java -jar target/<artifact>.jar` (or run via your IDE)

## API
Describe endpoints here (example):
- GET /api/v1/sailings
- POST /api/v1/sailings
- GET /api/v1/vessels
- ...

(If you want, I can add a full API reference or example curl commands.)

## Contributing
1. Fork the repo
2. Create a feature branch
3. Open a pull request with a description of your changes

## License
Specify the project license here (e.g., MIT).
