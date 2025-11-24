Sure! Here’s a **complete, minimal example** with 3 Spring Boot projects for your microservices architecture using:

* **Eureka Server**
* **User Service** (provider)
* **Order Service** (client calling User Service via Feign)

---

# Diagram of the architecture

```plaintext
             +---------------------+
             |     Eureka Server    |  (port 8761)
             +----------+----------+
                        ^
        Registers        |     Registers
                        |
      +-----------------+------------------+
      |                                    |
+-------------+                     +--------------+
| User Service|                     | Order Service| (port 8082)
| (port 8081) |                     +--------------+
+------+------+
       |
Feign calls "/users/{id}"
       |
+------------------------+
|       Client           |
| (calls Order Service)  |
+------------------------+
```

---

# 1. Eureka Server

---

### `pom.xml` dependencies

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>Hoxton.SR12</version> <!-- compatible with Spring Boot 2.2.x -->
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

### `EurekaServerApplication.java`

```java
package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(EurekaServerApplication.class, args);
  }
}
```

---

### `application.yml`

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false

spring:
  application:
    name: eureka-server
```

---

# 2. User Service

---

### `pom.xml` dependencies

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>Hoxton.SR12</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

### `UserServiceApplication.java`

```java
package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }
}
```

---

### `User.java` (model)

```java
package com.example.userservice.model;

public class User {
  private Long id;
  private String name;
  private String email;

  public User() {}

  public User(Long id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
```

---

### `UserController.java`

```java
package com.example.userservice.controller;

import com.example.userservice.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/users/{id}")
  public User getUserById(@PathVariable Long id) {
    // For demo, returning a dummy user
    return new User(id, "John Doe", "john.doe@example.com");
  }
}
```

---

### `application.yml`

```yaml
spring:
  application:
    name: user-service

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

# 3. Order Service (calls User Service via Feign)

---

### `pom.xml` dependencies

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>Hoxton.SR12</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

### `OrderServiceApplication.java`

```java
package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }
}
```

---

### `UserClient.java` (Feign client interface)

```java
package com.example.orderservice.client;

import com.example.orderservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")  // Eureka service name
public interface UserClient {
  @GetMapping("/users/{id}")
  User getUserById(@PathVariable("id") Long id);
}
```

---

### `User.java` (model)

```java
package com.example.orderservice.model;

public class User {
  private Long id;
  private String name;
  private String email;

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
```

---

### `OrderController.java`

```java
package com.example.orderservice.controller;

import com.example.orderservice.client.UserClient;
import com.example.orderservice.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  private final UserClient userClient;

  public OrderController(UserClient userClient) {
    this.userClient = userClient;
  }

  @GetMapping("/orders/{orderId}")
  public String getOrderWithUser(@PathVariable Long orderId) {
    User user = userClient.getUserById(1L);  // For demo, fixed user id 1
    return "Order #" + orderId + " is placed by " + user.getName() + " (" + user.getEmail() + ")";
  }
}
```

---

### `application.yml`

```yaml
spring:
  application:
    name: order-service

server:
  port: 8082

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

# How to run & test

1. Start **Eureka Server** (port 8761)
2. Start **User Service** (port 8081) — registers with Eureka as `user-service`
3. Start **Order Service** (port 8082) — registers with Eureka as `order-service`
4. Open browser or curl:

```
curl http://localhost:8082/orders/101
```

Expected output:

```
Order #101 is placed by John Doe (john.doe@example.com)
```

---

# Summary

* **Eureka Server** acts as the service registry.
* **User Service** registers as `user-service` and provides `/users/{id}` API.
* **Order Service** registers as `order-service`, uses Feign to call `user-service` dynamically.
* Feign and Eureka handle discovery and routing automatically — no hardcoded URLs.

---

If you want, I can help you with:

* Full GitHub repo for all 3 projects
* Docker compose file to start all together
* API Gateway example to route externally

Just say the word!

* sample repo
* https://vscode.dev/github/subhashlamba/spring-boot-microservice-example/tree/main/spring-boot-cloud-api-gateway-routing