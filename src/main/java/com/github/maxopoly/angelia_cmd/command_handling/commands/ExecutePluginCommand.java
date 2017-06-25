package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import java.util.Arrays;

public class ExecutePluginCommand extends Command {

	public ExecutePluginCommand() {
		super("runplugin", 1, Integer.MAX_VALUE, "executeplugin", "startplugin");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		String pluginName = args[0];
		if (args.length == 1) {
			args = new String[0];
		} else {
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		connection.getPluginManager().executePlugin(pluginName, args);
	}

	@Override
	public String getUsage() {
		return "runplugin <pluginName> [-argsName1] [argsValue1] [...]";
	}

}
