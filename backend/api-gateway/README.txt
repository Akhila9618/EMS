Required dependencies

Spring Cloud Gateway
WebFlux
Security
Validation
Actuator
Devtools
Lombok
JWT



Project structure:

api-gateway
|
|-- config
|      |
|      |-- CorsConfig.java
|      |-- RouterConfiguration.java
|
|-- filters
|      |
|      |-- RequestLoggingFilter.java
|      |-- JwtValidationFilter.java
|      |-- ResponseLoggingFilter.java
|
|-- constants
|      |
|      |-- PublicEndpoints.java
|      |-- HeaderConstants.java (optional)
|
|-- util
|      |
|      |-- JwtUtil.java
|      |-- HeaderUtil.java
|
|-- exception
|      |
|      |-- GatewayException.java
|      |-- GlobalExceptionHandler.java
|
|-- dto
|      |
|      |-- ApiResponse.java
|
|-- resources
|      |
|      |-- application.yml

when ever a request arrives the positive flow is:
                       React UI
                        |
                        v
                  API Gateway
                        |
                        v
              RequestLoggingFilter
                        |
                        v
                 JwtValidationFilter
                        |
                        v
                    JwtUtil
                        |
                        v
               PublicEndpoints Check
                        |
                        v
                  HeaderUtil
                        |
                        v
                 RouterConfiguration
                        |
                        v
                 Target Microservice
                        |
                        v
               ResponseLoggingFilter
                        |
                        v
                     React UI
                     
   if we encounter any error during request processing then the flow is :
                       React UI
                        |
                        v
                  API Gateway
                        |
                        v
              RequestLoggingFilter
                        |
                        v
                 JwtValidationFilter
                        |
                        v
                 throw GatewayExceptions()  
                        |
                        v
                 GlobalExceptionHandler
                        |
                        v
                  ApiResponse
                        |
                        v
                     React UI
                     
                     
                     