package net.plazmix.lobby.soulsbox;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.plazmix.utility.RotatingHead;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SoulsBox {

    public static final String HEAD_TEXTURE            = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ2ODRlM2U3ODkwY2FmN2QxMzc2MmVhMTllYjE0YzU5NDBiODhmZDdmMDc3ZDgxZTZlZmZiNGY2ZGYxNmMyNiJ9fX0=";
    public static final String CLOSE_MESSAGE_FORMAT    = "§b§lХранилище Душ §8:: %s §fвыпало %s";
    public static final String ALREADY_OPENING_MESSAGE = "§cХранилище душ уже открывается другим игроком!";

    public static SoulsBox create(Plugin plugin, Location location) {
        SoulsBox soulsBox = new SoulsBox(plugin, location);
        soulsBox.initialize();

        return soulsBox;
    }

    Plugin plugin;
    Location location;

    @NonFinal
    SoulsBoxOpeningTask openingTask;

    @NonFinal
    BukkitRunnable waitingAnimationTask;

    @NonFinal
    RotatingHead rotatingHead;


    private void initialize() {
        location.getBlock().setType(Material.BARRIER);
        location.clone().subtract(0, 1, 0).getBlock().setType(Material.ENDER_STONE);

        rotatingHead = new RotatingHead(plugin, location.clone().subtract(0, .5, 0), HEAD_TEXTURE);
        rotatingHead.setRotateDirection(RotatingHead.RotateDirection.TO_RIGHT);
        rotatingHead.setRotateSpeed(2.5f);

        rotatingHead.addTextLine("§e§lХранилище Душ");
        rotatingHead.addTextLine("§fНажмите, чтобы открыть меню");

        rotatingHead.register();

        this.resetWaitAnimation();
    }

    private void resetWaitAnimation() {
        if (waitingAnimationTask != null && !waitingAnimationTask.isCancelled()) {
            waitingAnimationTask.cancel();
        }

        waitingAnimationTask = new BukkitRunnable() {

            private final double radian = Math.pow(2, 4);
            private final double radius = 2.35d;

            private double t = 0;

            @Override
            public void run() {
                t += Math.PI / radian;

                if (t >= Math.PI * 2) {
                    t = 0;
                }

                double x = radius * Math.cos(t);
                double z = radius * Math.sin(t);

                location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.clone().add(z, x, x), 1, 0, 0, 0, 0);
                location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.clone().add(z, z, x), 1, 0, 0, 0, 0);
                location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.clone().add(x, z, z), 1, 0, 0, 0, 0);
                location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.clone().add(x, x, z), 1, 0, 0, 0, 0);
            }
        };

        waitingAnimationTask.runTaskTimer(plugin, 0, 1);
    }

    public boolean isAlreadyOpening() {
        return openingTask != null;
    }

    public CompletableFuture<Boolean> startOpening(Player player) {
        if (this.isAlreadyOpening()) {
            return CompletableFuture.completedFuture(false);
        }

        if (waitingAnimationTask != null && !waitingAnimationTask.isCancelled()) {
            waitingAnimationTask.cancel();
            waitingAnimationTask = null;
        }

        rotatingHead.getHolographic().remove();

        openingTask = new SoulsBoxOpeningTask(this);
        openingTask.start(player);

        return CompletableFuture.completedFuture(true);
    }

    public void onClose(String playerDisplay, String rewardTitle) {
        if (!this.isAlreadyOpening()) {
            return;
        }

        rotatingHead.getFakeArmorStand().spawn();
        rotatingHead.getHolographic().spawn();

        openingTask.cancel();
        openingTask = null;

        String message = String.format(CLOSE_MESSAGE_FORMAT, playerDisplay, rewardTitle);

        for (Player other : Bukkit.getOnlinePlayers()) {
            other.sendMessage(message);
        }

        this.resetWaitAnimation();
    }

}
