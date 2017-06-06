package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.listener.ChatListener;
import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.BlockPlaceAction;
import com.github.maxopoly.angeliacore.actions.BreakAction;
import com.github.maxopoly.angeliacore.actions.LookChangeAction;
import com.github.maxopoly.angeliacore.actions.SlotSelectionAction;
import com.github.maxopoly.angeliacore.actions.WaitingAction;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.model.Location;
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
		Location playerLoc = connection.getPlayerStatus().getLocation();
		ActionQueue queue = connection.getActionQueue();
		queue.queue(new WaitingAction(connection, 60));
		queue.queue(new SlotSelectionAction(connection, 0));
		Location loc = new Location(-7407, 71, 6404, 0.0f, 0.0f);
		Location lookLocation = new Location(-7406.5, 71.5, 6404.5, 0.0f, 0.0f);
		queue.queue(new LookChangeAction(connection, lookLocation));
		for (int i = 0; i < 50; i++) {
			queue.queue(new BreakAction(connection, loc, 40, (byte) 1));
			queue.queue(new WaitingAction(connection, 20));
			queue.queue(new BlockPlaceAction(connection, loc, 1));
		}
	}

	private static void registerListeners() {
		connection.getEventHandler().registerListener(new ChatListener(logger));
	}

	public static Logger getLogger() {
		return logger;
	}
}
