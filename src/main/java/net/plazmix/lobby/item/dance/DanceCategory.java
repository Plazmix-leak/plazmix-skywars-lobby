package net.plazmix.lobby.item.dance;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.cosmetics.game.DanceManager;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.lobby.utils.game.GameConstants;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class DanceCategory
        extends GameItemsCategory {

    public DanceCategory() {
        super(GameConstants.DANCES_ID, 34, "Победные танцы", new ItemStack(Material.NOTE_BLOCK));

        for (DanceManager danceManager : DanceManager.values()) {
            addItem(new DanceItem(danceManager.ordinal(), danceManager));
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
