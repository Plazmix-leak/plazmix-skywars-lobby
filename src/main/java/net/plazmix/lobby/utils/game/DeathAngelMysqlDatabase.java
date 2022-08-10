package net.plazmix.lobby.utils.game;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;

public class DeathAngelMysqlDatabase extends GameMysqlDatabase {

    public static final DeathAngelMysqlDatabase INSTANCE = new DeathAngelMysqlDatabase();

    private DeathAngelMysqlDatabase() {
        super("SkywarsDeathAngel", true);
    }

    @Override
    public void initialize() {
        addColumn("Souls", RemoteDatabaseRowType.INT, user -> user.getCache().getInt("Souls"));
        addColumn("DamnPercent", RemoteDatabaseRowType.INT, user -> user.getCache().getInt("DamnPercent"));
    }

    @Override
    public void onJoinLoad(GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(true, gameUser, gameUser.getCache()::set);

        if (gameUser.getCache().getInt("DamnPercent") <= 0) {
            gameUser.getCache().set("DamnPercent", 1);
        }
    }
}
