package be.heynist.gjallar.connectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.heynist.gjallar.commands.CommandMap;
import be.heynist.gjallar.commands.Executable;

@Component
public class CommandListener {
	
	@Autowired
	private CommandMap commandMap;
	
	public void receiveMessage(String message) {
		System.out.println("RECEIVED => " + message);
		JSONObject jsonObj = new JSONObject(message);
		String commandString = jsonObj.getString("command");
		System.out.println("Timebase received command: " + commandString);
		Executable command = commandMap.get(commandString);
		if(command != null)
			command.execute(jsonObj);				
	}

}
