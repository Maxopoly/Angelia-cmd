package com.github.maxopoly.angelia_cmd.command_handling.commands;

import com.github.maxopoly.angelia_cmd.command_handling.Command;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.model.ThePlayer;

public class GetPlayerStatsCommand extends Command {

	public GetPlayerStatsCommand() {
		super("stats", 0, 0, "playerstats", "status");
	}

	@Override
	public void execute(String[] args, ServerConnection connection) {
		ThePlayer status = connection.getPlayerStatus();
		connection.getLogger().info("Loc: " + status.getLocation().toString());
		//TODO
	}

	@Override
	public String getUsage() {
		return "stats";
	}

}
