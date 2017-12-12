package sample;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Controller
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	@GetMapping("/")
	String index() {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("index() ==> " + i.toString());
		});
		return "redirect:/inbox";
	}
}
