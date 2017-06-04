package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.listener.ChatListener;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
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
		logger.info("Waiting a second to ensure auth server updated");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			connection.connect();
		} catch (Exception e) {
			logger.info("Connecting failed, exiting", e);
			System.exit(1);
		}
		registerListeners();
	}

	private static void registerListeners() {
		connection.getEventHandler().registerListener(new ChatListener(logger));
	}

	public static Logger getLogger() {
		return logger;
	}
}
