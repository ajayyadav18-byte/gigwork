# GigWork

GigWork is a server-rendered Spring Boot application using Java 26, Thymeleaf,
Spring Security, JPA, and MySQL.

## Run locally

1. Start a local MySQL server and create the `gigwork_db` database and a local
   account with permission to use it.
2. Set the database variables in your shell. To avoid saving the password in
   shell history, enter it with the hidden prompt:

   ```sh
   export DB_URL='jdbc:mysql://localhost:3306/gigwork_db'
   export DB_USERNAME='gigwork_user'
   read -r -s -p 'Local MySQL password: ' DB_PASSWORD
   printf '\n'
   export DB_PASSWORD
   ./mvnw spring-boot:run
   ```

Local development keeps the existing `ddl-auto=update` behavior. It does not
copy data to or connect to Aiven unless you change `DB_URL` and the credentials.

## Connect to Aiven MySQL

The production profile reads credentials only from environment variables. In
the Aiven Console, open the `gigwork-mysql` service and confirm it is Running.
Use its connection information to set:

```text
DB_URL=jdbc:mysql://<host>:<port>/<database>?sslMode=REQUIRED
DB_USERNAME=<Aiven username>
DB_PASSWORD=<Aiven password>
SPRING_PROFILES_ACTIVE=prod
```

For the supplied service, the host, port, database, and username are shown on
the service's Connection information card. Do not put the password in this
README, `.env.example`, Java source, or Git. The `.env` patterns are ignored by
Git and Docker; `.env.example` contains placeholders only.

MySQL Connector/J's `sslMode=REQUIRED` makes the connection fail if TLS is not
available. The connector is managed by the Spring Boot dependency BOM. To run
locally against Aiven without saving the password in shell history:

```sh
export DB_URL='jdbc:mysql://<host>:<port>/<database>?sslMode=REQUIRED'
export DB_USERNAME='<Aiven username>'
read -r -s -p 'Aiven MySQL password: ' DB_PASSWORD
printf '\n'
export DB_PASSWORD
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

The `prod` profile uses Hibernate `validate` and Flyway. On a new empty database,
Flyway creates the three application tables from the checked-in versioned
migration. If Flyway encounters a non-empty schema without migration history,
it records version 1 as the baseline and skips the initial create-table
migration; Hibernate then validates that the existing schema matches the
entities. No tables or rows are dropped. If validation fails, stop and inspect
the schema before adding a follow-up migration. Do not switch production to
`ddl-auto=create`, `create-drop`, or `update` to bypass a validation error.

The initial migration creates `users`, `gigs`, and `gig_applications`; it does
not copy data from local `gigwork_db`. Existing local records need a separately
planned export/import after confirming which records to keep and checking for
duplicate emails/applications. No migration of local data is performed by
application startup.

## Tests

Run the isolated H2 integration tests with:

```sh
./mvnw clean test
```

The tests cover registration/password hashing and login, profile ownership and
validation, role-gated admin routes, gig creation/search/moderation, and the
application workflow. They do not prove a live Aiven connection or data
migration.

## Feature-completion audit

The public home page is a marketing preview. Its sample listings and local
search/category filters are illustrative only; actual database-backed listings
are available after login at `/gigs`. The preview has no save action because
those sample listings are not real database records.

| Area | Existing implementation and verification |
| --- | --- |
| Registration and login | Validated registration, BCrypt password hashing, case-insensitive email login, and a generic login error; covered by integration tests. |
| Dashboard and navigation | Dashboard links lead to implemented routes; admin link is role-scoped; authenticated and guest logo destinations are checked across templates. |
| Profiles | Authenticated users can view and update their own display name. Email and role are read-only; blank/oversized names, ownership, persistence, and CSRF are tested. |
| Marketplace | Authenticated users can browse open database listings, search title/description/category/location, filter categories, and see an empty state. Removed gigs are hidden. |
| Posting and details | Server-side field and budget validation, creator association, persistence, open detail view, and missing/removed listing handling are tested. |
| Applications | CSRF-protected submission, duplicate/self-application prevention, applicant/owner-scoped views, owner-only pending status decisions, and persistence are tested. |
| Admin moderation | `ROLE_ADMIN`-only panel, summary counts, and reversible remove/reopen status changes without deleting listings are tested. |
| Production database | Environment-only MySQL settings, TLS URL support, Flyway schema migration, and Hibernate validation are configured. Live Aiven connectivity and cloud persistence are still blocked pending private credential entry. |
| Build and deployment | Maven tests and executable jar build can be verified locally. Docker build is not verified because Docker is unavailable; deployment remains intentionally pending. |
| Repository and data | The workspace is secret-scanned and pushed to the private [GitHub repository](https://github.com/ajayyadav18-byte/gigwork). Any local-to-cloud data migration remains pending. Application startup does not copy or delete local records. |

The integration suite uses an isolated H2 database and covers template rendering,
database persistence, authentication, authorization, CSRF, navigation, gig
creation/search, applications, profiles, and moderation. Landing-page demo
listings are not persisted jobs and are labeled as illustrative.

### Completion checklist

- [x] Existing routes, controllers, services, repositories, entities, templates,
  and dashboard links audited against the original project requirements.
- [x] Core registration, login, marketplace, profile, gig posting, applications,
  and admin moderation covered by integration tests and runtime HTTP checks.
- [x] Public demo controls that only changed appearance removed; real gig detail
  pages display the stored posting date.
- [ ] Live Aiven connection and persistence: pending private entry of a rotated
  database password and network verification.
- [x] Local Git repository initialized; the first commit contains the reviewed
  project files and excludes `.env`, build output, and key files.
- [x] Private GitHub repository created and the reviewed `main` branch pushed:
  <https://github.com/ajayyadav18-byte/gigwork>.
- [ ] Docker image build: not run because Docker is unavailable on this machine.
- [ ] Local MySQL data import: no data was copied; inspect and decide which
  records should be migrated before importing to Aiven.
- [ ] Live deployment: intentionally not started; review cost and service
  settings before authorizing it.

### Administrator provisioning

Registration intentionally creates `USER` accounts only. There is no default
admin or web form that changes account roles. Provision an administrator with a
reviewed one-time database operation after deployment: first back up the
database and select the intended account by its unique email/ID; update only
that confirmed row's `role` to `ADMIN`; then re-read the row and verify the
affected-row count. Never expose role changes through the application or put
admin credentials in source control.

## Build and run the container locally

Build and run with Docker:

```sh
docker build -t gigwork .
docker run --rm -p 8080:8080 \
  -e DB_URL -e DB_USERNAME -e DB_PASSWORD \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e PORT=8080 gigwork
