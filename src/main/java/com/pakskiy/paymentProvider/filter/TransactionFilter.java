package com.pakskiy.paymentProvider.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
@Component
@Order(1)
public class TransactionFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger(TransactionFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOG.info("Initializing filter :{}", this);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        LOG.info("Starting Transaction for req :{}", req.getRequestURI());
        chain.doFilter(request, response);
        LOG.info("Committing Transaction for req :{}", req.getRequestURI());
    }

    @Override
    public void destroy() {
        LOG.warn("Destructing filter :{}", this);
    }
}



























To implement a basic authentication filter in a Spring Boot application using user credentials stored in a database, you can follow these steps:

1. **Define a User Entity**: Create an entity class to represent user credentials. This entity will map to your database table.

```java
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private Long id;
    private String username;
    private String password;

    // Getters and setters
}
```

2. **Create a User Repository**: Define a repository interface to interact with your User entity.

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

3. **Implement the Authentication Filter**: Create a filter that intercepts requests and performs basic authentication using the provided credentials.

```java
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BasicAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserRepository userRepository;

    public BasicAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return super.attemptAuthentication(request, response);
    }
}
```

4. **Configure Security**: Configure Spring Security to use the custom authentication filter.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .addFilter(new BasicAuthenticationFilter(authenticationManagerBean()));
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
```

5. **Define Controller Endpoints**: Create controller endpoints with appropriate security configurations.

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/public/hello")
    public String helloPublic() {
        return "Hello, Public!";
    }

    @GetMapping("/api/private/hello")
    public String helloPrivate() {
        return "Hello, Private!";
    }
}
```

This example demonstrates a basic authentication filter that authenticates users using credentials stored in a database. Adjust the implementation as per your application's requirements and security policies.