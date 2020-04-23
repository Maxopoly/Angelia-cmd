package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class ClearActionQueueCommand extends Command {

	public ClearActionQueueCommand() {
		super("clearqueue", 0, 0, "clearactionqueue");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getActionQueue().clear();
		connection.getLogger().info("Cleared action queue");
	}

	@Override
	public String getUsage() {
		return "clearqueue";
	}

}
