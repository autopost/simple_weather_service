### Introduction

This is a simple application that requests its data from [OpenWeather](https://openweathermap.org/) and stores the result in a database. 

### Summary

- javax validation
- org.modelmapper for mapping entities
- flyway for data migration
- simple docker file to highlight that app can be containerized
- mock server for integration tests to mock remote weather service
- unit tests for service class
- exception handling
- changed project structure
etc


### Assumptions 
- some properties should be store in secret manager, this is not included to keep the project
- I use in-memory H2 instead of Postgres just to keep the project runnable from repo
- Weather entity should not be a return type for the controller but Dto should be used instead, left this as is for the initial  version 
- BigDecimal  can be used instead double to avoid of unpredicted rounding
- added application-prod.properties to highlight that some sensitive properties can be substituted during deployment

P.S. Do not forget to add appid in application.properties