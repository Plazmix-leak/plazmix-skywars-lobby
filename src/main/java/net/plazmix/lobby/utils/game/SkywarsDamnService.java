package net.plazmix.lobby.utils.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.plazmix.game.GameCache;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.user.GameUser;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SkywarsDamnService {

    public static final SkywarsDamnService INSTANCE = new SkywarsDamnService();

    private GameMysqlDatabase getDatabase() {
        return DeathAngelMysqlDatabase.INSTANCE;
    }

    private GameCache getPlayerCache(String playerName, String onLoadCacheID) {
        GamePlugin gamePlugin = GamePlugin.getInstance();

        GameUser gameUser = GameUser.from(playerName);
        GameCache gameCache = gameUser.getCache();

        if (!gameCache.contains(onLoadCacheID)) {
            this.getDatabase().onJoinLoad(gamePlugin, gameUser);
        }

        return gameCache;
    }

    public int getDamnPercent(String playerName) {
        String id = ("DamnPercent");
        return this.getPlayerCache(playerName, id).getInt(id);
    }

    public void addPercent(String playerName, int value) {
        String id = ("DamnPercent");

        GameCache gameCache = this.getPlayerCache(playerName, id);
        gameCache.add(id, value);

        this.getDatabase().insert(false, GameUser.from(playerName));
    }

    public void removePercent(String playerName, int value) {
        this.addPercent(playerName, -value);
    }

    public int summaryPercent(Collection<GameUser> ingameUsers) {
        return ingameUsers.stream().map(gameUser -> this.getDamnPercent(gameUser.getName())).mapToInt(value -> value).sum();
    }

}
