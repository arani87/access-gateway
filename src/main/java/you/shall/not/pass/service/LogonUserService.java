package you.shall.not.pass.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import you.shall.not.pass.domain.Access;

@Service
public class LogonUserService {

  public Optional<Access> getCurrentAccessLevel() {
    return getGateKeeperGrant();
  }

  private Optional<Access> getGateKeeperGrant() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetails) {
      UserDetails userDetails = ((UserDetails) principal);
      List<GrantedAuthority> targetList = new ArrayList<>(userDetails.getAuthorities());
      return targetList.stream().map(grantedAuthority ->
          Access.valueOf(grantedAuthority.getAuthority())).findAny();
    }
    if (SecurityContextHolder.getContext()
        .getAuthentication() instanceof AnonymousAuthenticationToken) {
      return Optional.of(Access.Level0);
    }

    return Optional.empty();
  }

  public Optional<String> getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetails) {
      UserDetails userDetails = ((UserDetails) principal);
      return Optional.of(userDetails.getUsername());
    }
    if (SecurityContextHolder.getContext()
        .getAuthentication() instanceof AnonymousAuthenticationToken) {
      return Optional.of("anonymous");
    }
    return Optional.empty();
  }

}
