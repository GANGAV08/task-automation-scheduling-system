# Task Automation & Scheduling System (Java Spring Boot + Spring boot Scheduling + MySQL)

Two microservices:
- **scheduler-service** — Accepts tasks, schedules webhooks, manages retries & async polling, persists task metadata.
- **executor-service** — Simulates business logic: accepts webhooks (sync or async), exposes `api/status/{id}` for polling, send mail.

**Tech**: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Lombok, Actuator, SpringDoc OpenAPI, Mysql,  Docker Compose.

## Repository setup
To run the services locally with Docker:

```bash
# From the repo root
 1. Open PowerShell or your terminal
 2. Navigate to the project folder

 3. start all services
      docker-compose up
```

Services:
- Scheduler API: http://localhost:8081/swagger-ui.html
- Executor API:  http://localhost:8090/swagger-ui.html
- Mysql (scheduler):  localhost:3307:3306
- Mysql (executor):   localhost:3308:3306

### Sample tasks
1. Send Welcome Email (sync in executor)
2. Notify Admin on New Signup (sync in executor)
3. Trigger Daily Summary Report (async)
4. Security Alert Notification (async)

You can also create new tasks with `POST api/tasks` in the scheduler.

## Design Overview

- **Task Lifecycle**  
  ` PENDING → RUNNING → SUCCESS / FAILED / CANCELLED`

- **Retries**  
  Configurable max retries via application.yml.

- **Async handling**  
  Executor may return  `{ "status": "QUEUED", "check_url": "http://executor:8090/api/status/{id}" }`.  
  Scheduler persists the `check_url` and runs a Scheduller `processRunningOrders` to poll.

- **Recurrence**  
  Supports `NONE`, `HOURLY`, `DAILY`, and `Weekly`. On success, the next run is queued automatically.

- **Persistence**  
  Separate Mysql DBs for each service.

- **Extensibility**  
  Webhook executor is generic; scheduler takes any URL & payload.

## Endpoints

### Scheduler
BaseUrl: `http://localhost:8081/api`
- `POST /tasks` — create a task
- `GET /tasks` — list tasks
- `GET /tasks/{id}` — fetch one task
- `POST /tasks/{id}/cancel` — cancel a task
- OpenAPI: `http://localhost:8081/v3/api-docs`, `http://localhost:8081/swagger-ui.html`
- Actuator: `http://localhost:8081/actuator/health` -  to check a health

### Executor
BaseUrl: `http://localhost:8090/api`
- `POST /send-welcome`
- `POST /notify-admin`
- `POST /daily-summary`
- `POST /security-alert`
- `GET /execution-history/{taskId}`- execution history
- `GET /status/{id}` — async status poll
- OpenAPI: `http://localhost:8090/v3/api-docs`, `http://localhost:8090/swagger-ui.html`
- Actuator:`http://localhost:8090/actuator/health` - to check a health

## Notes
- All webhooks are HTTP POST with JSON body.
- For demo, executor uses random/controlled delays; real systems would do actual work/queues.
- For async tasks, maintain check_url and status polling in scheduler.
- Timezone is UTC by default;
- Security & auth are assumed to be handled by API Gateway / Identity Service in production.
- Configuration-driven: max retries, polling interval, recurrence, etc., should reside in application.yml.
- Use OpenAPI for API docs & testing.
- For demo, “Send Welcome Email” and “Notify Admin” tasks are implemented with basic email templates. Demo credentials used an my alternate email details for sending (configrable), and the admin email is configurable in the application.
- Scheduler polling and task exexcution interval are configurable in the application config file.

## Local development (without Docker)
- Ensure MySql running with matching credentials from `application.yml` files.
- Active Spring profile is configurable in application.yml.
- Build: `mvn clean install` - in each service.
- Run: `java -jar target/*.jar`

## Notes
- Use `application.yml` max retries, polling intervals, task execution interval etc.
- APIs are fully documented via OpenAPI UI.

## Sample reqest to create a task: (after contarization)

```json
{
  "name": "Send Welcome Email",
  "executionTime": "2025-09-04T19:29:09Z",
  "webhookUrl": "http://executor-service:8090/api/send-welcome", //Executor service name
  "payload": {
    "email": "gangawarvishwanath@gmail.com", // mail will send to this mail
    "template": "welcome"
  },
  "recurrence": "NONE"   // options: HOURLY, DAILY, WEEKLY
}

