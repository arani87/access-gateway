package you.shall.not.pass.service;

import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import you.shall.not.pass.domain.User;
import you.shall.not.pass.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUserByName(String name) {
        Example<User> example = Example.of(User.builder().userName(name).build());
        Optional<User> optionalUser = repository.findOne(example);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
