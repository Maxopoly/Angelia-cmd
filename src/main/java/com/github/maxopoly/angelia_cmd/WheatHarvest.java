package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.actions.BreakBlock;
import com.github.maxopoly.angeliacore.actions.actions.DetectAndEatFood;
import com.github.maxopoly.angeliacore.actions.actions.MoveTo;
import com.github.maxopoly.angeliacore.actions.actions.PlaceBlock;
import com.github.maxopoly.angeliacore.actions.actions.inventory.ChangeSelectedItem;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ActionQueueEmptiedEvent;
import com.github.maxopoly.angeliacore.event.events.HungerChangeEvent;
import com.github.maxopoly.angeliacore.model.BlockFace;
import com.github.maxopoly.angeliacore.model.ItemStack;
import com.github.maxopoly.angeliacore.model.Location;
import com.github.maxopoly.angeliacore.model.Material;
import com.github.maxopoly.angeliacore.model.MovementDirection;
import com.github.maxopoly.angeliacore.model.inventory.PlayerInventory;
import com.github.maxopoly.angeliacore.util.HorizontalField;
import java.util.Iterator;

public class WheatHarvest implements AngeliaListener {

	private ServerConnection connection;
	private HorizontalField field;
	private Iterator<Location> locIterator;
	private ActionQueue queue;

	public WheatHarvest(ServerConnection connection, int lowerX, int upperX, int lowerZ, int upperZ, int y,
			MovementDirection startingDirection, MovementDirection secondaryDirection, boolean snakeLines) {
		this.connection = connection;
		this.field = new HorizontalField(lowerX, upperX, lowerZ, upperZ, y, startingDirection, secondaryDirection,
				snakeLines, 1);
		this.locIterator = field.iterator();
		this.queue = connection.getActionQueue();
		connection.getEventHandler().registerListener(this);
		queueEmpty(null);
	}

	@AngeliaEventHandler
	public void queueEmpty(ActionQueueEmptiedEvent e) {
		if (locIterator.hasNext()) {
			Location target = locIterator.next();
			queue.queue(new MoveTo(connection, target.getBlockCenterXZ(), MoveTo.SPRINTING_SPEED));
			Location wheatLoc = new Location((int) target.getX(), (int) target.getY() + 1, (int) target.getZ());
			queue.queue(new BreakBlock(connection, wheatLoc, 1, BlockFace.TOP));
			PlayerInventory inv = connection.getPlayerStatus().getPlayerInventory();
			int seedSlot = inv.getHotbar().findSlot(new ItemStack(Material.SEEDS));
			if (seedSlot != -1 && seedSlot != connection.getPlayerStatus().getSelectedHotbarSlot()) {
				queue.queue(new ChangeSelectedItem(connection, seedSlot));
			}
			queue.queue(new PlaceBlock(connection, wheatLoc, BlockFace.TOP));
		} else {
			connection.getEventHandler().unregisterListener(this);
		}
	}

	@AngeliaEventHandler
	public void hungerChange(HungerChangeEvent e) {
		if (e.getNewValue() > 7) {
			return;
		}
		PlayerInventory inv = connection.getPlayerStatus().getPlayerInventory();
		int foodSlot = inv.getHotbar().findSlot(new ItemStack(Material.BREAD));
		if (foodSlot == -1) {
			// none found
			return;
		}
		queue.queue(new ChangeSelectedItem(connection, foodSlot));
		queue.queue(new DetectAndEatFood(connection));
	}

}
