package net.plazmix.lobby;

import lombok.Getter;
import net.plazmix.PlazmixApi;
import net.plazmix.cases.CasesManager;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.mysql.type.BasedGameItemsMysqlDatabase;
import net.plazmix.game.mysql.type.UpgradeGameItemsMysqlDatabase;
import net.plazmix.game.user.GameUser;
import net.plazmix.lobby.cases.SkywarsBoxType;
import net.plazmix.lobby.command.SpawnCommand;
import net.plazmix.lobby.item.cage.CageCategory;
import net.plazmix.lobby.item.dance.DanceCategory;
import net.plazmix.lobby.item.killeffect.KillEffectCategory;
import net.plazmix.lobby.item.kit.KitCategory;
import net.plazmix.lobby.item.perk.PerkCategory;
import net.plazmix.lobby.item.predeath.PreDeathCategory;
import net.plazmix.lobby.listener.ScoreboardListener;
import net.plazmix.lobby.listener.SoulsBoxListener;
import net.plazmix.lobby.listener.SpawnListener;
import net.plazmix.lobby.listener.StaticChatListener;
import net.plazmix.lobby.npc.RewardsNPC;
import net.plazmix.lobby.npc.SWLobbyNPC;
import net.plazmix.lobby.npc.SkyWarsSpectateNPC;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.lobby.playertop.PlayerTopsStorage;
import net.plazmix.lobby.playertop.database.type.PlayerTopsMysqlDatabase;
import net.plazmix.lobby.playertop.pagination.PlayerTopsPaginationChanger;
import net.plazmix.lobby.soulsbox.SoulsBox;
import net.plazmix.lobby.utils.game.DeathAngelMysqlDatabase;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.mojang.MojangSkin;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/


public final class SkyWarsLobbyPlugin extends JavaPlugin {

