package net.plazmix.lobby.npc;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.updater.SimpleHolographicUpdater;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.mojang.MojangSkin;
import org.bukkit.Location;

public class SWLobbyNPC extends ServerPlayerNPC {

    private final String name;
    private final String serversPrefix;

    public SWLobbyNPC(MojangSkin mojangSkin, String name, String serversPrefix, Location location) {
        super(mojangSkin, location);

        this.name = name;
        this.serversPrefix = serversPrefix;
    }

    @Override
    public void onReceive(@NonNull FakePlayer fakePlayer) {
        addHolographicLine(name);
        addHolographicLine("");

        addLocalizedLine("HOLOGRAM_LOBBY_MODE_NPC_LINE_1");
        addLocalizedLine("HOLOGRAM_LOBBY_MODE_NPC_LINE_2");
        addLocalizedLine("HOLOGRAM_LOBBY_MODE_NPC_LINE_3");

        // FIXME
        holographic.setHolographicUpdater(20, new SimpleHolographicUpdater(holographic) {

            @Override
            public void accept(ProtocolHolographic protocolHolographic) {

                holographic.setLangHolographicLine(3, localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_LOBBY_MODE_NPC_LINE_2")
                .replaceAll("%online%", NumberUtil.spaced(PlazmixCoreApi.getOnlineByServersPrefixes(serversPrefix))));

                holographic.setLangHolographicLine(4, localizationPlayer -> localizationPlayer.getMessageText("HOLOGRAM_LOBBY_MODE_NPC_LINE_3")
                .replaceAll("%servers%", NumberUtil.spaced(PlazmixCoreApi.getConnectedServersCount(serversPrefix))));
            }

        });

        //fakePlayer.setGlowingColor(ChatColor.getByChar(name.charAt(1)));

        System.out.println("GetOnline by Prefix " + serversPrefix + " = " + NumberUtil.spaced(PlazmixCoreApi.getOnlineByServersPrefixes(serversPrefix)));
        System.out.println("GetServers by Prefix " + serversPrefix + " = " + NumberUtil.spaced(PlazmixCoreApi.getConnectedServersCount(serversPrefix)));

        fakePlayer.setClickAction(player -> PlazmixCoreApi.dispatchCommand(player, "gameselector " + serversPrefix));

        enableAutoLooking(10);
    }

}
