package net.plazmix.lobby.item.predeath;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.cosmetics.game.DeathSoundManager;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.lobby.utils.game.GameConstants;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class PreDeathCategory
        extends GameItemsCategory {

    public PreDeathCategory() {
        super(GameConstants.PRE_DEATH_ID, 30, "Предсмертные крики", new ItemStack(Material.NOTE_BLOCK));

        for (DeathSoundManager deathSound : DeathSoundManager.values()) {
            addItem(new PreDeathItem(deathSound.ordinal(), deathSound));
        }
    }

    @SneakyThrows
    @Override
    public void addItem(@NonNull GameItem gameItem) {
        super.gameItemsMap.put(gameItem.getId(), gameItem);

        Method setCategoryMethod = GameItem.class.getDeclaredMethod("setItemCategory", GameItemsCategory.class);

        setCategoryMethod.setAccessible(true);
        setCategoryMethod.invoke(gameItem, this);
    }

}
