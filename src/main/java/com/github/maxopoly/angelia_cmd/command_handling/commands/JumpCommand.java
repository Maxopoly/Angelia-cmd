package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.actions.actions.Jump;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

public class JumpCommand extends Command {

	public JumpCommand() {
		super("jump", 0, 1);
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		double acceleration;
		if (args.length == 1) {
			try {
				acceleration = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				connection.getLogger().warn("The acceleration supplied was not a valid double");
				return;
			}
		} else {
			acceleration = 5;
		}
		connection.getActionQueue().queue(new Jump(connection, acceleration));
	}

	@Override
	public String getUsage() {
		return "jump [acceleration]";
	}

}
