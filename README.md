# Student Registration

The system under test for the API contract lab and the UI testing lab.
It is a Spring Boot REST API that manages students, courses, and course rosters, with a React web UI.

## What you need

- JDK 17 or newer
- Maven
- Google Chrome, for the UI tests
- Postman and Newman (`npm install -g newman`), for the API lab

You don't need to install Node. The first build downloads its own copy into `frontend/node`.

## Start the app

From the project root, run:

```bash
mvn spring-boot:run
```

The app is ready when the log says `Started StudentRegDemo`. The first run takes a few minutes, because it builds the web UI.

| What | Where |
| --- | --- |
| Web UI | `http://localhost:8080` |
| API base URL | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI contract (JSON) | `http://localhost:8080/v3/api-docs` |
| OpenAPI contract (YAML) | `http://localhost:8080/v3/api-docs.yaml` |
| H2 database console | `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, password `password`) |

The web UI's source is in `frontend`. If you want to change it and see it reload live, run `npm run dev` in that folder while the app is running, and open `http://localhost:5173`.  (This will require installing Node, or a more complicated run through Maven:  'mvn frontend:npm -Dfrontend.npm.arguments="run dev"')

## Run the UI tests

Stop the app if it's running, then run:

```bash
mvn verify
```

Maven builds everything, starts the app, runs every test class whose name ends in `IT` (Stands for Integration Test, a maven convention for Failsafe tests), and stops the app. Screenshots of failed tests are saved in `target/ui-screenshots`.  The following options may be added to the mvn verify command:

| Option | Effect |
| --- | --- |
| `-Dheadless=false` | Shows the browser while the tests run |
| `-Dit.test=StudentListSmokeIT` | Runs only one test class |
| `-Dlab.mutant=P1` | Runs the app with a deliberately faulty UI, to check that your tests notice |
| `-Dui.excludedGroups=examples,defect` | Skips tests tagged `examples` or `defect` (the default skips only `examples`) |

The UI tests and their starter kit are in `src/test/java/com/baarsch_bytes/studentRegDemo/ui`.

## Simulated network latency

The app delays every API response by a random 0.1 to 1.5 seconds, so the UI behaves as it would on a real network.
Tests must wait for what they need instead of assuming the page is fast.

## The contract

The live contract is generated from the OpenAPI annotations in the controllers and DTOs.
`contract/openapi.json` and `contract/openapi.yaml` are saved copies of version 1.0.0, which you use as a fixed baseline.

## Test data

The database is in memory. It starts empty, and restarting the app clears it.
Tests should create the data they need, with names that are unique to each run.
Don't assume a particular id, because ids keep increasing until the app restarts.
