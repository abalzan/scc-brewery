package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }
    }

    private void loadSecurityData() {
        Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
        Authority userRole = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
        Authority customerRole = authorityRepository.save(Authority.builder().role("ROLE_CUSTOMER").build());

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("test"))
                .authority(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .authority(customerRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authority(userRole)
                .build());

        log.debug("users loaded: " + userRepository.count());
    }
}
