package net.plazmix.lobby.listener;

import lombok.RequiredArgsConstructor;
import net.plazmix.lobby.soulsbox.SoulsBox;
import net.plazmix.lobby.soulsbox.gui.SoulsBoxMenu;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public final class SoulsBoxListener implements Listener {
    private final SoulsBox soulsBox;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || PlayerCooldownUtil.hasCooldown("soulsBox_click", event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (!soulsBox.getLocation().getBlock().equals(clickedBlock)) {
            return;
        }

        if (!soulsBox.isAlreadyOpening()) {
            new SoulsBoxMenu(soulsBox).openInventory(player);

        } else {

            player.sendMessage(SoulsBox.ALREADY_OPENING_MESSAGE);
        }

        PlayerCooldownUtil.putCooldown("soulsBox_click", event.getPlayer(), 250);
    }

}
