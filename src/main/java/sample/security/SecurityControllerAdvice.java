package sample.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sample.user.User;

/**
 * @author Rob Winch
 * @since 5.0
 */
@ControllerAdvice
public class SecurityControllerAdvice {
	@ModelAttribute
	Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
		Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
		return csrfToken.doOnSuccess(token -> exchange.getAttributes()
				.put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
	}

	@ModelAttribute("currentUser")
	User currentUser(@AuthenticationPrincipal User currentUser) {
		return currentUser;
	}
}
