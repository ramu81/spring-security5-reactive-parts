package sample;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
public class MessageApplication {

	private static final Logger logger = LoggerFactory.getLogger(MessageApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}

	// https://github.com/spring-projects/spring-boot/issues/9167
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("hiddenHttpMethodFilter  "+i.toString());
		});
		return new HiddenHttpMethodFilter();
	}
}
