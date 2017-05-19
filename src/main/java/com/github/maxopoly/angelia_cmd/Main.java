package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.connection.ServerConnection;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) {
		if (args.length > 4 || args.length < 3) {
			logger.error(args.length
					+ " arguments were provided, but allowed argument format is: loginName password serverIP [port]");
			return;
		}
		logger.error("HI");
		String loginName = args[0];
		String password = args[1];
		String serverDomain = args[2];
		ServerConnection connection;
		if (args.length == 4) {
			String portString = args[3];
			int port;
			try {
				port = Integer.parseInt(portString);
			} catch (NumberFormatException e) {
				logger.error(portString + " is not a valid number");
				return;
			}
			connection = new ServerConnection(serverDomain, port, loginName, password, logger);
		} else {
			connection = new ServerConnection(serverDomain, loginName, password, logger);
		}
		try {
			connection.connect();
		} catch (IOException e) {
			logger.error("Failed to connect:" + e.getMessage(), e);
		}
	}

	public static Logger getLogger() {
		return logger;
	}
}
