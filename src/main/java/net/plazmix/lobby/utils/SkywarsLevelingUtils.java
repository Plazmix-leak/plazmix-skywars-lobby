package net.plazmix.lobby.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.player.PlazmixUser;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class SkywarsLevelingUtils {

    public static final String GET_EXPERIENCE = "SELECT `Experience` WHERE `Skywars%s` WHERE `Id`=?";

    private final Cache<Integer, Integer> skywarsExperienceCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    private int getTotalExperience(SkywarsMode mode, int playerID) {
        skywarsExperienceCache.cleanUp();

        if (skywarsExperienceCache.asMap().containsKey(playerID)) {
            return skywarsExperienceCache.asMap().get(playerID);
        }

        int result = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, String.format(GET_EXPERIENCE, mode.getTitle()),
                resultSet -> {

                    if (resultSet.next()) {
                        return resultSet.getInt("Experience");
                    }

                    return 0;
                }, playerID);

        skywarsExperienceCache.put(playerID, result);
        return result;
    }

    public int getCurrentLevel(SkywarsMode skywarsMode, String playerName) {
        int playerID = CoreConnector.getNetworkInstance().getPlayerId(playerName);
        return LevelingUtil.getLevel(getTotalExperience(skywarsMode, playerID));
    }

    public int getCurrentExperience(SkywarsMode skywarsMode, String playerName) {
        int playerID = PlazmixUser.of(playerName).getPlayerId();

        int currentLevel = getCurrentLevel(skywarsMode, playerName);

        int playerExperience = getTotalExperience(skywarsMode, playerID);
        int totalExperienceToLevel = (int) LevelingUtil.getTotalExpToLevel(currentLevel);

        return currentLevel > 1 ? playerExperience - totalExperienceToLevel : playerExperience;
    }

    public int getMaxExperience(SkywarsMode skywarsMode, String playerName) {
        return (int)LevelingUtil.getExpFromLevelToNext(getCurrentLevel(skywarsMode, playerName));
    }

}