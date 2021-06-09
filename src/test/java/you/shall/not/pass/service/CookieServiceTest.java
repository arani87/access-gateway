package you.shall.not.pass.service;

import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CookieServiceTest {

  CookieService cookieService = new CookieService();


  HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

  @Test
  @DisplayName("Test if a cookie is read correctly from HttpServletRequest")
  void testGetCookieValue() {

    Cookie cookie1 = new Cookie("GRANT", "GRANTSESSION");
    Cookie cookie2 = new Cookie("CSRF", "CSRFVALUE");
    Cookie[] cookies = new Cookie[]{cookie1, cookie2};
    when(req.getCookies()).thenReturn(cookies);
    String result = cookieService.getCookieValue(req, "GRANT");
    Assertions.assertEquals("GRANTSESSION", result);
  }

  @Test
  @DisplayName("Test creation of a cookie")
  void testCreateCookie() {
    String result = cookieService.createCookie("name", "token", 0);
    Assertions.assertEquals("name=token; SameSite=Strict; Path=/; HttpOnly; Max-Age=0", result);
  }

}


