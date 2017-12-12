package sample.security;

import reactor.core.publisher.Mono;
import sample.user.User;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Rob Winch
 * @since 5.0
 */
@ControllerAdvice
public class SecurityControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityControllerAdvice.class);

	@ModelAttribute
	Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
		logger.info("csrfToken " );
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
		return csrfToken.doOnSuccess(
				token -> exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
	}

	@ModelAttribute("currentUser")
	User currentUser(@CurrentUser User currentUser) {
		logger.info("currentUser " );
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		return currentUser;
	}
}
