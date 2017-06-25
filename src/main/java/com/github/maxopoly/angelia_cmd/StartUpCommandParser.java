package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.SessionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.login.AuthenticationHandler;
import java.io.Console;
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
		Console c = System.console();
		try {
			CommandLine cmd = parser.parse(options, args);
			AuthenticationHandler auth;
			String userName;
			if (!cmd.hasOption("user")) {
				if (c == null) {
					logger.info("You have to supply a username");
					return null;
				}
				userName = c.readLine("Enter the email adress of your minecraft account:\n");
			} else {
				userName = cmd.getOptionValue("user");
			}
			auth = sessionManager.getAccount(userName.toLowerCase());
			if (auth == null) {
				String password;
				if (!cmd.hasOption("password")) {
					if (c == null) {
						logger
								.info("No password supplied, no valid auth token was saved and no console to enter a password manually was found.");
						return null;
					}
					password = new String(c.readPassword("Enter your minecraft password:\n"));
				} else {
					password = cmd.getOptionValue("password");
				}
				auth = sessionManager.authNewAccount(userName, password);
				if (auth == null) {
					logger.info("Wrong password");
					return null;
				}
			}
			String serverIP;
			String portString;
			if (!cmd.hasOption("ip")) {
				if (c == null) {
					logger.info("No ip/domain supplied and no console available");
					return null;
				}
				serverIP = c.readLine("Enter the ip/domain of the server you want to connect to:\n");
				if (serverIP.contains(":")) {
					String[] temp = serverIP.split(":");
					serverIP = temp[0];
					portString = temp[1];
				}
			} else {
				serverIP = cmd.getOptionValue("ip");
			}
			if (cmd.hasOption("port")) {
				portString = cmd.getOptionValue("port");
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
