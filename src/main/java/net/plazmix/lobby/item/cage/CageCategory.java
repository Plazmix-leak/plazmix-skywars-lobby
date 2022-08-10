package net.plazmix.lobby.item.cage;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemPrice;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.lobby.utils.game.GameConstants;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class CageCategory extends GameItemsCategory {

    public CageCategory() {
        super(GameConstants.CAGES_ID, 16, "Клетки", new ItemStack(Material.BUCKET));

        addItem(new CageItem(0, GameItemPrice.createDefault(0), "Стеклянная клетка", new ItemStack(Material.GLASS)));
        addItem(new CageItem(1, GameItemPrice.createDefault(20_000), "Невидимая клетка", new ItemStack(Material.BARRIER)));
        addItem(new CageItem(2, GameItemPrice.createDefault(15_000), "Ледяная клетка", new ItemStack(Material.PACKED_ICE)));
        addItem(new CageItem(3, GameItemPrice.createDefault(15_000), "Слаймовая клетка", new ItemStack(Material.SLIME_BLOCK)));
        addItem(new CageItem(4, GameItemPrice.createDefault(15_000), "Белая клетка", new ItemStack(Material.STAINED_GLASS)));
        addItem(new CageItem(5, GameItemPrice.createDefault(15_000), "Железная клетка", new ItemStack(Material.IRON_BARDING)));
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
