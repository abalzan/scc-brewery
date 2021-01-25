package guru.sfg.brewery.config;

import guru.sfg.brewery.security.CustomPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //needed for use with Spring Data JPA SPel
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin(loginConfigurer ->
                        loginConfigurer
                                .loginProcessingUrl("/login")
                                .loginPage("/")
                                .successForwardUrl("/")
                                .defaultSuccessUrl("/")
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .httpBasic().and()
                .csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        //h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring").password("blahh").roles("ADMIN").build();
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user").password("passwd").roles("USER").build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //In memory authentication sample
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("spring")
//                .password("{bcrypt}$2a$10$Tvr/GxBIPVcocA6zPndfO.kTEmvHwa5UsqsZ6/KRth7ij6VmhyhQ6")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}d8ba028a9c6f8c349fa833a39b96421b178e7cad4f131e02cff51b0cae38d4cee0a8191524261486")
//                .roles("USER")
//                .and()
//                .withUser("scott")
//                .password("{bcrypt15}$2a$15$xJIkEiXw/GBsXogTTUNimesiajtiVGXWFyaiHMr2x3elA2gF88Cj.")
//                .roles("CUSTOMER");
//
//    }
}
