package com.github.maxopoly.angelia_cmd.listener;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.player.HealthChangeEvent;

public class PlayerStateListener implements AngeliaListener {

	private Logger logger;
	private DecimalFormat formatter;

	public PlayerStateListener(Logger logger) {
		this.logger = logger;
		this.formatter = new DecimalFormat("#.###");
	}

	@AngeliaEventHandler(autoTransfer = true)
	public void chatMessageReceived(HealthChangeEvent e) {
		if (e.getNewValue() == e.getOldValue()) {
			return;
		}
		double change = e.getOldValue() - e.getNewValue();
		if (change > 0) {
			logger.info("Received " + formatter.format(change) + " damage, total health is "
					+ formatter.format(e.getNewValue()));
		} else {
			if (change != 0) {
				logger.info("Healed " + formatter.format(change * -1) + " health, total health is "
						+ formatter.format(e.getNewValue()));
			}
		}
		if (e.getNewValue() <= 0) {
			logger.info("YOU ARE DEAD!");
		}
	}
}
