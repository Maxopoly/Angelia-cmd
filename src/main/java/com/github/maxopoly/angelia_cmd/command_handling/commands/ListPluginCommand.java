package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import java.util.List;

public class ListPluginCommand extends Command {

	public ListPluginCommand() {
		super("listplugins", 0, 0, "listplugin", "pluginlist");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		List<String> pluginNames = connection.getPluginManager().getAvailablePlugins();
		if (pluginNames.size() == 0) {
			connection.getLogger().info("No plugins available :(");
			return;
		}
		connection.getLogger().info(pluginNames.size() + " plugins found: ");
		for (String pluginName : pluginNames) {
			connection.getLogger().info(" - " + pluginName);
		}
	}

	@Override
	public String getUsage() {
		return "listplugins";
	}

}
