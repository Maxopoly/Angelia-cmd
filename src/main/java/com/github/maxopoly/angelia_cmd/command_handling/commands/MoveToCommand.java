package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angeliacore.model.location.Location;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.actions.actions.SprintTo;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class MoveToCommand extends Command {

	public MoveToCommand() {
		super("move", 2, 2, "moveto", "goto");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		int x, z;
		try {
			x = Integer.parseInt(args[0]);
			z = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			connection.getLogger().warn("One of the args supplied was not a valid integer");
			return;
		}
		connection.getActionQueue().queue(
				new SprintTo(connection, new Location(x, 0, z).getBlockCenterXZ()));
		connection.getLogger().info("Queued movement to " + x + " " + z);
	}

	@Override
	public String getUsage() {
		return "moveto <x> <z>";
	}

}
