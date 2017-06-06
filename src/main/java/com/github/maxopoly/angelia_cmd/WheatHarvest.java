package com.github.maxopoly.angelia_cmd;

import com.github.maxopoly.angeliacore.actions.ActionQueue;
import com.github.maxopoly.angeliacore.actions.BreakAction;
import com.github.maxopoly.angeliacore.actions.EatingAction;
import com.github.maxopoly.angeliacore.actions.MovementAction;
import com.github.maxopoly.angeliacore.api.MovementDirection;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.event.AngeliaEventHandler;
import com.github.maxopoly.angeliacore.event.AngeliaListener;
import com.github.maxopoly.angeliacore.event.events.ActionQueueEmptiedEvent;
import com.github.maxopoly.angeliacore.event.events.HungerChangeEvent;
import com.github.maxopoly.angeliacore.model.ItemStack;
import com.github.maxopoly.angeliacore.model.Location;
import com.github.maxopoly.angeliacore.model.inventory.PlayerInventory;
import com.github.maxopoly.angeliacore.util.fields.HorizontalField;
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
				snakeLines);
		this.locIterator = field.iterator();
		this.queue = connection.getActionQueue();
		connection.getEventHandler().registerListener(this);
	}

	@AngeliaEventHandler
	public void queueEmpty(ActionQueueEmptiedEvent e) {
		if (locIterator.hasNext()) {
			Location target = locIterator.next();
			queue.queue(new MovementAction(connection, target.getBlockCenterXZ(), MovementAction.SPRINTING_SPEED, connection
					.getTicksPerSecond()));
			queue.queue(new BreakAction(connection, target, 0, (byte) 1));
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
		int foodSlot = inv.findHotbarSlot(new ItemStack((short) 297));
		if (foodSlot == -1) {
			// none found
			return;
		}
		connection.getPlayerStatus().setSelectedHotbarSlot(foodSlot);
		queue.queue(new EatingAction(connection, 20));
	}

}
