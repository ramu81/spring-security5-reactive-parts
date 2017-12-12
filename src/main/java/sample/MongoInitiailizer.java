package sample;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sample.message.Message;
import sample.message.MessageRepository;
import sample.security.RepositoryReactiveUserDetailsService;
import sample.sequence.NextSequenceService;
import sample.user.User;
import sample.user.UserRepository;

/**
 * @author Rob Winch
 */
@Component
class MongoInitiailizer implements SmartInitializingSingleton {
	private final MessageRepository messages;
	private final UserRepository users;
	@Autowired
	private NextSequenceService service;
	private static final Logger logger = LoggerFactory.getLogger(MongoInitiailizer.class);

	MongoInitiailizer(MessageRepository messages, UserRepository users) {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("MongoInitiailizer() ==> "+i.toString());
		});
		this.messages = messages;
		this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		Stream.of(Thread.currentThread().getStackTrace()).forEach(i -> {
			logger.info("afterSingletonsInstantiated  "+i.toString());
		});
		// sha256 w/ salt encoded "password"
		String passsword = "73ac8218b92f7494366bf3a03c0c2ee2095d0c03a29cb34c95da327c7aa17173248af74d46ba2d4c";
		User rob = new User(service.getNextSequence("userSequence"), "rob@example.com", passsword, "Rob", "Winch");
		User joe = new User(service.getNextSequence("userSequence"), "joe@example.com", passsword, "Joe", "Grandja");
		User vedran = new User(service.getNextSequence("userSequence"), "vedran@example.com", passsword, "Vedran",
				"Pavić");

		this.users.save(rob).block();
		this.users.save(joe).block();
		this.users.save(vedran).block();

		this.messages.save(new Message(service.getNextSequence("messageSequence"), rob, joe, "Hello World")).block();
		this.messages.save(new Message(service.getNextSequence("messageSequence"), rob, joe, "Greetings SpringOne"))
				.block();
		this.messages.save(new Message(service.getNextSequence("messageSequence"), joe, vedran, "Hi San Francisco"))
				.block();

		// @formatter:off
		this.users.findAll().doOnNext(user -> user.setPassword("{sha256}" + user.getPassword()))
				.flatMap(this.users::save).collectList().block();
		// @formatter:on

	}
}
