package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class GetNameCommand extends Command {

	public GetNameCommand() {
		super("name", 0, 0, "getname", "whoami");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getLogger().info("You are " + connection.getPlayerName());
	}

	@Override
	public String getUsage() {
		return "name";
	}

}
