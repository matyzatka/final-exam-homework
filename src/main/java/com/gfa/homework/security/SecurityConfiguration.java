package com.gfa.homework.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.homework.exceptions.AccessDeniedException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableWebSecurity
@Getter
public class SecurityConfiguration {

  public static final int TOKEN_EXPIRATION_TIME = 3600000;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) ->
        web.ignoring().antMatchers("/api/v1/customers/sign-up", "/api/v1/customers/login");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf().disable();
    httpSecurity
        .headers()
        .xssProtection()
        .and()
        .contentSecurityPolicy("script-src 'self'; form-action 'self'")
        .and()
        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
        .and()
        .permissionsPolicy(
            permissionsPolicyConfig -> permissionsPolicyConfig.policy("geolocation=(self)"));
    httpSecurity.authorizeRequests().anyRequest().authenticated();
    httpSecurity.addFilterBefore(
        new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  static final class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain)
        throws IOException {
      String authorizationHeader = request.getHeader(AUTHORIZATION);

      try {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
          String token = authorizationHeader.substring("Bearer ".length());
          Dotenv dotenv = Dotenv.load();

          Algorithm algorithm =
              Algorithm.HMAC512(
                  Objects.requireNonNull(dotenv.get("JWT_SECRET_KEY"))
                      .getBytes(StandardCharsets.UTF_8));

          JWTVerifier verifier = JWT.require(algorithm).build();
          DecodedJWT decodedJWT = verifier.verify(token);

          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request, response);
        } else {
          throw new AccessDeniedException("Access denied.");
        }
      } catch (Exception e) {
        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        HashMap<String, String> error = new HashMap<>();
        error.put("Error", e.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), error);
      }
    }
  }
}
