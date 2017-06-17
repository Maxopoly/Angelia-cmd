package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.CodeAction;
import com.github.maxopoly.angeliacore.actions.actions.Eat;
import com.github.maxopoly.angeliacore.actions.actions.Logoff;
import com.github.maxopoly.angeliacore.actions.actions.LookAtAndBreakBlock;
import com.github.maxopoly.angeliacore.actions.actions.LookAtAndPlaceBlock;
import com.github.maxopoly.angeliacore.actions.actions.MoveTo;
import com.github.maxopoly.angeliacore.actions.actions.Wait;
import com.github.maxopoly.angeliacore.actions.actions.inventory.ChangeSelectedItem;
import com.github.maxopoly.angeliacore.actions.actions.inventory.PickHotbarItemByType;
import com.github.maxopoly.angeliacore.actions.actions.inventory.RefillHotbarWithType;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ActionQueueEmptiedEvent;
import com.github.maxopoly.angeliacore.event.events.HealthChangeEvent;
import com.github.maxopoly.angeliacore.event.events.HungerChangeEvent;
import com.github.maxopoly.angeliacore.event.events.TeleportByServerEvent;
import com.github.maxopoly.angeliacore.model.ItemStack;
import com.github.maxopoly.angeliacore.model.Location;
import com.github.maxopoly.angeliacore.model.Material;
import com.github.maxopoly.angeliacore.model.MovementDirection;
import com.github.maxopoly.angeliacore.model.inventory.PlayerInventory;
import com.github.maxopoly.angeliacore.util.HorizontalField;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BranchMiningBot implements AngeliaListener {

	private static final int BREAK_TIME = 7;
	private static final int locationCacheSize = 10;

	private ServerConnection connection;
	private HorizontalField field;
	private Iterator<Location> locIterator;
	private ActionQueue queue;
	private List<Location> lastLocations;
	private Material tool;

	public BranchMiningBot(ServerConnection connection, int lowerX, int upperX, int lowerZ, int upperZ, int y,
			MovementDirection startingDirection, MovementDirection secondaryDirection, boolean snakeLines, Material tool) {
		this.connection = connection;
		this.field = new HorizontalField(lowerX, upperX, lowerZ, upperZ, y, startingDirection, secondaryDirection,
				snakeLines, 3);
		this.tool = tool;
		this.locIterator = field.iterator();
		this.lastLocations = new LinkedList<Location>();
		this.queue = connection.getActionQueue();
		connection.getEventHandler().registerListener(this);
		// explicitly reset slot selection
		queue.queue(new MoveTo(connection, field.getStartingLocation().getBlockCenterXZ(), MoveTo.SPRINTING_SPEED,
				connection.getTicksPerSecond()));
		queue.queue(new ChangeSelectedItem(connection, 0));
		queueEmpty(null);
	}

	@AngeliaEventHandler
	public void queueEmpty(ActionQueueEmptiedEvent e) {
		if (locIterator.hasNext()) {
			queue.queue(new RefillHotbarWithType(connection, tool));
			final PickHotbarItemByType picker = new PickHotbarItemByType(connection, tool);
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
			// placeTorch(target);
		} else {
			System.out.println("done");
			System.exit(0);
		}
	}

	private void mineBlocksAndMoveIn(Location loc, int breakTime) {
		Location upperLoc = new Location(loc.getBlockX(), (int) loc.getY() + 1, (int) loc.getZ());
		Location upperLoc2 = new Location(loc.getBlockX(), (int) loc.getY() + 2, (int) loc.getZ());
		Location upperLoc3 = new Location(loc.getBlockX(), (int) loc.getY() + 3, (int) loc.getZ());
		Location upperLoc4 = new Location(loc.getBlockX(), (int) loc.getY() + 4, (int) loc.getZ());
		queue.queue(new LookAtAndBreakBlock(connection, upperLoc, breakTime));
		queue.queue(new LookAtAndBreakBlock(connection, loc, breakTime));
		queue.queue(new LookAtAndBreakBlock(connection, upperLoc2, breakTime));
		queue.queue(new LookAtAndBreakBlock(connection, upperLoc3, breakTime));
		queue.queue(new LookAtAndBreakBlock(connection, upperLoc4, breakTime));
		queue.queue(new MoveTo(connection, loc.getBlockCenterXZ(), MoveTo.SPRINTING_SPEED, connection.getTicksPerSecond()));
	}

	private void placeTorch(Location loc) {
		if (loc.getBlockX() % 4 != 0 || loc.getBlockZ() % 4 != 0) {
			return;
		}
		PlayerInventory inv = connection.getPlayerStatus().getPlayerInventory();
		int seedSlot = inv.getHotbar().findSlotByType(new ItemStack(Material.TORCH));
		if (seedSlot != -1 && seedSlot != connection.getPlayerStatus().getSelectedHotbarSlot()) {
			queue.queue(new Wait(connection, 5));
			queue.queue(new ChangeSelectedItem(connection, seedSlot));
			queue.queue(new LookAtAndPlaceBlock(connection, loc));
			queue.queue(new Wait(connection, 5));
		}
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
		queue.queue(new PickHotbarItemByType(connection, Material.BREAD)); // bread
		queue.queue(new Eat(connection, 20));
	}

	@AngeliaEventHandler
	public void teleportedBack(TeleportByServerEvent e) {
		Location oldBlockLoc = e.getLocationTeleportedTo().toBlockLocation();
		if (lastLocations.contains(oldBlockLoc)) {
			System.out.println("Detected failed break, rolling back to " + oldBlockLoc.toString());
			queue.clear();
			queue.queue(new RefillHotbarWithType(connection, tool));
			queue.queue(new PickHotbarItemByType(connection, tool));
			int index = lastLocations.indexOf(oldBlockLoc);
			for (int i = index; i < lastLocations.size(); i++) {
				// maybe we hit something harder, so let's take extra time
				mineBlocksAndMoveIn(lastLocations.get(i), BREAK_TIME * 10);
			}
		}
	}

}
