package sample;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sample.message.Message;
import sample.message.MessageRepository;
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

	MongoInitiailizer(MessageRepository messages, UserRepository users) {
		this.messages = messages;
		this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		// sha256 w/ salt encoded "password"
		String passsword = "73ac8218b92f7494366bf3a03c0c2ee2095d0c03a29cb34c95da327c7aa17173248af74d46ba2d4c";
		User rob = new User(service.getNextSequence("customSequences"), "rob@example.com", passsword, "Rob", "Winch");
		User joe = new User(service.getNextSequence("customSequences"), "joe@example.com",
				passsword, "Joe", "Grandja");
		User vedran = new User(service.getNextSequence("customSequences"), "vedran@example.com", passsword, "Vedran", "Pavić");

		this.users.save(rob).block();
		this.users.save(joe).block();
		this.users.save(vedran).block();

		this.messages.save(new Message(1L, rob, joe, "Hello World")).block();
		this.messages.save(new Message(2L, rob, joe, "Greetings SpringOne")).block();
		this.messages.save(new Message(3L, joe, vedran, "Hi San Francisco")).block();

		// @formatter:off
		this.users.findAll().doOnNext(user -> user.setPassword("{sha256}" + user.getPassword()))
				.flatMap(this.users::save).collectList().block();
		// @formatter:on

	}
}
