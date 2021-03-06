# Learn spring security.

## Talbe of Content

- [Basic Authentication](#basic-authentication-setup)
- [Form Based Authentication](#form-based-authentication)
- [Database Authentication](#database-authentication)

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

---

## hasAuthority()

update securityFilterChain method with antMatchers()

```java
@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**/").hasRole(AppUserRoles.STUDENT.name())
		.antMatchers(HttpMethod.GET,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.name())
		.antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.name())
		.antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.name())
		.antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(
				AppUserRoles.ADMIN.name(),
				AppUserRoles.ADMINTRAINEE.name())
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();
		return http.build();
	}
```

---

## Adding Authorities to Users

Update AppUserRoles file with following code snippet.

```java
public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
		var permissions= getPermissions().stream()
		.map((permission)-> new SimpleGrantedAuthority(permission.getPermission()))
		.collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

		return permissions;
	}
```

---

## antMatchers Order Does Matter

```java

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**/").hasRole(AppUserRoles.STUDENT.name())
		.antMatchers(HttpMethod.GET,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.getPermission())
		.antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.getPermission())
		.antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(AppUserPermission.COURSE_READ.getPermission())
		.antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(
				AppUserRoles.ADMIN.name(),
				AppUserRoles.ADMINTRAINEE.name())
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();

		return http.build();
	}

```

---

## preAuthorize()

`hasRole("ROLE_")`  
`hasAnyRole("ROLE_")`  
`hasAuthority("permission")`  
`hasAnyAuthority("permission")`

Update at resources or Controller class

```java

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagmentController {

	private static final List<Student> STUDENTS = Arrays.asList(new Student(1, "James Bond"),
			new Student(2, "Maria Jones"), new Student(3, "Anna Smith"));

	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMINTRAINEE')")
	public List<Student> getAllStudents() {
		System.out.println("getAllStudents()");
		return STUDENTS;
	}

	@PostMapping
	@PreAuthorize("hasAuthority('student:write')")
	public void registerNewStudent(@RequestBody Student student) {
		System.out.println("registerNewStudent()");
		System.out.println(student);
	}
	@DeleteMapping(path="{studentId}")
	@PreAuthorize("hasAuthority('student:write')")
	public void deleteStudent(@PathVariable("studentId") Integer studentId) {
		System.out.println("deleteStudent()");
		System.out.println(studentId);
	}

	@PutMapping(path="{studentId}")
	@PreAuthorize("hasAuthority('student:write')")
	public void updateStudent(@PathVariable("studentId") Integer studentId,@RequestBody Student student) {
		System.out.println("updateStudent()");
		System.out.println(String.format("%s %s", studentId,student));
	}
}
```

Update at Security Config class

```java

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {}
```

Remove all antMatcher from securityFilterChain method.

```java
@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.httpBasic();

		return http.build();
	}
```

---

## Form Based Authentication

- Username and password
- Standard in most websites
- Forms (Full Control)
- Can logout
- HTTPS Recommended

## Enabling Form Based Authentication

```java
@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll();

		return http.build();
	}

```

## Setup Controller for Template View

```java
@Controller
@RequestMapping("/")
public class TemplateController {

	@GetMapping("login")
	public String getLoginView() {
		return "login";
	}

	@GetMapping("courses")
	public String getCourses() {
		return "courses";
	}
}

```

## REDIRECT AFTER SUCCESS LOGIN

```java
@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll()
		// Setup default URL after login
		.defaultSuccessUrl("/courses",true);

		return http.build();
	}

```

---

## Remember Me

```java
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll()
		.defaultSuccessUrl("/courses",true)
		.and()
		// remever me checkbox
		.rememberMe(); // default to 2 weeks

		return http.build();
	}
```

## REMEMBER ME COOKIE AND EXTRA OPTIONS

```java
http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll()
		.defaultSuccessUrl("/courses",true)
		.and()
		.rememberMe()
		.tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))
		.key("verysomethingsecured");

		return http.build();
```

---

## LOGOUT

```java
@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll()
		.defaultSuccessUrl("/courses",true)
		.and()
		.rememberMe()
		.tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))
		.key("verysomethingsecured")
		.and()
		// logout setup and its logoutSuccessUrl
		.logout()
		.logoutUrl("/logout")
		.clearAuthentication(true)
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID","remember-me")
		.logoutSuccessUrl("/login");

		return http.build();
	}

```

---

## Database Authentication
