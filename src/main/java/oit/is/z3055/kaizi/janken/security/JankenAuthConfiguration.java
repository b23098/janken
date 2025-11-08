package oit.is.z3055.kaizi.janken.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import oit.is.z3055.kaizi.janken.model.Entry;

@Configuration
@EnableWebSecurity
public class JankenAuthConfiguration {

  private final Entry entry;

  public JankenAuthConfiguration(Entry entry) {
    this.entry = entry;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService users(PasswordEncoder encoder) {
    UserDetails honda = User.withUsername("ほんだ")
        .password(encoder.encode("isdev"))
        .roles("USER")
        .build();

    UserDetails igaki = User.withUsername("いがき")
        .password(encoder.encode("isdev"))
        .roles("USER")
        .build();

    UserDetails cpu = User.withUsername("CPU")
        .password(encoder.encode("isdev"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(honda, igaki, cpu);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandler logoutSuccessHandler)
      throws Exception {

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/janken/**").authenticated()
            .anyRequest().permitAll())
        .formLogin(Customizer.withDefaults())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler))
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return (request, response, authentication) -> {
      if (authentication != null) {
        entry.remove(authentication.getName());
      }
      response.sendRedirect("/");
    };
  }

  @EventListener
  public void onAuthSuccess(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();
    if (auth != null) {
      entry.add(auth.getName());
    }
  }

  @EventListener
  public void onSessionDestroyed(SessionDestroyedEvent event) {
    List<org.springframework.security.core.context.SecurityContext> contexts = event.getSecurityContexts();
    for (var ctx : contexts) {
      var auth = ctx.getAuthentication();
      if (auth != null) {
        entry.remove(auth.getName());
      }
    }
  }
}
