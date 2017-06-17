package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.command_handling.CommandHandler;
import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.actions.DigDown;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.play.packets.out.ChatPacket;
import com.github.maxopoly.angeliacore.connection.play.packets.out.ClientStatusPacket;
import com.github.maxopoly.angeliacore.connection.play.packets.out.MovePacket;
import com.github.maxopoly.angeliacore.model.Location;
import java.io.Console;
import java.io.IOException;
import org.apache.logging.log4j.Logger;

public class CommandLineReader {

	private Mode mode;

	enum Mode {
		CHAT, COMMAND;
	}

	private Logger logger;
	private ServerConnection connection;
	private CommandHandler cmdHandler;

	public CommandLineReader(Logger logger, ServerConnection connection, CommandHandler cmdHandler) {
		this.logger = logger;
		this.connection = connection;
		this.mode = Mode.CHAT;
		this.cmdHandler = cmdHandler;
	}

	public void start() {
		Console c = System.console();
		if (c == null) {
			logger.error("No open console was found, assuming we are running as daemon and continue anyway");
			return;
		}
		while (true) {
			String msg = c.readLine("");
			if (msg.equals("++")) {
				if (mode == Mode.CHAT) {
					mode = Mode.COMMAND;
					logger.info("--- Switched to command mode");
				} else if (mode == Mode.COMMAND) {
					mode = Mode.CHAT;
					logger.info("--- Switched to chat mode");
				}
				continue;
			}
			if (mode == Mode.CHAT) {
				sendChatMsg(msg);
				continue;
			}
			if (mode == Mode.COMMAND) {
				cmdHandler.handle(msg, connection);
			}
		}
	}

	private void sendChatMsg(String msg) {
		ChatPacket packet;
		try {
			packet = new ChatPacket(msg);
		} catch (IOException e) {
			logger.error("Failed to create msg packet with msg " + msg, e);
			return;
		}
		try {
			connection.sendPacket(packet);
		} catch (IOException e) {
			logger.error("Failed to send message, server might have disconnected?");
		}
	}

	private void handleCommand(String cmd) {
		String[] args = cmd.split(" ");
		ActionQueue queue = connection.getActionQueue();
		try {
			switch (args[0].toLowerCase()) {
				case "respawn":
					connection.sendPacket(new ClientStatusPacket(0));
					logger.info("--- Respawning");
					break;
				case "status":
					logger.info(connection.getPlayerStatus().toString());
					break;
				case "digdown":
					queue.queue(new DigDown(connection, connection.getPlayerStatus().getLocation(), 2, 12));
					break;
				case "location":
					logger.info(connection.getPlayerStatus().getLocationString());
					break;
				case "xp":
					logger.info(connection.getPlayerStatus().getXPString());
					break;
				case "health":
					logger.info(connection.getPlayerStatus().getHealthString());
					break;
				case "move":
					if (args.length == 1) {
						logger.error("Supply a direction");
						return;
					}
					String direction = args[1];
					Location status = connection.getPlayerStatus().getLocation();
					switch (direction.toLowerCase()) {
						case "north":
							connection.getPlayerStatus().updatePosition(status.getX(), status.getY(), status.getZ() + 1);
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						case "south":
							connection.getPlayerStatus().updatePosition(status.getX(), status.getY(), status.getZ() - 1);
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						case "west":
							connection.getPlayerStatus().updatePosition(status.getX() - 1, status.getY(), status.getZ());
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						case "east":
							connection.getPlayerStatus().updatePosition(status.getX() + 1, status.getY(), status.getZ());
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						case "up":
							connection.getPlayerStatus().updatePosition(status.getX(), status.getY() + 1, status.getZ());
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						case "down":
							connection.getPlayerStatus().updatePosition(status.getX(), status.getY() - 1, status.getZ());
							connection.sendPacket(new MovePacket(status.getX(), status.getY(), status.getZ()));
							break;
						default:
							logger.error(direction + " is not a valid direction");
							return;
					}
					break;
				default:
					logger.info("Command '" + args[0] + "' could not be recognized");
			}
		} catch (IOException e) {
			logger.error("Failed to execute command", e);
		}
	}

}
