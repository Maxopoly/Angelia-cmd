package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class ReloadConfigCommand extends Command {

	public ReloadConfigCommand() {
		super("reloadconfig", 0, 0);
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getConfig().reloadConfig();
		System.out.println(connection.getConfig().holdBlockModel());
	}

	@Override
	public String getUsage() {
		return "reloadconfig";
	}

}
