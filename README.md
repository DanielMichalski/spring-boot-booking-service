# Property booking service

This is a property booking service with ability to book and block properties.

## Table of Contents

* [Prerequisites](#prerequisites)
* [Running the application](#running-the-application)
    * [On Windows](#on-windows)
    * [On MacOS/ Linux](#on-macos-linux)
* [Testing the API](#testing-the-api)

## Prerequisites

- [Java JDK](https://www.oracle.com/pl/java/technologies/downloads/#java17) version 17+

## Running the application

#### On Windows

```bash
## Build application using Maven Wrapper
mvnw.cmd clean install

## Run Spring boot application using Maven Wrapper or simply run Application class
mvnw.cmd spring-boot:run
```

#### On MacOS/ Linux

```bash
## Build application using Maven Wrapper
./mvnw clean install

## Run Spring boot application using Maven Wrapper or simply run Application class
./mvnw spring-boot:run
```

## Testing the API

### Bookings

#### 1. Create booking

```bash
curl -d '{
  "guestFirstName": "John",
  "guestLastName": "Travolta",
  "startDate": "2027-12-03T00:00:00Z",
  "endDate": "2027-12-06T00:00:00Z"
}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/bookings
```

#### 2. Update booking

```bash
curl -d '{
  "guestFirstName": "Denis_updated",
  "guestLastName": "Carey_updated",
  "startDate": "2025-12-07T00:00:00Z",
  "endDate": "2025-12-08T00:00:00Z"
}' -H "Content-Type: application/json" -X PUT http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/bookings/cdd88bcb-8fc7-4a39-822c-e514150d769e
```

#### 3. Delete booking

```bash
curl -X DELETE http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/bookings/5299e50c-4de4-4d76-95df-412552a9fe38
```

### Property blocks

#### 1. Create property block

```bash
curl -d '{
  "startDate": "2023-12-12T00:00:00Z",
  "endDate": "2023-12-15T00:00:00Z"
}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/blocks
```

#### 2. Update property block

```bash
curl -d '{
  "guestFirstName": "Denis_updated",
  "guestLastName": "Carey_updated",
  "startDate": "2025-12-07T00:00:00Z",
  "endDate": "2025-12-08T00:00:00Z"
}' -H "Content-Type: application/json" -X PUT http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/blocks/2e8d11c2-3c7e-4dd8-9714-dd2e5968b4ed
```

#### 3. Delete property block

```bash
curl -X DELETE http://localhost:8080/api/properties/6c5d63b4-d776-4a9a-a5ac-06244ebfbcdf/blocks/49122e5e-7983-447d-a340-03c4a774bae3
```
