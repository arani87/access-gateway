package you.shall.not.pass.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import you.shall.not.pass.filter.staticresource.StaticResourceService;
import you.shall.not.pass.repositories.UserRepository;
import you.shall.not.pass.service.CookieService;
import you.shall.not.pass.service.CsrfProtectionService;
import you.shall.not.pass.service.SessionService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GateController.class)
@AutoConfigureMockMvc
class GateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SessionService sessionService;
  @MockBean
  private CsrfProtectionService csrfProtectionService;
  @MockBean
  private StaticResourceService resourceService;
  @MockBean
  private CookieService cookieService;
  @MockBean
  private UserRepository userRepository;
  @Autowired
  private Gson gson;



  @Test
  @DisplayName("Test if resources endpoint responds with HTTP Status 200")
  void testResources() throws Exception {
    when(resourceService.getAllStaticResources()).thenReturn(List.of("resource1", "resource2"));
    mockMvc.perform(MockMvcRequestBuilders.get("/resources"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Test if resources endpoint responds with expected resources list")
  void testResourcesList() throws Exception {
    when(resourceService.getAllStaticResources()).thenReturn(List.of("resource1", "resource2"));
    mockMvc.perform(MockMvcRequestBuilders.get("/resources"))
        .andExpect(status().isOk()).andExpect(content().string("{\"resources\":[\"resource1\",\"resource2\"]}"));
  }

  @Test
  @DisplayName("Test Access Endpoint")
  void testAccessAnonymous() throws Exception {
    when(sessionService.getSession()).thenReturn(Optional.of("DUMMY_SESSION"));
    when(csrfProtectionService.getCsrfCookie()).thenReturn("COOKIE");
    mockMvc.perform(MockMvcRequestBuilders.get("/access"))
        .andExpect(status().isOk()).andExpect(content().string("{\"authenticated\":true}"));
  }

}
