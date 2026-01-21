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
