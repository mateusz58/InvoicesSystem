#!/usr/bin/env bash
curl -X POST "http://localhost:8097/users/register" -H  \
"accept: application/json" -H  \
"Content-Type: application/json"
-d "{  \"id\": 0,  \"active\": 1,  \"email\": \"sample@mail.com\",  \"lastName\": \"Doe\",  \"name\": \"John\",  \"password\": \"admin\",  \"roles\": [    {      \"id\": 0,      \"roleName\": \"ADMIN\"    }  ]}"