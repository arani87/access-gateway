# Changelog : Gate-Keeper Senior Test : Arani

### Anonymous user access
1. Changes in SecurityConfigAdapter to allow Anonymous user login
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
http.sessionManagement()
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
.and().csrf().disable()
.httpBasic()
.and()
.anonymous().principal("anonymous").authorities("0")
.and()
.authorizeRequests()
.antMatchers("/access")
.permitAll();
}
```
2. Changes in LogonUserService to check if user principal belongs to AnonymousAuthenticationToken
```java
if (SecurityContextHolder.getContext()
    .getAuthentication() instanceof AnonymousAuthenticationToken) {
    return Optional.of("anonymous");
    }
```
3. Changes in SessionService to handle session creation for anonymous user

### Code Improvements
> Life Cycle Management
1. Upgraded codebase to Java11
2. Upgraded SpringBoot version to the latest release of 2.2.x series
3. Upgraded versions of dependencies

> General improvements
1. The classes that I have touched, I have replaced field injection to constructor injection as that is the preferred way of doing DI
2. Small refactorings

> Unit Tests
1. Some unit tests have been added

### Frontend Assignment
Have developed most of the requirement, and the completed functionality are as follows:
1. /home loads the frontend page > DONE
2. On load all the resources are loaded > DONE
3. Anonymous user can view the unprotected resources > DONE
4. If an Anonymous user tries to view a Level1 or Level2 resource, authorization prompt is shown > DONE
5. If level1 user tries to viewe a Level2 resource, auth promt is shown > DONE

however, I have doubts regarding some part of it.
That is regarding CSRF protection and sending it as XSRF header.
> The CSRF cookie is httpOnly, which makes it impossible to read at client side.
> Is it a common practise to set CSRF without httpOnly flag? But doesn't that defeat the whole purpose of XSS?
> I could tweak the code to make the CSRF token as not httpOnly, that way I could complete the FE assignment. 
> But, I think it's really not the point and better to have a discussion on what the established best practices are.
   

