package com.github.maxopoly.angelia_cmd.command_handling;

import com.github.maxopoly.angeliacore.connection.ServerConnection;

public abstract class Command {

	private String identifier;
	private int minArgs;
	private int maxArgs;
	private String[] alternativeIdentifiers;

	public Command(String identifier, int minArgs, int maxArgs, String... alt) {
		this.identifier = identifier;
		this.minArgs = minArgs;
		this.maxArgs = maxArgs;
		this.alternativeIdentifiers = alt;
		this.alternativeIdentifiers = alt;
	}

	public abstract void execute(String[] args, ServerConnection connection);

	/**
	 * Example usage like 'moveto <x> <y> <z>'
	 */
	public abstract String getUsage();

	/**
	 * @return The actual string entered to run this command
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return Minimum amount of arguments
	 */
	public int minimumArgs() {
		return minArgs;
	}

	/**
	 * @return Maximum amount of arguments
	 */
	public int maximumArgs() {
		return maxArgs;
	}

	/**
	 * @return Alternative commands, which will also execute this
	 */
	public String[] getAlternativeIdentifiers() {
		return alternativeIdentifiers;
	}

}
