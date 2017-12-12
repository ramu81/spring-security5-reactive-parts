package sample.user;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

/**
 * @author Rob Winch
 */
@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {
	private final UserRepository users;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(UserRepository users) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("UserController  "+i.toString());
		});
		this.users = users;
	}

	@GetMapping
	public Flux<User> users() {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("users  "+i.toString());
		});
		return this.users.findAll();
	}
}
