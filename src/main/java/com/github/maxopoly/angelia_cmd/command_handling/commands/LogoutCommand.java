package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class LogoutCommand extends Command {
	public LogoutCommand() {
		super("logout", 0, 0, "exit", "quit");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.close();
		connection.getLogger().info("Good bye, have a nice day!");
		System.exit(0);
	}

	@Override
	public String getUsage() {
		return "logout";
	}

}