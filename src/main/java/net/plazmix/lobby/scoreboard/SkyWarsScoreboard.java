package net.plazmix.lobby.scoreboard;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.PlayerGamesData;
import net.plazmix.lobby.utils.game.SkywarsSoulsService;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.scoreboard.ScoreboardDisplayAnimation;
import net.plazmix.scoreboard.animation.ScoreboardDisplayCustomAnimation;
import net.plazmix.scoreboard.animation.ScoreboardDisplayFlickAnimation;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PercentUtil;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.player.LocalizationPlayer;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SkyWarsScoreboard {

    public SkyWarsScoreboard(@NonNull Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        ScoreboardDisplayFlickAnimation displayFlickAnimation = new ScoreboardDisplayFlickAnimation();

        displayFlickAnimation.addColor(ChatColor.LIGHT_PURPLE);
        displayFlickAnimation.addColor(ChatColor.DARK_PURPLE);
        displayFlickAnimation.addColor(ChatColor.WHITE);
        displayFlickAnimation.addColor(ChatColor.DARK_PURPLE);

        displayFlickAnimation.addTextToAnimation(plazmixUser.localization().getMessageText("SKYWARS_BOARD_TITLE"));

        scoreboardBuilder.scoreboardDisplay(displayFlickAnimation);
        scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {

            baseScoreboard.setScoreboardDisplay(displayFlickAnimation);

            List<String> scoreboardLineList = getScoreboardLines(plazmixUser);

            for (String scoreboardLine : scoreboardLineList) {
                baseScoreboard.setScoreboardLine(scoreboardLineList.size() - scoreboardLineList.indexOf(scoreboardLine), player, scoreboardLine);
            }

        }, 20);

        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }

    private List<String> getScoreboardLines(@NonNull PlazmixUser plazmixUser) {
        List<String> scoreboardLineList = new LinkedList<>();

        int solo_kills = plazmixUser.getDatabaseValue("SkywarsSolo", "Kills");
        int team_kills = plazmixUser.getDatabaseValue("SkywarsTeam", "Kills");
        int count_kills = solo_kills + team_kills;

        int solo_wins = plazmixUser.getDatabaseValue("SkywarsSolo", "Wins");
        int team_wins = plazmixUser.getDatabaseValue("SkywarsTeam", "Wins");
        int count_wins = solo_wins + team_wins;

        int solo_exp = plazmixUser.getDatabaseValue("SkywarsSolo", "Experience");
        int team_exp = plazmixUser.getDatabaseValue("SkywarsTeam", "Experience");
        int count_exp = solo_exp + team_exp;

        int level = LevelingUtil.getLevel(count_exp);

        for (String scoreboardLine : plazmixUser.localization().getMessageList("SKYWARS_BOARD_LINES")) {
            scoreboardLine = scoreboardLine
                    .replace("%level%", NumberUtil.spaced(level))
                    .replace("%exp_percent%", String.format("%d%%", (int)PercentUtil.getPercent(count_exp, LevelingUtil.getExpFromLevelToNext(level))))
                    .replace("%souls%", "" + Math.min(100, SkywarsSoulsService.INSTANCE.getSouls(plazmixUser.getName())))

                    .replace("%kills%", NumberUtil.spaced(count_kills))
                    .replace("%wins%", NumberUtil.spaced(count_wins))

                    .replace("%plazma%", NumberUtil.spaced(plazmixUser.getGolds()))
                    .replace("%money%", NumberUtil.spaced(plazmixUser.getCoins()));

            scoreboardLineList.add(ChatColor.translateAlternateColorCodes('&', scoreboardLine));
        }

        return scoreboardLineList;
    }
}