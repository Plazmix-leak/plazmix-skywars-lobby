package net.plazmix.lobby.soulsbox;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.game.user.GameUser;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.lobby.SkyWarsLobbyPlugin;
import net.plazmix.lobby.utils.SkywarsDatabaseHelper;
import net.plazmix.utility.FireworkExplosion;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.RotatingHead;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class SoulsBoxOpeningTask extends BukkitRunnable {

    private final SoulsBox soulsBox;
    private PlazmixUser plazmixUser;

    private long counter;

    private Location beginLocation;
    private float beginRotateSpeed;

    private boolean hasReward;
    private ProtocolHolographic rewardHolographic;


    private float upSoundPitch;
    private String rewardTitle;

    @Override
    public void run() {
        this.counter++;

        if (counter % 2 != 0) {
            return;
        }

        RotatingHead rotatingHead = soulsBox.getRotatingHead();
        Location location = rotatingHead.getFakeArmorStand().getLocation().clone().add(0, 0.1, 0);

        // 3 sec later
        if (this.counter >= (20L * 3)) {

            if (!hasReward) {
                Location explosionLocation = location.clone().add(0, 1.85, 0);
                rotatingHead.getFakeArmorStand().remove();

                FireworkExplosion.spawn(explosionLocation, FireworkEffect.builder()
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .withTrail()
                                .withFlicker()
                                .withColor(Color.AQUA)
                                .withColor(Color.BLUE)
                                .withColor(Color.BLACK)
                                .withColor(Color.PURPLE)
                                .withColor(Color.YELLOW)
                                .build(),

                        Bukkit.getOnlinePlayers().toArray(new Player[0]));

                location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, explosionLocation, 3);
                location.getWorld().playSound(explosionLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                rotatingHead.setRotateSpeed(0);

                rewardTitle = this.initReward(location);
            }

            // 3 sec later more
            if (this.counter >= (20L * 6)) {

                rotatingHead.getFakeArmorStand().teleport(beginLocation);
                rotatingHead.setRotateSpeed(beginRotateSpeed);

                location.getWorld().playSound(beginLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);

                soulsBox.onClose(plazmixUser.getDisplayName(), rewardTitle);

                this.removeRewardDisplay();
            }
        }
        else {
            float rotatingSpeed = rotatingHead.getRotatingSpeed() + 0.15F;

            rotatingHead.getFakeArmorStand().teleport(location);
            rotatingHead.setRotateSpeed(rotatingSpeed);

            Location particlesLocation = location.clone().add(0, 1.85, 0);

            location.getWorld().spawnParticle(Particle.FLAME, particlesLocation, 5, 0.05F, 0.05F, 0.05F);
            location.getWorld().spawnParticle(Particle.SPELL_WITCH, particlesLocation, 5, 0.01F, 0.01F, 0.01F);

            location.getWorld().playSound(particlesLocation, Sound.BLOCK_NOTE_PLING, 1, upSoundPitch += 0.03f);
        }
    }

    public final void start(Player player) {
        this.start(PlazmixUser.of(player));
    }

    public final void start(PlazmixUser plazmixUser) {
        this.plazmixUser = plazmixUser;

        this.beginLocation = soulsBox.getRotatingHead().getFakeArmorStand().getLocation();
        this.beginRotateSpeed = soulsBox.getRotatingHead().getRotatingSpeed();

        super.runTaskTimer(SkyWarsLobbyPlugin.getPlugin(SkyWarsLobbyPlugin.class), 0, 1);
    }

    private String initReward(Location location) {
        this.hasReward = true;

        if (rewardHolographic == null) {
            rewardHolographic = new SimpleHolographic(location);
        }

        Map.Entry<String, ItemStack> rewardEntry = this.generateRewardItem();

        rewardHolographic.addOriginalHolographicLine(rewardEntry.getKey());
        rewardHolographic.addItemHolographicLine(rewardEntry.getValue());
        rewardHolographic.spawn();

        return this.rewardTitle = rewardEntry.getKey();
    }

    private void removeRewardDisplay() {
        this.hasReward = false;

        if (rewardHolographic != null) {
            rewardHolographic.remove();
            rewardHolographic = null;
        }
    }

    private Map.Entry<String, ItemStack> generateRewardItem() {
        GamePluginService gamePluginService = SkyWarsLobbyPlugin.getService();
        Set<GameItem> gameItemSet = new HashSet<>();

        for (GameItemsCategory category : gamePluginService.getGameItemCategoriesMap().valueCollection()) {
            gameItemSet.addAll(category.getMappedItems());
        }

        GameUser gameUser = GameUser.from(plazmixUser.getPlayerId());
        GameItem reward = gameItemSet.stream().skip((long) (Math.random() * gameItemSet.size())).findFirst().orElse(null);

        if (reward != null) {

            SkywarsDatabaseHelper.loadDatabasesData(gamePluginService, gameUser);
            if (gameUser.hasItem(reward)) {

                int price = (int) (reward.getPrice().getCount() * 0.7);

                switch (reward.getPrice().getCurrency()) {
                    case COINS: {
                        plazmixUser.addCoins(price);
                        break;
                    }

                    case GOLDS: {
                        plazmixUser.addGolds(price);
                        break;
                    }
                }

                Player player = plazmixUser.handle();

                if (player != null) {
                    player.sendMessage("§cДанный предмет уже есть в Вашей коллекции, поэтому");
                    player.sendMessage("§cВам было зачислено 70% от его стоимости (" + NumberUtil.spaced(price) + ")");
                }
            }
            else {
                gameUser.addItem(reward);
            }

            return Maps.immutableEntry(reward.getItemName(), reward.getIconItem());
        }
        else {
            return Maps.immutableEntry("§cНичего", new ItemStack(Material.BARRIER));
        }
    }

}
