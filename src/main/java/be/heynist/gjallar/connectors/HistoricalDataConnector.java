package be.heynist.gjallar.connectors;

import java.util.List;
import java.util.Map;

import org.influxdb.dto.Serie;
import org.json.JSONObject;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.BindingBuilder.DirectExchangeRoutingKeyConfigurer;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HistoricalDataConnector {

	@Value("${historical_data.queue_name}")
	private String historicalDataQueueName;

	@Bean
	Queue historicalDataQueue() {
		return new Queue(historicalDataQueueName, false);
	}
	
	@Value("${historical_data.exchange_name}")
	private String historicalDataExchangeName;

	@Bean
	DirectExchange historicalDataExchange() {
		DirectExchange exchange = new DirectExchange(historicalDataExchangeName);
		return exchange;
	}

	@Bean
	DirectExchangeRoutingKeyConfigurer historicalDataBinding(Queue historicalDataQueue, DirectExchange historicalDataExchange) {
		return BindingBuilder.bind(historicalDataQueue).to(historicalDataExchange);
	}
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private InfluxDBConnector influxDBConnector;
	
	public void sendHistoricalData(String routingKey, String security, String timeframe, Long from, Long to) {
		List<Serie> results = influxDBConnector.getHistoricalDataFor(security, timeframe, from, to);
		for(int i=0; i < results.size(); i++) {
			Serie serie = results.get(i);
			List<Map<String, Object>> rows = serie.getRows();
			for(Map<String, Object> row : rows) {
				rabbitTemplate.convertAndSend(historicalDataExchangeName, routingKey, payloadFromRow(row).toString());
			}
		}
	}

	private JSONObject payloadFromRow(Map<String, Object> row) {
		JSONObject payload = new JSONObject();
		payload.put("command", "marketData");
		payload.put("timestamp", row.get("timestamp"));
		payload.put("open", row.get("open"));
		payload.put("high", row.get("high"));
		payload.put("low", row.get("low"));
		payload.put("close", row.get("close"));
		payload.put("volume", row.get("volume"));
		return payload;
	}
}
