curl -X POST http://localhost:8080/api/students \
-H "Content-Type: application/json" \
-d '{
"name": "Rishabh",
"email": "rishabh@example.com"
}'

curl -X GET http://localhost:8080/api/students

curl -X GET http://localhost:8080/api/students/1

curl -X PUT http://localhost:8080/api/students/1 \
-H "Content-Type: application/json" \
-d '{
"name": "Rishabh Garcha",
"email": "rishabh.garcha@example.com"
}'

curl -X DELETE http://localhost:8080/api/students/1