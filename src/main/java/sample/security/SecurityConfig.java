package sample.security;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(SecurityControllerAdvice.class);
	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		logger.info("springSecurityFilterChain " );
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		http
			.authorizeExchange()
				.pathMatchers("/users").access(this::hasAdminRole)
				.pathMatchers("/login", "/signup", "/webjars/**").permitAll()
				.anyExchange().authenticated()
				.and()
			.httpBasic().and()
			.formLogin()
				.loginPage("/login");
		return http.build();
	}

	private Mono<AuthorizationDecision> hasAdminRole(Mono<Authentication> authentication,
			AuthorizationContext authorizationContext) {
		logger.info("hasAdminRole " );
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		return authentication
				.map(Authentication::getName)
				.map(username -> username.startsWith("rob@"))
				.map(AuthorizationDecision::new);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
