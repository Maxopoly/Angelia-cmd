package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.command_handling.CommandHandler;
import com.github.maxopoly.angeliacore.connection.ActiveConnectionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AngeliaMain {
	private static Logger logger = LogManager.getLogger("Main");
	private static CommandHandler cmdHandler;
	private static ActiveConnectionManager connManager;

	public static void main(String[] args) {
		connManager = ActiveConnectionManager.getInstance();
		StartUpCommandParser parser = new StartUpCommandParser();
		ServerConnection connection = parser.parse(args, logger);
		if (connection == null) {
			System.exit(0);
			return;
		}
		connManager.initConnection(connection, null);
		connection.getPluginManager().executePlugin("AngeliaCmd", new HashMap<String, String>());
		cmdHandler = new CommandHandler(logger);
		CommandLineReader reader = new CommandLineReader(logger, connManager, connection.getPlayerName(), cmdHandler);
		cmdHandler.handle(parser.getCmdToRun(), connection);
		reader.start();
	}

	public static CommandHandler getCommandHandler() {
		return cmdHandler;
	}

	public static ActiveConnectionManager getConnectionManager() {
		return connManager;
	}
}
