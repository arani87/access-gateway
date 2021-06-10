package you.shall.not.pass.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.HttpCookie;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GateControllerIT {

  @LocalServerPort
  private int port;
  TestRestTemplate restTemplate;
  HttpHeaders headers;

  @BeforeEach
  public void setup() {
    restTemplate = new TestRestTemplate();
    headers = new HttpHeaders();
  }

  @Test
  @DisplayName("Simple Integration test of resources endpoint to check if all resources are listed or not")
  void testGetResources() {

    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/resources"), HttpMethod.GET, entity, String.class);

    String expected = "{\"resources\":[\"/Level1/low/access.html\",\"/Level1/low_access.html\",\"/Level2/high_access.html\",\"/Level2/what/am/I/access.html\",\"/css/app.css\",\"/css/bootstrap.css\",\"/css/main.css\",\"/js/app/script.js\",\"/js/main.js\"]}";

    assertEquals(expected, response.getBody());
  }

  @Test
  @DisplayName("Simple Integration test to check anonymous login : without Auth Header")
  void testAccess() {
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);

    String expected = "{\"authenticated\":true}";

    assertEquals(expected, response.getBody());
  }


  @Test
  @DisplayName("Simple Integration test to check Level1 login : with Auth Header")
  void testAccessWithAuthHeader() {
    headers.add("Authorization", "Basic MSNib2I6MTIzNDE=");
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);

    String expected = "{\"authenticated\":true}";

    assertEquals(expected, response.getBody());
  }

  @Test
  @DisplayName("Integration test to check incorrect login : with Auth Header and incorrect credentials")
  void testAccessWithAuthHeaderIncorrect() {
    headers.add("Authorization", "Basic MSNib2I6MTDFIzNDE=");
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Integration test to check Level0 resource access : Anonymous mode")
  void testAccessOfResourceInAnonymousMode() {
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);
    String set_cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assert set_cookie != null;
    Optional<String> csrfCookie = HttpCookie.parse(set_cookie).stream()
        .filter(cookie -> cookie.getName().equalsIgnoreCase("CSRF")).findFirst()
        .map(HttpCookie::getValue);

    headers.add("XSRF", csrfCookie.orElse(""));
    headers.add(HttpHeaders.SET_COOKIE, set_cookie);

    ResponseEntity<String> resourceResponse = restTemplate.exchange(
        createURLWithPort("/js/main.js"), HttpMethod.GET, entity, String.class);
    assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
  }

  @Test
  @DisplayName("Integration test to check Level1 resource access : Anonymous mode : Negative scenario")
  void testAccessOfL1ResourceInAnonymousMode() {
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);
    String set_cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assert set_cookie != null;
    Optional<String> csrfCookie = HttpCookie.parse(set_cookie).stream()
        .filter(cookie -> cookie.getName().equalsIgnoreCase("CSRF")).findFirst()
        .map(HttpCookie::getValue);

    headers.add("XSRF", csrfCookie.orElse(""));
    headers.add("Cookie", set_cookie);

    HttpEntity<String> newEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> resourceResponse = restTemplate.exchange(
        createURLWithPort("/Level1/low/access.html"), HttpMethod.GET, newEntity, String.class);
    assertEquals(HttpStatus.FORBIDDEN, resourceResponse.getStatusCode());
  }


  @Test
  @DisplayName("Integration test to check Level1 resource access :L1 Authenticated mode : Positive scenario")
  void testAccessOfL1ResourceInL1AuthMode() {
    headers.add("Authorization", "Basic MSNib2I6MTIzNDE=");
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);
    String set_cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assert set_cookie != null;
    Optional<String> csrfCookie = HttpCookie.parse(set_cookie).stream()
        .filter(cookie -> cookie.getName().equalsIgnoreCase("CSRF")).findFirst()
        .map(HttpCookie::getValue);

    headers.add("XSRF", csrfCookie.orElseThrow());
    headers.add("Cookie", set_cookie);

    HttpEntity<String> newEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> resourceResponse = restTemplate.exchange(
        createURLWithPort("/Level1/low/access.html"), HttpMethod.GET, newEntity, String.class);
    assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
  }

  @Test
  @DisplayName("Integration test to check Level2 resource access :L1 Authenticated mode : Negative scenario")
  void testAccessOfL2ResourceInL1AuthMode() {
    headers.add("Authorization", "Basic MSNib2I6MTIzNDE=");
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);
    String set_cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assert set_cookie != null;
    Optional<String> csrfCookie = HttpCookie.parse(set_cookie).stream()
        .filter(cookie -> cookie.getName().equalsIgnoreCase("CSRF")).findFirst()
        .map(HttpCookie::getValue);

    headers.add("XSRF", csrfCookie.orElseThrow());
    headers.add("Cookie", set_cookie);

    HttpEntity<String> newEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> resourceResponse = restTemplate.exchange(
        createURLWithPort("/Level2/high_access.html"), HttpMethod.GET, newEntity, String.class);
    assertEquals(HttpStatus.FORBIDDEN, resourceResponse.getStatusCode());
  }


  @Test
  @DisplayName("Integration test to check Level1 resource access :L2 Authenticated mode : Positive scenario")
  void testAccessOfL1ResourceInL2AuthMode() {
    headers.add("Authorization", "Basic MiNib2I6dGVzdDE=");
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/access"), HttpMethod.GET, entity, String.class);
    String set_cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assert set_cookie != null;
    Optional<String> csrfCookie = HttpCookie.parse(set_cookie).stream()
        .filter(cookie -> cookie.getName().equalsIgnoreCase("CSRF")).findFirst()
        .map(HttpCookie::getValue);

    headers.add("XSRF", csrfCookie.orElseThrow());
    headers.add("Cookie", set_cookie);

    HttpEntity<String> newEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> resourceResponse = restTemplate.exchange(
        createURLWithPort("/Level1/low/access.html"), HttpMethod.GET, newEntity, String.class);
    assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
