package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

public class ExecutePluginCommand extends Command {

	public ExecutePluginCommand() {
		super("runplugin", 1, Integer.MAX_VALUE, "executeplugin", "startplugin");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		String pluginName = args[0];
		String reAssembled = "";
		for (int i = 1; i < args.length; i++) {
			reAssembled += args [i];
			reAssembled += " ";
		}
		reAssembled = reAssembled.trim();
		Map<String, String> parameter = parseParameter(connection.getLogger(), reAssembled);
		connection.getPluginManager().executePlugin(pluginName, parameter);
	}

	@Override
	public String getUsage() {
		return "runplugin <pluginName> [-argsName1] [argsValue1] [...]";
	}

	private Map<String, String> parseParameter(Logger logger, String input) {
		Map<String, String> result = new HashMap<String, String>();
		int index = 0;
		String key = "";
		String value = "";
		boolean escaping = false;
		boolean atValue = false;
		boolean quoting = false;
		while (true) {
			char current = input.charAt(index);
			switch (current) {
			case '\\':
				if (escaping) {
					if (atValue) {
						value = value + current;
					} else {
						key = key + current;
					}
					escaping = false;
					break;
				}
				escaping = true;
				break;
			case '\"':
				if (escaping) {
					if (atValue) {
						value = value + current;
					} else {
						key = key + current;
					}
					escaping = false;
					break;
				}
				quoting = !quoting;
				break;
			case ' ':
				if (escaping || quoting) {
					if (atValue) {
						value = value + current;
					} else {
						key = key + current;
					}
					escaping = false;
					break;
				}
				if (!atValue) {
					logger.warn("Parameter " + key + " was not formatted properly, it was ignored");
					key = "";
					break;
				}
				// finish key-value pair
				result.put(key, value);
				key = "";
				value = "";
				atValue = false;
				break;
			case '=':
				if (escaping || quoting) {
					if (atValue) {
						value = value + current;
					} else {
						key = key + current;
					}
					escaping = false;
					break;
				}
				if (atValue) {
					logger.warn("Parameter " + key + " was not formatted properly, it was ignored");
					key = "";
					value = "";
					atValue = false;
					break;
				}
				atValue = true;
				break;
			default:
				if (atValue) {
					value = value + current;
				} else {
					key = key + current;
				}
				escaping = false;
				break;
			}
			return result;

		}
	}
}
