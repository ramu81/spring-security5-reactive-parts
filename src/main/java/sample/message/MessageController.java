package sample.message;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.result.view.Rendering;

import reactor.core.publisher.Mono;
import sample.security.CurrentUser;
import sample.user.User;

/**
 * @author Rob Winch
 */
@Controller
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
	private final MessageRepository messages;

	public MessageController(MessageRepository messages) {
		this.messages = messages;
	}

	@GetMapping("/inbox")
	Rendering inbox(@CurrentUser User user) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		Stream.of(st).forEach(i -> {
			logger.info("inbox " + i.toString());
		});
		return Rendering.view("messages/inbox").modelAttribute("messages", this.messages.findByTo(user.getId()))
				.build();
	}

	@GetMapping("/messages/{id}")
	Rendering message(@PathVariable Long id) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		Stream.of(st).forEach(i -> {
			logger.info("message " + i.toString());
		});
		return Rendering.view("messages/view").modelAttribute("message", this.messages.findById(id)).build();
	}

	@DeleteMapping("/messages/{id}")
	Mono<String> delete(@PathVariable Long id) {
		return this.messages.deleteById(id).then(Mono.just("redirect:/inbox"));
	}
}
