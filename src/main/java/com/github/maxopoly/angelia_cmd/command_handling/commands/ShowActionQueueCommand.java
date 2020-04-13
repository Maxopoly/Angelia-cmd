package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class ShowActionQueueCommand extends Command {

	public ShowActionQueueCommand() {
		super("showqueue", 0, 0, "actions", "queue", "showactionqueue", "actionqueue");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getLogger().info(connection.getActionQueue().toString());
	}

	@Override
	public String getUsage() {
		return "showqueue";
	}

}
