package com.github.maxopoly.angelia_cmd.listener;

import org.apache.logging.log4j.Logger;

import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.player.ChatMessageReceivedEvent;

public class ChatListener implements AngeliaListener {

	private Logger logger;

	public ChatListener(Logger logger) {
		this.logger = logger;
	}

	@AngeliaEventHandler(autoTransfer = true)
	public void chatMessageReceived(ChatMessageReceivedEvent e) {
		logger.info("[CHAT] " + e.getMessage());
	}

}
