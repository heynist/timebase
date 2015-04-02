package be.heynist.gjallar.connectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class TimebaseConnector {

	@Value("${timebase.queue_name}")
	private String timebaseQueueName;

	@Bean
	Queue timebaseQueue() {
		return new Queue(timebaseQueueName, false);
	}
	
	@Value("${timebase.exchange_name}")
	private String timebaseExchangeName;

	@Bean
	FanoutExchange timebaseExchange() {
		FanoutExchange exchange = new FanoutExchange(timebaseExchangeName);
		return exchange;
	}

	@Bean
	Binding marketDataBinding(Queue timebaseQueue, FanoutExchange timebaseExchange) {
		return BindingBuilder.bind(timebaseQueue).to(timebaseExchange);
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(CommandListener commandListener) {
		return new MessageListenerAdapter(commandListener, "receiveMessage");
	}
	
	@Bean
	public SimpleMessageListenerContainer serviceListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(timebaseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}
	
}
