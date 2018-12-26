package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class StopPluginCommand extends Command {

	public StopPluginCommand() {
		super("stopplugin", 1, 1, "cancelplugin");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		String pluginName = args[0];
		boolean stopped = connection.getPluginManager().stopPlugin(pluginName);
		if (stopped) {
			connection.getLogger().info("Plugin " + pluginName + " was stopped");
		}
		else {
			connection.getLogger().info("No plugin with the given name was found");
		}
	}

	@Override
	public String getUsage() {
		return "stopplugin <pluginName>";
	}

}
