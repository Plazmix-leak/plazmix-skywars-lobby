package net.plazmix.lobby.cases;

import net.plazmix.cases.type.BaseCaseBoxType;
import net.plazmix.cases.type.BaseCaseItem;
import net.plazmix.cosmetics.game.DanceManager;
import net.plazmix.cosmetics.game.DeathSoundManager;
import net.plazmix.cosmetics.game.KillEffectManager;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.game.user.GameUser;
import net.plazmix.lobby.SkyWarsLobbyPlugin;
import net.plazmix.lobby.utils.SkywarsDatabaseHelper;
import net.plazmix.lobby.utils.game.GameConstants;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class SkywarsBoxType extends BaseCaseBoxType {

    public SkywarsBoxType() {
        super(2, 15,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhiZThhYmQ2NmQwOWE1OGNlMTJkMzc3NTQ0ZDcyNmQyNWNhZDdlOTc5ZThjMjQ4MTg2NmJlOTRkM2IzMmYifX19",
                "Косметика Skywars",
                "В данной игровой коробке можно",
                "получить любой игровой предмет на SkyWars");

        DanceManager[] dances = DanceManager.values();
        KillEffectManager[] killEffects = KillEffectManager.values();
        DeathSoundManager[] deathSounds = DeathSoundManager.values();

        for (DanceManager danceManager : dances) {
            super.addItem(this.createCaseItem(GameConstants.DANCES_ID, danceManager.ordinal(), Material.NOTE_BLOCK, danceManager.getName()));
        }

        for (KillEffectManager killEffect : killEffects) {
            super.addItem(this.createCaseItem(GameConstants.KILL_EFFECTS_ID, killEffect.ordinal(), Material.SKULL_ITEM, killEffect.getName()));
        }

        for (DeathSoundManager deathSound : deathSounds) {
            super.addItem(this.createCaseItem(GameConstants.PRE_DEATH_ID, deathSound.ordinal(), Material.RECORD_4, deathSound.getName()));
        }
    }

    private BaseCaseItem createCaseItem(int categoryID, int itemID, Material iconType, String itemName) {
        return new BaseCaseItem(itemName, new ItemStack(iconType),
                player -> {
                    GameItemsCategory category = SkyWarsLobbyPlugin.getService().getItemsCategory(categoryID);
                    GameUser gameUser = GameUser.from(player.getName());

                    SkywarsDatabaseHelper.loadDatabasesData(
                            SkyWarsLobbyPlugin.getService(), gameUser
                    );

                    GameItem gameItem = category.getItem(itemID);
                    if (gameUser.hasItem(gameItem)) {

                        int price = (int) (gameItem.getPrice().getCount() * 0.7);
                        PlazmixUser plazmixUser = PlazmixUser.of(player);

                        switch (gameItem.getPrice().getCurrency()) {
                            case COINS: {
                                plazmixUser.addCoins(price);
                                break;
                            }

                            case GOLDS: {
                                plazmixUser.addGolds(price);
                                break;
                            }
                        }

                        player.sendMessage("§cДанный предмет уже есть в Вашей коллекции, поэтому");
                        player.sendMessage("§cВам было зачислено 70% от его стоимости (" + NumberUtil.spaced(price) + ")");
                    }
                    else {
                        gameUser.addItem(gameItem);
                    }
                }
        );
    }

}
