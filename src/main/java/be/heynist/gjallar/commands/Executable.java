package be.heynist.gjallar.commands;

import org.json.JSONObject;

public interface Executable {
	public void execute(JSONObject payload);
}
