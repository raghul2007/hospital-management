# Case Study 17 – Hospital Patient Treatment Status

Spring Boot 3 · Spring Data JPA · MySQL · Swagger/OpenAPI · animated hospital-themed dashboard.

## What it does
Input from the UI: `1001-BLOOD:C,1002-XRAY:P,1003-SCAN:C,1001-BLOOD:P`

* splits by comma → `PatientID-TEST:STATUS`
* converts `C → COMPLETED`, `P → PENDING`
* same patient + test repeated → **last occurrence overwrites** (1001-BLOOD ends as PENDING)
* NULL / empty / malformed ID, Test or Status (or a status other than C/P) → `400 Bad Request`,
  and nothing is saved (single transaction)
* table `PATIENT_TREATMENT` (`ID` LONG, `TEST` VARCHAR, `STATUS` VARCHAR; primary key = ID + TEST)

## Run
Requirements: JDK 17+, Maven 3.9+.

**Option A – MySQL (as per assignment)**
1. Start MySQL; edit username/password in `src/main/resources/application.properties`
   (the DB `hospital_db` is auto-created).
2. `mvn spring-boot:run`

**Option B – no database needed (in-memory H2)**
`mvn spring-boot:run -Dspring-boot.run.profiles=h2`

Then open:
* Dashboard UI → http://localhost:8080/
* Swagger UI   → http://localhost:8080/swagger-ui.html
* Tests        → `mvn test`

## REST API
| Method | URL | Purpose |
|---|---|---|
| POST | `/api/treatments` body `{"input":"1001-BLOOD:C,..."}` | parse, validate, convert, save/update (201) |
| GET | `/api/treatments/{id}/{test}` | status by Patient ID + Test (404 if missing) |
| GET | `/api/treatments/patient/{id}` | all tests of one patient |
| GET | `/api/treatments` | all records |

Error body: `{ timestamp, status, error, message, path }`.

## Postman
Import `postman/Hospital_Treatment.postman_collection.json` (has test scripts; run the collection top-to-bottom).

## Structure
```
entity/      PatientTreatment, PatientTreatmentId (composite key)
repository/  PatientTreatmentRepository (JpaRepository)
service/     PatientTreatmentService (parse → validate → convert → dedupe → save)
controller/  PatientTreatmentController (POST + GET)
exception/   InvalidInputException, TreatmentNotFoundException, GlobalExceptionHandler
util/        TreatmentInputParser
config/      OpenApiConfig (Swagger)
static/      index.html (animated UI)
```
