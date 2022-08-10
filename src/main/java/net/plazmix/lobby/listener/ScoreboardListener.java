package net.plazmix.lobby.listener;

import net.plazmix.lobby.scoreboard.SkyWarsScoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new SkyWarsScoreboard(event.getPlayer());
    }

}
