* https://git.epam.com/epm-cdp/global-java-foundation-program/java-modules/-/blob/master/modules/30.%20Spring-boot/Homework.md
* docker-compose -f docker/postgres/docker-compose.yml up
* docker-compose -f docker/keycloak/docker-compose.yml up
* http://localhost:9000/
* username: admin, password: admin
* create realm: rish-student-system-realm
* create client:
  Client ID: student-api-client-id
  Type: OpenID Connect
  client-secret returned: XDhtGTaKCwfLtTj8MUEC0y9t8lFh9eUu
* create roles:
  USER
  ADMIN
* http://localhost:9000/realms/rish-student-system-realm/.well-known/openid-configuration
  Spring will use this automatically.
* create user:
  username: john
  password: secret

##### Important Commands:
* docker exec -it student-api-db-container bash
* pg_dump -U appuser -h localhost -p 5432 -s -t students student_api_db
* docker exec -it student-api-db-container psql -U appuser -d student_api_db
* drop table students; drop table flyway_schema_history;