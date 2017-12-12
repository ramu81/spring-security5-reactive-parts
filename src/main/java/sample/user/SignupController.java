package sample.user;

import java.util.stream.Stream;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sample.sequence.NextSequenceService;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Controller
@RequestMapping(path = "/signup")
public class SignupController {
	@Autowired
	private NextSequenceService service;

	private final UserRepository users;

	private final PasswordEncoder encoder;
	private static final Logger logger = LoggerFactory.getLogger(SignupController.class);
	public SignupController(UserRepository users, PasswordEncoder encoder) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("SignupController  "+i.toString());
		});
		this.users = users;
		this.encoder = encoder;
	}

	@GetMapping
	public Mono<String> signupForm(@ModelAttribute User user) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("signupForm  "+i.toString());
		});
		return Mono.just("signup/form");
	}

	@PostMapping
	public Mono<String> signup(@Valid User user, BindingResult result) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("signup  "+i.toString());
		});
		if(result.hasErrors()) {
			return signupForm(user);
		}
		return Mono.just(user)
				.doOnNext(u -> u.setId(service.getNextSequence("customSequences")))
				.subscribeOn(Schedulers.parallel())
				.doOnNext(u -> u.setPassword(this.encoder.encode(u.getPassword())))
				.flatMap(this.users::save)
				.then(Mono.just("redirect:/"));
	}
}
