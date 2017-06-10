package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.actions.actions.inventory.PickHotbarItemByType;

import com.github.maxopoly.angeliacore.actions.actions.inventory.RefillHotbarWithType;
import com.github.maxopoly.angeliacore.actions.actions.inventory.ChangeSelectedItem;
import com.github.maxopoly.angeliacore.util.HorizontalField;
import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.CodeAction;
import com.github.maxopoly.angeliacore.actions.actions.DigDown;
import com.github.maxopoly.angeliacore.actions.actions.Eat;
import com.github.maxopoly.angeliacore.actions.actions.LookAtAndBreakBlock;
import com.github.maxopoly.angeliacore.actions.actions.LookAtAndPlaceBlock;
import com.github.maxopoly.angeliacore.actions.actions.MoveTo;
import com.github.maxopoly.angeliacore.actions.actions.Wait;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ActionQueueEmptiedEvent;
import com.github.maxopoly.angeliacore.event.events.HungerChangeEvent;
import com.github.maxopoly.angeliacore.event.events.TeleportByServerEvent;
import com.github.maxopoly.angeliacore.model.ItemStack;
import com.github.maxopoly.angeliacore.model.Location;
import com.github.maxopoly.angeliacore.model.MovementDirection;
import com.github.maxopoly.angeliacore.model.inventory.PlayerInventory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DiggingBot implements AngeliaListener {

	private static final int BREAK_TIME = 14;
	private static final int locationCacheSize = 10;

	private ServerConnection connection;
	private HorizontalField field;
	private Iterator<Location> locIterator;
	private ActionQueue queue;
	private List<Location> lastLocations;
	private boolean movingBack;

	public DiggingBot(ServerConnection connection, int lowerX, int upperX, int lowerZ, int upperZ, int y,
			MovementDirection startingDirection, MovementDirection secondaryDirection, boolean snakeLines) {
		this.connection = connection;
		this.movingBack = true;
		this.field = new HorizontalField(lowerX, upperX, lowerZ, upperZ, y, startingDirection, secondaryDirection,
				snakeLines, 1);
		this.locIterator = field.iterator();
		this.lastLocations = new LinkedList<Location>();
		this.queue = connection.getActionQueue();
		connection.getEventHandler().registerListener(this);
		// explicitly reset slot selection
		queue.queue(new MoveTo(connection, field.getStartingLocation().getBlockCenterXZ(), MoveTo.SPRINTING_SPEED,
				connection.getTicksPerSecond()));
		queue.queue(new CodeAction(connection) {

			@Override
			public void execute() {
				DiggingBot.this.movingBack = false;

			}
		});
		queue.queue(new ChangeSelectedItem(connection, 0));
		queueEmpty(null);
	}

	@AngeliaEventHandler
	public void queueEmpty(ActionQueueEmptiedEvent e) {
		if (locIterator.hasNext()) {
			queue.queue(new RefillHotbarWithType(connection, (short) 274));
			final PickHotbarItemByType picker = new PickHotbarItemByType(connection, (short) 274);
			queue.queue(picker);
			queue.queue(new CodeAction(connection) {

				@Override
				public void execute() {
					if (!picker.wasFound()) {
						connection.getLogger().info("Failed to find tool to dig, exiting");
						System.exit(0);
					}

				}
			});
			Location target = locIterator.next();
			lastLocations.add(target);
			if (lastLocations.size() > locationCacheSize) {
				lastLocations.remove(0);
			}
			mineBlocksAndMoveIn(target, BREAK_TIME);
			placeTorch(target);
		} else {
			movingBack = true;
			Location start = field.getStartingLocation();
			queue.queue(new MoveTo(connection, start.getBlockCenterXZ(), MoveTo.SPRINTING_SPEED, connection
					.getTicksPerSecond()));
			queue.queue(new DigDown(connection, start.getBlockCenterXZ(), 2, BREAK_TIME));
			field = field.copy(field.getY() - 2);
			locIterator = field.iterator();
			queue.queue(new CodeAction(connection) {

				@Override
				public void execute() {
					DiggingBot.this.movingBack = false;
				}
			});
		}
	}

	private void mineBlocksAndMoveIn(Location loc, int breakTime) {
		Location upperLoc = new Location(loc.getBlockX(), (int) loc.getY() + 1, (int) loc.getZ());
		queue.queue(new LookAtAndBreakBlock(connection, upperLoc, breakTime));
		queue.queue(new LookAtAndBreakBlock(connection, loc, breakTime));
		queue.queue(new MoveTo(connection, loc.getBlockCenterXZ(), MoveTo.SPRINTING_SPEED, connection.getTicksPerSecond()));
	}

	private void placeTorch(Location loc) {
		if (loc.getBlockX() % 4 != 0 || loc.getBlockZ() % 4 != 0) {
			return;
		}
		PlayerInventory inv = connection.getPlayerStatus().getPlayerInventory();
		int seedSlot = inv.findHotbarSlotByType(new ItemStack((short) 50));
		if (seedSlot != -1 && seedSlot != connection.getPlayerStatus().getSelectedHotbarSlot()) {
			queue.queue(new Wait(connection, 5));
			queue.queue(new ChangeSelectedItem(connection, seedSlot));
			queue.queue(new LookAtAndPlaceBlock(connection, loc));
			queue.queue(new Wait(connection, 5));
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

	@AngeliaEventHandler
	public void teleportedBack(TeleportByServerEvent e) {
		if (movingBack) {
			return;
		}
		Location oldBlockLoc = e.getLocationTeleportedTo().toBlockLocation();
		if (lastLocations.contains(oldBlockLoc)) {
			System.out.println("Detected failed break, rolling back to " + oldBlockLoc.toString());
			queue.clear();
			queue.queue(new RefillHotbarWithType(connection, (short) 274));
			queue.queue(new PickHotbarItemByType(connection, (short) 274));
			int index = lastLocations.indexOf(oldBlockLoc);
			for (int i = index; i < lastLocations.size(); i++) {
				// maybe we hit something harder, so let's take extra time
				mineBlocksAndMoveIn(lastLocations.get(i), BREAK_TIME * 5);
			}
		}
	}
}
