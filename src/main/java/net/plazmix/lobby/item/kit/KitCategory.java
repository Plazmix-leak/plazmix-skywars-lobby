package net.plazmix.lobby.item.kit;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.lobby.utils.game.GameConstants;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class KitCategory extends GameItemsCategory {

    public KitCategory() {
        super(GameConstants.KITS_ID, 14, "Наборы", new ItemStack(Material.LEATHER_CHESTPLATE));

        for (KitItem kitItem : KitItem.values()) {
            addItem(kitItem.createGameItem());
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
