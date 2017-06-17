package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.command_handling.CommandHandler;
import com.github.maxopoly.angelia_cmd.listener.ChatListener;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static Logger logger = LogManager.getLogger("Main");
	private static ServerConnection connection;
	private static CommandHandler cmdHandler;

	public static void main(String[] args) {
		connection = StartUpCommandParser.parse(args, logger);
		if (connection == null) {
			System.exit(0);
			return;
		}
		try {
			connection.connect();
		} catch (Exception e) {
			logger.info("Connecting failed, exiting", e);
			System.exit(1);
		}
		registerListeners();
		while (!connection.getPlayerStatus().isInitialized()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cmdHandler = new CommandHandler(logger);
		CommandLineReader reader = new CommandLineReader(logger, connection, cmdHandler);
		reader.start();
	}

	private static void registerListeners() {
		connection.getEventHandler().registerListener(new ChatListener(logger));
	}

	public static Logger getLogger() {
		return logger;
	}
}
