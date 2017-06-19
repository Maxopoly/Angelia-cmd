package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.actions.actions.DigDown;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class DigDownCommand extends Command {
	public DigDownCommand() {
		super("digdown", 2, 2);
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		int blocks, breakTime;
		try {
			blocks = Integer.parseInt(args[0]);
			breakTime = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			connection.getLogger().warn("One of the arguments is not a number");
			return;
		}
		connection.getActionQueue().queue(
				new DigDown(connection, connection.getPlayerStatus().getLocation(), blocks, breakTime));
	}

	@Override
	public String getUsage() {
		return "digdown <howFar> <breakTime in ticks per block>";
	}

}