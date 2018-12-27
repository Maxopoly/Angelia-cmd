package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angelia_cmd.listener.ChatListener;
import com.github.maxopoly.angelia_cmd.listener.PlayerStateListener;
import com.github.maxopoly.angeliacore.plugin.AngeliaLoad;
import com.github.maxopoly.angeliacore.plugin.AngeliaPlugin;

@AngeliaLoad(name = "AngeliaCmd", version = "1.0.03")
public class AngeliaCmdPlugin extends AngeliaPlugin {
	
	private ChatListener chatListener;
	private PlayerStateListener stateListener;

	@Override
	public void start() {
		chatListener = new ChatListener(connection.getLogger());
		stateListener = new PlayerStateListener(connection.getLogger());
		connection.getEventHandler().registerListener(chatListener);
		connection.getEventHandler().registerListener(stateListener);
	}

	@Override
	public void stop() {
		connection.getEventHandler().unregisterListener(chatListener);
		connection.getEventHandler().unregisterListener(stateListener);		
	}

}
