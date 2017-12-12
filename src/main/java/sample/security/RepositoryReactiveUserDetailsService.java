package sample.security;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import sample.user.User;
import sample.user.UserRepository;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Component
public class RepositoryReactiveUserDetailsService implements ReactiveUserDetailsService {
	private final UserRepository users;
	private static final Logger logger = LoggerFactory.getLogger(RepositoryReactiveUserDetailsService.class);

	public RepositoryReactiveUserDetailsService(UserRepository users) {
		this.users = users;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return this.users.findByEmail(username).map(CustomUserDetails::new);
	}

	static class CustomUserDetails extends User implements UserDetails {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4659808990518701354L;

		public CustomUserDetails(User user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
				logger.info(i.toString());
			});
			if ("rob@example.com".equals(getEmail())) {
				return AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
			} else {
				return AuthorityUtils.createAuthorityList("ROLE_USER");
			}
		}

		@Override
		public String getUsername() {
			return getEmail();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			logger.info("isAccountNonLocked  ==> "+getEmail());
			if ("rob@example.com".equals(getEmail())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
