package net.plazmix.lobby.soulsbox.gui;

import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.lobby.soulsbox.SoulsBox;
import net.plazmix.lobby.utils.game.SkywarsDamnService;
import net.plazmix.lobby.utils.game.SkywarsSoulsService;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoulsBoxMenu extends BaseSimpleInventory {
    private final SoulsBox soulsBox;

    public SoulsBoxMenu(SoulsBox soulsBox) {
        super("Хранилище душ", 3);

        this.soulsBox = soulsBox;
    }

    @Override
    public void drawInventory(Player player) {
        this.setClickItem(13, ItemUtil.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)
                        .setTextureValue(SoulsBox.HEAD_TEXTURE)

                        .setName("§aХранилище душ")
                        .addLore("")
                        .addLore("§8Может выпасть:")
                        .addLore(" §7- Игровые предметы Skywars")
                        .addLore("")
                        .addLore("§7Стоимость: §b10 душ")
                        .addLore("")
                        .addLore("§aНажмите, чтобы открыть!")

                        .build(),

                (click, inventoryClickEvent) -> {
                    click.closeInventory();

                    if (SkywarsSoulsService.INSTANCE.getSouls(click.getName()) < 10) {
                        click.sendMessage("§cОшибка, у Вас недостаточно душ для открытия коробки душ!");
                        return;
                    }

                    soulsBox.startOpening(click).thenAccept(isSuccess -> {

                        if (!isSuccess) {
                            click.sendMessage(SoulsBox.ALREADY_OPENING_MESSAGE);

                        } else {

                            for (Player other : Bukkit.getOnlinePlayers()) {
                                other.sendMessage("§b§lХранилище душ §8:: " + PlayerUtil.getDisplayName(click) + " §fначал открывать коробку");
                            }

                            SkywarsSoulsService.INSTANCE.removeSouls(click.getName(), 10);
                        }
                    });
                });

        int damnPercent = SkywarsDamnService.INSTANCE.getDamnPercent(player.getName());
        this.setClickItem(15, ItemUtil.newBuilder(Material.IRON_SWORD)
                        .setName("§cПроклятая игра")
                        .addLore("")
                        .addLore("§7Данное улучшение помогает повысить")
                        .addLore("§7Ваш шанс в проклятой игре")
                        .addLore("")
                        .addLore("§7Сейчас он составляет: §c" + damnPercent + "%")
                        .addLore("§7Стоимость улучшения: " + NumberUtil.spaced(damnPercent * 50_000))
                        .addLore("")
                        .addLore("§aНажмите, чтобы улучшить!")
                        .build(),

                (click, inventoryClickEvent) -> {
                    PlazmixUser plazmixUser = PlazmixUser.of(click);

                    if (!plazmixUser.hasCoins(damnPercent * 50_000)) {
                        click.sendMessage("§cОшибка, у Вас недостаточно душ для улучшения шанса проклтой игры!");
                        return;
                    }

                    plazmixUser.removeCoins(damnPercent * 50_000);

                    SkywarsDamnService.INSTANCE.addPercent(click.getName(), 1);

                    click.closeInventory();
                    click.sendMessage("§aВы успешно повысили шанс Проклятой игры до " + (damnPercent + 1));

                    soulsBox.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, soulsBox.getLocation(), 25);
                    soulsBox.getLocation().getWorld().playSound(soulsBox.getLocation(), Sound.ENTITY_ENDERDRAGON_AMBIENT, 1, 2);
                });
    }

}
