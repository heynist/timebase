package be.heynist.gjallar.commands;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandMap {
	
	private Map<String, Executable> commandMap;
	
	@Autowired
	private HistoricalDataCommand historicalDataCommand;

	@PostConstruct
	public void initCommandMap() {
		commandMap = new HashMap<String, Executable>();
		commandMap.put("getHistoricalData", historicalDataCommand);
	}
	
	public Executable get(String command) {
		return commandMap.get(command);
	}

}
