package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.listener.ChatListener;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.model.MovementDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static Logger logger = LogManager.getLogger("Main");
	private static ServerConnection connection;

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
		queueTestDigging();
		CommandLineReader reader = new CommandLineReader(logger, connection);
		reader.start();
	}

	private static void queueTestDigging() {
		// new DiggingBot(connection, -9168, -9137, 9184, 9199, 39, MovementDirection.SOUTH, MovementDirection.WEST, true);
		new BranchMiningBot(connection, 9913, 9963, 9574, 9624, 91, MovementDirection.NORTH, MovementDirection.EAST, true,
				(short) 257);
	}

	private static void registerListeners() {
		connection.getEventHandler().registerListener(new ChatListener(logger));
	}

	public static Logger getLogger() {
		return logger;
	}
}
