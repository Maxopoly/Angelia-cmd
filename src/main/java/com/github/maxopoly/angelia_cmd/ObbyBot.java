package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.actions.actions.inventory.PickHotbarItemByType;

import com.github.maxopoly.angeliacore.actions.actions.inventory.RefillHotbarWithType;
import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.actions.ChangeViewingDirection;
import com.github.maxopoly.angeliacore.actions.actions.Eat;
import com.github.maxopoly.angeliacore.actions.actions.Logoff;
import com.github.maxopoly.angeliacore.actions.actions.LookAtAndBreakBlock;
import com.github.maxopoly.angeliacore.actions.actions.MoveTo;
import com.github.maxopoly.angeliacore.actions.actions.PlaceBlock;
import com.github.maxopoly.angeliacore.actions.actions.Wait;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ActionQueueEmptiedEvent;
import com.github.maxopoly.angeliacore.event.events.HealthChangeEvent;
import com.github.maxopoly.angeliacore.event.events.HungerChangeEvent;
import com.github.maxopoly.angeliacore.model.BlockFace;
import com.github.maxopoly.angeliacore.model.ItemStack;
import com.github.maxopoly.angeliacore.model.Location;
import com.github.maxopoly.angeliacore.model.PlayerStatus;

public class ObbyBot implements AngeliaListener {

	private ActionQueue queue;
	private ServerConnection connection;
	private Location startingLocation;
	private Location blockBelowObby;
	private Location obbyBlock;
	private Location buttonBlock;

	public ObbyBot(ServerConnection connection, Location startingLocation) {
		this.connection = connection;
		this.queue = connection.getActionQueue();
		connection.getEventHandler().registerListener(this);
		this.startingLocation = startingLocation.toBlockLocation();
		this.blockBelowObby = this.startingLocation.relativeBlock(0, 0, 1);
		this.obbyBlock = this.startingLocation.relativeBlock(0, 1, 1);
		this.buttonBlock = this.startingLocation.relativeBlock(0, 2, -1);
		queue.queue(new MoveTo(connection, startingLocation.getBlockCenterXZ(), MoveTo.SPRINTING_SPEED, connection
				.getTicksPerSecond()));
	}

	@AngeliaEventHandler
	public void queueEmpty(ActionQueueEmptiedEvent e) {
		PlayerStatus status = connection.getPlayerStatus();
		if (!status.getLocation().toBlockLocation().equals(startingLocation)) {
			connection.getLogger().error(
					"Player is at wrong location, current location: " + status.getLocation().toString() + ", expected: "
							+ startingLocation.toString());
			queue.queue(new Logoff(connection));
			return;
		}
		moveStringAsNeeded();
		queue.queue(new ChangeViewingDirection(connection, blockBelowObby));
		queue.queue(new PickHotbarItemByType(connection, (short) 287)); // string
		queue.queue(new PlaceBlock(connection, obbyBlock, BlockFace.TOP));
		pressButton();
		queue.queue(new Wait(connection, 20));
		pressButton();
		queue.queue(new PickHotbarItemByType(connection, (short) 278)); // diamond pick
		// 189 break ticks
		queue.queue(new LookAtAndBreakBlock(connection, obbyBlock, 190));

	}

	private void pressButton() {
		queue.queue(new ChangeViewingDirection(connection, buttonBlock.getBlockCenter()));
		queue.queue(new PlaceBlock(connection, buttonBlock, BlockFace.SOUTH));
	}

	private void moveStringAsNeeded() {
		int slot = connection.getPlayerStatus().getPlayerInventory().findHotbarSlotByType(new ItemStack((short) 287));
		if (slot != -1) {
			// still some there
			return;
		}
		queue.queue(new RefillHotbarWithType(connection, (short) 287));
	}

	@AngeliaEventHandler
	public void damageReceived(HealthChangeEvent e) {
		if (e.getNewValue() < e.getOldValue()) {
			queue.clear();
			queue.queue(new Logoff(connection));
		}
	}

	@AngeliaEventHandler
	public void hungerChange(HungerChangeEvent e) {
		if (e.getNewValue() > 7) {
			return;
		}
		queue.queue(new PickHotbarItemByType(connection, (short) 297)); // bread
		queue.queue(new Eat(connection, 20));
	}

}
