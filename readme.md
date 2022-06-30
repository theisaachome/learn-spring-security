# Learn spring security.

## Basic Authentication Setup

Write a basic authentication configuration file.

```java
@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
}
```

## Setup for White List for resources

Setup white list using antMatchers()

```java

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
        		.antMatchers("/","index","/css/*","/js/*")
        		.permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
```

---

## In Memory Application User

A User must have the following :

- username
- password (must be encoded)
- ROLE / ROLES (ROLE_NAME)
- Authorities
- more...

#### setup userDetailsService in ApplicationSecurityConfig file.

```java
@Bean
    UserDetailsService userDetailsService() {
    	UserDetails studentUser =
    			User.builder()
    			.username("annasmith")
    			.password("")
    			.roles("STUDENT")// ROLE_STUDENT spring security will generate.
    			.build();
    		return new InMemoryUserDetailsManager(studentUser);
    }
```

### Roles with Users

```java
@Bean
    UserDetailsService userDetailsService() {
    	UserDetails studentUser =
    			User.builder()
    			.username("annasmith")
    			.password("")
    			.roles("STUDENT")// ROLE_STUDENT spring security will generate.
    			.build();
        var adminUser = User.builder()
    			.username("linda")
    			.password(passwordEncoder.encode("password"))
    			.roles("ADMIN")
    			.build();
    	return new InMemoryUserDetailsManager(studentUser,adminUser);
    }
```

## Roles and Permissions using ENUM

Define ENUM to represent the roles and permissions for the given user.

### Roles

```java
public enum AppUserRoles {
	STUDENT,
	ADMIN
}
```

### Permission

```java
public enum AppUserPermission {
	STUDENT_READ("student:read"),
	STUDENT_WRITE("student:write"),
	COURSE_READ("course:read"),
	COURSE_WRITE("course:write");

	private final String permission;

	private AppUserPermission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
```

### add maven

```xml
<!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>30.1-jre</version>
</dependency>

```

### update AppUserRoles

```java
public enum AppUserRoles {
	STUDENT(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(AppUserPermission.COURSE_READ,
			AppUserPermission.COURSE_WRITE,
			AppUserPermission.STUDENT_READ,
			AppUserPermission.STUDENT_WRITE));
	private final Set<AppUserPermission> permissions;
	private AppUserRoles(Set<AppUserPermission> permissions) {
		this.permissions = permissions;
	}
	public Set<AppUserPermission> getPermissions() {
		return permissions;
	}
}
```

### update User with New Permissions.

```java
 @Bean
    UserDetailsService userDetailsService() {
    	UserDetails studentUser =
    			User.builder()
    			.username("annasmith")
    			.password(passwordEncoder.encode("password"))
    			.roles(AppUserRoles.STUDENT.name())// ROLE_STUDENT spring security will generate.
    			.build();
    	var adminUser = User.builder()
    			.username("linda")
    			.password(passwordEncoder.encode("password"))
    			.roles(AppUserRoles.ADMIN.name())
    			.build();
    		return new InMemoryUserDetailsManager(studentUser,adminUser);
    }

```

---

## ROLES Based Authentication

- Specify the resource for a Specific user roles

- Access only the resource with student roles.

```java
	.antMatchers("/api/**/").hasRole(AppUserRoles.STUDENT.name())
```

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
    		.antMatchers("/","index","/css/*","/js/*").permitAll()
    		.antMatchers("/api/**/").hasRole(AppUserRoles.STUDENT.name())
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    return http.build();
  }
```

---

## Permission Based on Authentication

Add one more roles

```java
ADMINTRAINEE(
			Sets.newHashSet(
			AppUserPermission.COURSE_READ,
			AppUserPermission.STUDENT_READ));
```

Add User with new roles

```java
var tomUser = User.builder()
    	.username("tom")
    	.password(passwordEncoder.encode("password"))
    	.roles(AppUserRoles.ADMINTRAINEE.name())
    	.build();
```

Add resources

```java
@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagmentController {}
```

---

## Disabling CSRF

```
		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**/").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();

		return http.build();
```

## hasAuthority()

```java

```
