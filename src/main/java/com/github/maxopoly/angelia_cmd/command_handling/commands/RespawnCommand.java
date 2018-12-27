package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.actions.actions.RespawnAction;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

/**
 * Attempts to respawn
 *
 */
public class RespawnCommand extends Command {

	public RespawnCommand() {
		super("respawn", 0, 0);
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		connection.getActionQueue().queue(new RespawnAction(connection));
		connection.getLogger().info("Queued respawn action");
	}

	@Override
	public String getUsage() {
		return "respawn";
	}

}

