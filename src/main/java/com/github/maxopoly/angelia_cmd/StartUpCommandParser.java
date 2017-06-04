package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.SessionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.login.AuthenticationHandler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;

public class StartUpCommandParser {

	private static Options options;

	static {
		options = new Options();
		options.addOption("user", true, "Username");
		options.addOption("password", true, "Password");
		options.addOption("ip", true, "Server IP");
		options.addOption("port", true, "Server port");
	}

	public static ServerConnection parse(String[] args, Logger logger) {
		CommandLineParser parser = new DefaultParser();
		SessionManager sessionManager = new SessionManager(logger, true);
		try {
			CommandLine cmd = parser.parse(options, args);
			AuthenticationHandler auth;
			if (!cmd.hasOption("user")) {
				logger.info("You have to supply a username");
				return null;
			}
			String userName = cmd.getOptionValue("user");
			auth = sessionManager.getAccount(userName.toLowerCase());
			if (auth == null) {
				if (!cmd.hasOption("password")) {
					logger.info("No password supplied and no valid auth token was saved. Restart with a password!");
					return null;
				}
				String password = cmd.getOptionValue("password");
				auth = sessionManager.authNewAccount(userName, password);
				if (auth == null) {
					logger.info("Wrong password");
					return null;
				}
			}
			if (!cmd.hasOption("ip")) {
				logger.info("You have to supply a server ip/domain");
			}
			String serverIP = cmd.getOptionValue("ip");
			if (cmd.hasOption("port")) {
				String portString = cmd.getOptionValue("port");
				try {
					int port = Integer.parseInt(portString);
					return new ServerConnection(serverIP, port, logger, auth);
				} catch (NumberFormatException e) {
					logger.error(portString + " is not a valid number");
				}
			} else {
				return new ServerConnection(serverIP, logger, auth);
			}
			return null;
		} catch (ParseException e) {
			logger.error("Failed to parse input", e);
			return null;
		}
	}

}