    @Getter
    private static GamePluginService service;
    static {
        try {
            service = (GamePluginService) GameUser.class.getMethod("getService").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        service.addGameDatabase(DeathAngelMysqlDatabase.INSTANCE);
        service.addGameDatabase(new BasedGameItemsMysqlDatabase("BSkywars"));
        service.addGameDatabase(new UpgradeGameItemsMysqlDatabase("USkywars"));

        service.registerItemsCategory(new KitCategory());
        service.registerItemsCategory(new PerkCategory());
        service.registerItemsCategory(new CageCategory());
        service.registerItemsCategory(new PreDeathCategory());
        service.registerItemsCategory(new KillEffectCategory());
        service.registerItemsCategory(new DanceCategory());

        CasesManager.INSTANCE.registerCaseType(new SkywarsBoxType());

        // World processes.
        this.addNpcs();

        // Add tops.
        this.addTops();

        // Add souls box.
        this.addSoulsBox();

        // Register plugin services.
        this.registerCommands();
        this.registerListeners();

        // Enable world ticker.
        this.enableWorldTicker();
    }

    private void addSoulsBox() {
        DeathAngelMysqlDatabase.INSTANCE.initTableConnection();

        Location location = LocationUtil.stringToLocation(getConfig().getString("locations.soulsBox"));
        SoulsBox soulsBox = SoulsBox.create(this, location);

        getServer().getPluginManager().registerEvents(new SoulsBoxListener(soulsBox), this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new StaticChatListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
    }

    private void registerCommands() {
        PlazmixApi.registerCommand(new SpawnCommand(this));
    }

    private void addNpcs() {

        // Daily rewards.
        ServerNPCManager.INSTANCE.register(new RewardsNPC(LocationUtil.stringToLocation(getConfig().getString("locations.npc.rewards"))));

        // Arena spectator.
        ServerNPCManager.INSTANCE.register(new SkyWarsSpectateNPC(LocationUtil.stringToLocation(getConfig().getString("locations.npc.spectate"))));

        // Select game mode.
        ServerNPCManager.INSTANCE.register(new SWLobbyNPC(new MojangSkin("SkyWars_SOLO", UUID.randomUUID().toString(), "ewogICJ0aW1lc3RhbXAiIDogMTYzOTE2NzAwMDg2NywKICAicHJvZmlsZUlkIiA6ICJkMGI4MjE1OThmMTE0NzI1ODBmNmNiZTliOGUxYmU3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJqYmFydHl5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RhZDNkNGMxNzViMTgxMDA1MDFhMDEzMGU0NzU5YTQzMDNhOGI4ODJjNjczYjNiOWZhZDEwZjhkZjFkYmMxNGIiCiAgICB9CiAgfQp9", "v73F89wrBWqUm+YGnXV8BxkMb0Z8ZBtFURrfUip5lbAWyndZ3b49xVSajYZ0m6N0caAOLNnkZePn+j5yP0X0y/9XzAfnNLxi6KyyoJ4aWLRjyNwL3B+hybZ527rF7QUZm0vQw3BD2zBTlsVomS0RW9f0SzKZ6Lx7R8/a+vUSSHpzwg1idZIJxEsx3TzEoyTjiW6N8QalGQ6UMoniU2Oy4gNdmhzcXuIXmI0vitCjxs7MTPEuRH9KxJXELpAHFgSEjb+9p5SvMG68/INYXs4LyIkgZL9/AKATeCqq2rlA9EirJG2ejQyMnepMXgI+rqiI0ucYxpNQVi/9iGbX2Y6TO3+K2hE/T+V97QwA5fbzFIy7VFr6alkZf+ToOdLwrD+IytFEXzjiGB9Ej0UG104fTewgAxEVRDJ4O1kQ7Rbsb+w7nPbU0+/BEYnS3SnGR5anBzt8WIh4buJzn6JTRxg4kqleKqo2zpuhvai0oLGcVPDSboai93Lvv4N4+FsiaXpBB1EUgBo9b6zaY6FQBXHrVre1XF/LIgoUGk++f9RdX3OJLzaWtDfnc7xu2whebnORYG63Tkh6Wi9apiZAR5CfO8CD6PZPpwqT+VPRNc+44k5TNY6V5vNO5n6KeFUeG/lKRpMKjvwIVBuV7WkAZUKX2h/D4N6H/yiejpnX7kTUCbU=", System.currentTimeMillis()), "§b§lSolo Insane", "sws", LocationUtil.stringToLocation(getConfig().getString("locations.npc.solo"))));
        ServerNPCManager.INSTANCE.register(new SWLobbyNPC(new MojangSkin("SkyWars_RANKED", UUID.randomUUID().toString(), "ewogICJ0aW1lc3RhbXAiIDogMTY0NTQ2MjgyNzAzMSwKICAicHJvZmlsZUlkIiA6ICJiOTE5M2NiMjkzMWI0M2FhYmM1OGQ2NjAwMTg3NGRjMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJiMmJsYWtlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUwYmQ3MThiYzlkYzlkYTI5NjNiZTBmYmUwZjhjNTNkZmRmMTUyYTg0MzQxZjkzOGY1MzU3MDhiMzQwZjg1MDQiCiAgICB9CiAgfQp9", "ahU3pd0vj6+gDR9Zem+SzvGWFdVGbGVjnOfdgs+hrthuxxZ+yd4NWOmusDikPZaAhuxUrIvQhxtIhn4NbK9tpLKneVP5OSPbmIruvKr8zmocZfLf6sG8MTA3qmijuumnDYdsEIRzjjKFjO+Fl1DyBCFefKLY/bpAIDyZ9ioSF1cezfa8uKw8J5UxHBou5j74DupPLPppRDalip0gG5SzSPjxoHVqT+FqxRR0cLbAwgOK8flJlTEU7QOZWPjnBZ584y69V5gpFgcmt5Q4Dw2t/3xzJz+ZjZNds4rkvdTtfe125cJwOyOFkti6t18nigKHKta1VeBoktzx+mwfwrSrZnj0siGwTNiTmVyC2ql7vzqTFGi1dnIC3XWvHMeYndeDaqNZIUOONlisIGuKrsNSs1a1674JFfHbTcg+xhf7qe5nL8eD0nKwytp3eWf4O0auvUahuSvQ2WpAfVkXk8dba0m1L5xsoYJTw1Kr3OFxhF6dVMKtyZxWNkgIGQLeK/dYPew1QMoJd1LMYOB2pDtzbi641Wgjmf6+LCeCnzv1hl7fxIIvRNgoookxWhxSuy8RJW4zJxM1DqV1JAY4FlrGLiMKNvtrYXlrv1QHO5hiiIW7M7nTQ3IMh4w9y03C+iUGIj835vwY5weVLlPSIT7lmkf9Jvslt8fb9YK/De4NMaM=", System.currentTimeMillis()), "§c§lRanked", "swr", LocationUtil.stringToLocation(getConfig().getString("locations.npc.ranked"))));
        ServerNPCManager.INSTANCE.register(new SWLobbyNPC(new MojangSkin("SkyWars_TEAM", UUID.randomUUID().toString(), "ewogICJ0aW1lc3RhbXAiIDogMTYzOTk0MDU1MzU2MywKICAicHJvZmlsZUlkIiA6ICI5MGQ1NDY0OGEzNWE0YmExYTI2Yjg1YTg4NTU4OGJlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFdW4wbWlhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlOGVhZWY5OWZkZDg1YWQzYjk2Y2FiNWI2ZmQ4MmQzNjc1NzkwMjgyYTc5OWVjYjQ4MjEzMTI4Y2U3YTI1NjMiCiAgICB9CiAgfQp9", "HRd3WfF0nfQ8m0pxEABRb7zNB3ixDaj7AQQO26nLSjirj2uD01100MX1yAkij0mpZqZmhpRWeTkcE0hgEQA8M0Qo4FaBkSMd83uriawMCPOpHybwebYBS6G6GQyDwfhn2CYt/jIBGWmK/TgTPx8GLLWZpg7LYhYV+59BERjlJCYk2zu+f/JGfNS6yBLcMHoAEVuvKPy6vmTLFsWY7EUfX7YzcqjCaVTt3xLsfiyzxcABv1KHgFAAVyF87ej0xmF0MRBZnS2C+HUIUJdnx+RwGTN1G4oXH99/oW0XVu6MzQSqvFzPIM24K0VqurSJPzk75FRXhxgZaZ1O+xlQX8a9YCOJd2dAtK6W48CIgkQtBC8w/NZyH3xFLvSHTVwPoeV76WqWo2XlIZ0Z5oKAuYw/VxLwvfdngBB+HFKJtGzGmDsXxu3Hg6t4jgvQfyQJ0EvSYGlBy6wILanllDpD4HkrANpOehFTWj0E5VTxeFuUToHmsZqXlR4It3dTBKUMjArtB050pZelbrZbyVsba3tbz/q7bsZef64G1tKiEVH+sMKPCH0B1RpBbVcmdUijOEzDHS+6qmwxR4JBJzqermFMK8fOw9o0dOG5azmT/uuhEkeB9onR7FO0vMkUUG1S3JkEKbhLLiKf/sMDAVznmyZ8/kHVaYqQJSFeGti3HrroZm8=", System.currentTimeMillis()), "§a§lTeam Insane", "swd", LocationUtil.stringToLocation(getConfig().getString("locations.npc.team"))));
        ServerNPCManager.INSTANCE.register(new SWLobbyNPC(new MojangSkin("SkyWars_CRAZY", UUID.randomUUID().toString(), "ewogICJ0aW1lc3RhbXAiIDogMTYzOTE3MTAyOTg3MCwKICAicHJvZmlsZUlkIiA6ICJmODJmNTQ1MDIzZDA0MTFkYmVlYzU4YWI4Y2JlMTNjNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXNwb25kZW50cyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jOGIyMTc3NzMzZDQ3NDlmYTY2NjA1MDBhMTQ1M2I0M2VkNTExNmYyMTRmN2RmZDBlMWY2YmYwYTJkNDRkNWE2IgogICAgfQogIH0KfQ==", "yKb9Y81oqwUq6rrmNcftGvS/1GK0qbGteOGxPCEEKu4u1oxJGXenZ6BqJTLdRNiUVR7+Ue8juHMLTGSZDesZHX+2Q6nkwZhmG3u2fJXNj2wXHjL9RYKAMfLKjPubKkF/MEEad/kkjFpTBt9wtFEAG4XHv9tGW6JsAKawZNavkOBiMFAYDKZctyi4uQBIqOIzGUzDKzQaaKO33te7Q9KImL5GUHxeBmzfyQBYm2cGp+a+wLW34DcVixGaacy7gWreCdpokrSqaGCnYNoRm+59clZR7HtFltbh5furMw3PewIYzMS1Q03TiPvT6lY/mpnlsd+lwYNnDAUhc0C3hc3Qa5CltRHnpHgg6NmQCHwOclbhzrPvNeBL1dp2FiwcFALA9kLVOOpGts2EHQlmf6ewAu4GQEa8PIfhm+70hwJFyM5kx6qNBeh8BG2+O5tqkEEX3PlGhiYaihdvkjTQQdzurRnooCAjH/vMuA41hOodBOVaJYaN9hiqWMXKJv1+c8TpeUsb5xZ5iuxKG2ilo48a7nFanvyooxD5TmhiMWuVRfEA0bQjG39RvRDqeShHnjkNU5ZdzPBU0u811nqa7du75FqJY08PeiFcrKo6Gu6v5kiMTi6JhzPTXsA+CusPTWzqVi9JKuwoQ9HtY1Ehswe4G0KDP+8tuO8q3zTV+8kL2TU=", System.currentTimeMillis()), "§a§lCrazy", "swс", LocationUtil.stringToLocation(getConfig().getString("locations.npc.crazy"))));
    }

    private void enableWorldTicker() {
        for (World world : getServer().getWorlds()) {
            world.setPVP(false);
            world.setDifficulty(Difficulty.NORMAL);

            new BukkitRunnable() {

                @Override
                public void run() {

                    world.setTime(18000);

                    // Set world settings.
                    world.setThundering(false);
                    world.setStorm(false);

                    world.setGameRuleValue("randomTickSpeed", "0");

                    world.setWeatherDuration(0);
                }

            }.runTaskTimer(this, 0, 1);
        }
    }

    private void addTops() {
        Location location = LocationUtil.stringToLocation(getConfig().getString("locations.tops"));
        PlayerTopsPaginationChanger paginationChanger = PlayerTopsPaginationChanger.create();

       // Solo
       paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
               .setDatabaseManager(new PlayerTopsMysqlDatabase("SkywarsSolo", "Wins"))

               .setLocation(location)
               .setSkullParticle(Particle.TOTEM)

               .setLimit(10)
               .setUpdater(60)

               .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBiZjM0YTcxZTc3MTViNmJhNTJkNWRkMWJhZTVjYjg1Zjc3M2RjOWIwZDQ1N2I0YmZjNWY5ZGQzY2M3Yzk0In19fQ==")

               .setStatsName("SkyWars Solo")
               .setDescription("Топ 10 игроков, набравшие наибольшее",
                       "количество побед по данному режиму")

               .setValueSuffix("побед"));

       // Team
       paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
               .setDatabaseManager(new PlayerTopsMysqlDatabase("SkywarsTeam", "Wins"))

               .setLocation(location)
               .setSkullParticle(Particle.TOTEM)

               .setLimit(10)
               .setUpdater(60)

               .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY0YmRjNmY2MDA2NTY1MTFiZWY1OTZjMWExNmFhYjFkM2Y1ZGJhYWU4YmVlMTlkNWMwNGRlMGRiMjFjZTkyYyJ9fX0=")

               .setStatsName("SkyWars Team")
               .setDescription("Топ 10 игроков, набравшие наибольшее",
                       "количество побед по данному режиму")

               .setValueSuffix("побед"));

        // Spawn all tops.
        paginationChanger.spawn();
    }

}
