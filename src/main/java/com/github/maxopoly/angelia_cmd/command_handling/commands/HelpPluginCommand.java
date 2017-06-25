package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.plugin.AngeliaPlugin;

public class HelpPluginCommand extends Command {

	public HelpPluginCommand() {
		super("helpplugin", 1, 1);
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		String pluginName = args[0];
		AngeliaPlugin plugin = connection.getPluginManager().getPlugin(pluginName);
		if (plugin == null) {
			connection.getLogger().warn("No plugin with the name " + pluginName + " could be found");
			return;
		}
		plugin.printHelp(connection.getLogger());
	}

	@Override
	public String getUsage() {
		return "runplugin <pluginName>";
	}

}
