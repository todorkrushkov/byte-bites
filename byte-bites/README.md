how to setup:

create database in pgadmin

in application.proprties:
spring.datasource.url=jdbc:postgresql://localhost:5432//your database/

spring.datasource.username=//your username

spring.datasource.password=//your password

spring.jpa.hibernate.ddl-auto=create-drop

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.properties.hibernate.format_sql=true

server.port=8080

bonus.threshold = 30.00

bonus.multiplier = 1.10

start the server

for frontend 

install nodejs https://nodejs.org/en

open terminal and write:

cd frontend

npm start

if it not start install dependancies:

npm i /dependency/
