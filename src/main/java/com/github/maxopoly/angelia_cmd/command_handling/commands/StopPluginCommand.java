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
		int stopped = connection.getPluginManager().stopPlugin(pluginName);
		connection.getLogger().info(stopped + " plugin(s) were stopped");
	}

	@Override
	public String getUsage() {
		return "stopplugin <pluginName>";
	}

}
