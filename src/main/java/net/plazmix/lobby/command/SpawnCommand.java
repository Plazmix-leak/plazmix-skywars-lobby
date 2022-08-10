package net.plazmix.lobby.command;

import net.plazmix.command.BaseCommand;
import net.plazmix.lobby.SkyWarsLobbyPlugin;
import net.plazmix.utility.location.LocationUtil;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand<Player> {

    private final SkyWarsLobbyPlugin plugin;

    public SpawnCommand(SkyWarsLobbyPlugin plugin) {
        super("spawn", "ызфцт", "спаун", "спавн");
        this.plugin = plugin;
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        player.teleport(LocationUtil.stringToLocation(plugin.getConfig().getString("locations.spawn")));
    }
}