```

The image is multi-stage, uses the project's Java 26 build, and runs as a
non-root user. It does not contain `.env` files or credentials. The application
uses `PORT` when supplied; otherwise it listens on 8080.

## Deployment preparation: Render

Render supports Docker-based web services and Java containers. The app remains
one service: Spring Boot serves both the API and Thymeleaf pages. No separate
frontend host is required.

Before creating a service:

1. Confirm the Aiven service is Running and decide whether any local data needs
   a one-time migration. The supplied Aiven service is a separate database and
   does not contain local data automatically.
2. The reviewed project is in the private repository at
   <https://github.com/ajayyadav18-byte/gigwork>. Do not add `.env`, database
   dumps, passwords, or certificate/key files.
3. In Render, choose **New → Web Service → Git Provider**, select the private
   repository, and choose **Docker** as the runtime. Use the repository root
   Dockerfile. The container reads Render's `PORT` environment variable.
4. Before creating/deploying the service, add these environment variables in
   Render's Environment settings: `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`,
   `DB_USERNAME`, and `DB_PASSWORD`. Set the JDBC URL to the Aiven host/port/
   database with `?sslMode=REQUIRED`; enter the password directly in Render's
   secret value field.
5. Select a compute plan only after reviewing its current price and limits.
   Render's web-service plan may incur charges. Do not enable paid add-ons or
   create the service until you explicitly approve the plan and first deploy.
6. Configure `/` as the health-check path if desired. Verify the deploy logs,
   then test registration, login, profile, gig creation, and applications on
   the public HTTPS URL.

The Aiven trial banner applies credits to non-free Aiven services; it does not
mean this application or a Render service is free. Recheck the Aiven plan and
Render pricing in their dashboards before committing to paid compute. Free
Aiven MySQL plans can power off after inactivity and have limited resources.

## Security notes

- Production must use HTTPS; the `prod` profile sets secure, HTTP-only,
  same-site session cookies and trusts forwarded headers from the host.
- CSRF protection remains enabled for form posts.
- Production hides server exception details from browser responses.
- Never commit credentials, `.env` files, database exports, or private keys.
