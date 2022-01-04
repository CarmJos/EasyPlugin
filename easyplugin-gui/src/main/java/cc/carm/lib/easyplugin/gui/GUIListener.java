package cc.carm.lib.easyplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GUIListener implements Listener {

	GUI currentGUI;

	public GUIListener(GUI gui) {
		this.currentGUI = gui;
	}

	public GUI getCurrentGUI() {
		return currentGUI;
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if (!GUI.hasOpenedGUI(player)) return;
		if (GUI.getOpenedGUI(player) != getCurrentGUI()) return;

		getCurrentGUI().rawClickListener(event);

		if (event.getSlot() == -999 && getCurrentGUI().cancelOnOuter) {
			event.setCancelled(true);
			return;
		}

		if (event.getClickedInventory() == null) return;

		if (event.getClickedInventory().equals(getCurrentGUI().inv)) {

			if (getCurrentGUI().cancelOnTarget) event.setCancelled(true);

			if (event.getSlot() != -999) {
				GUIItem clickedItem = getCurrentGUI().getItem(event.getSlot());
				if (clickedItem != null) {
					if (clickedItem.isActionActive()) {
						clickedItem.onClick(event.getClick());
						clickedItem.rawClickAction(event);
						clickedItem.actions.forEach(action -> action.run(event.getClick(), player));
					}
					clickedItem.actionsIgnoreActive.forEach(action -> action.run(event.getClick(), player));
				}
			}

		} else if (event.getClickedInventory().equals(player.getInventory()) && getCurrentGUI().cancelOnSelf) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		if (e.getInventory().equals(getCurrentGUI().inv)
				|| e.getInventory().equals(e.getWhoClicked().getInventory())) {
			getCurrentGUI().onDrag(e);
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (!event.getInventory().equals(getCurrentGUI().inv)) return;

		HandlerList.unregisterAll(this);
		getCurrentGUI().listener = null;
		GUI.removeOpenedGUI((Player) event.getPlayer());
		getCurrentGUI().onClose();

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		GUI.removeOpenedGUI(event.getPlayer());
	}

}
