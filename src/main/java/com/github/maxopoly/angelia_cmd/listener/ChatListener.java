package com.github.maxopoly.angelia_cmd.listener;

import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ChatMessageReceivedEvent;
import org.apache.logging.log4j.Logger;

public class ChatListener implements AngeliaListener {

	private Logger logger;

	public ChatListener(Logger logger) {
		this.logger = logger;
	}

	@AngeliaEventHandler
	public void chatMessageReceived(ChatMessageReceivedEvent e) {
		logger.info("[CHAT] " + e.getMessage());
	}

}
