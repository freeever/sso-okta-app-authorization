# sso-okta-app-authorization
This is a sample application which use OKTA for authentication, and application itself handles the authorization.

Backend-Initiated OAuth2 with OKTA
* Spring Boot handles login redirects to OKTA
* OKTA returns code → backend exchanges for access/refresh/id tokens
* Backend maintains authenticated session
* Angular never directly communicates with OKTA
* Backend enforces role-based authorization from our DB

Backend Microservices Structure:
* OKTA Application 1: UserManagementApp
  * user-config-server (9004)
  * user-eureka-server (9761)
  * user-gateway (9001)
  * user-profile-svc (9002)
  * user-admin-svc (9003)
* OKTA Application 2: CourseManagementApp
  * course-config-server (9015)
  * course-eureka-server (9672)
  * course-gateway (9011)
  * course-query-svc (9012)
  * course-management-svc (9013)
  * course-registration-svc (9014)
