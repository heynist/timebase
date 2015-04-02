package be.heynist.gjallar.commands;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.heynist.gjallar.connectors.HistoricalDataConnector;

import com.rabbitmq.client.Channel;

@Component
public class HistoricalDataCommand implements Executable {
	private final ThreadLocal<Channel> channels = new ThreadLocal<Channel>(); 
	
	@Autowired
	private HistoricalDataConnector historicalDataConnector;
	
	@Override
	public void execute(JSONObject payload) {
		String routingKey = payload.getString("routing_key");
		String security = payload.getString("security");
		String timeframe = payload.getString("timeframe");
		Long from = payload.getLong("from");
		Long to = payload.getLong("to");
		historicalDataConnector.sendHistoricalData(routingKey, security, timeframe, from, to);
	}
}
