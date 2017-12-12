package sample.security;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sample.user.User;
import sample.user.UserRepository;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Component
public class PasswordUpgradeReactiveAuthenticationManager implements ReactiveAuthenticationManager {
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordUpgradeReactiveAuthenticationManager.class);
	private final UserRepository users;
	private final ReactiveAuthenticationManager delegate;
	private final PasswordEncoder encoder;

	PasswordUpgradeReactiveAuthenticationManager(UserRepository users, ReactiveUserDetailsService userDetailsService,
			PasswordEncoder encoder) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		this.users = users;
		this.delegate = createDelegate(userDetailsService, encoder);
		this.encoder = encoder;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return this.delegate.authenticate(authentication).delayUntil(a -> updatePassword(authentication));
	}

	private Mono<User> updatePassword(Authentication authentication) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		return this.users.findByEmail(authentication.getName()).publishOn(Schedulers.parallel())
				.doOnSuccess(u -> u.setPassword(this.encoder.encode(authentication.getCredentials().toString())))
				.flatMap(this.users::save);
	}

	private static ReactiveAuthenticationManager createDelegate(ReactiveUserDetailsService userDetailsService,
			PasswordEncoder encoder) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info(i.toString());
		});
		UserDetailsRepositoryReactiveAuthenticationManager result = new UserDetailsRepositoryReactiveAuthenticationManager(
				userDetailsService);
		result.setPasswordEncoder(encoder);
		return result;
	}
}
