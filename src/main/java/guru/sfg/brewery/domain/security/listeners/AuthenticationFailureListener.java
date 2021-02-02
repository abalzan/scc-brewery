package guru.sfg.brewery.domain.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login failure!");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();

            if (token.getPrincipal() instanceof String) {
                log.debug("Attempted User " + token.getPrincipal());
                loginFailureBuilder.username((String) token.getPrincipal());
                userRepository.findByUsername((String) token.getPrincipal())
                        .ifPresent(loginFailureBuilder::user);
            }
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                log.debug("Source IP: " + details.getRemoteAddress());
                loginFailureBuilder.sourceIp(details.getRemoteAddress());
            }

            LoginFailure failure = loginFailureRepository.save(loginFailureBuilder.build());
            log.debug("Failure event : " + failure.getId());
        }
    }
}
