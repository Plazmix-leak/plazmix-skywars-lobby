package net.plazmix.lobby.item.cage;

import lombok.NonNull;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemPrice;
import org.bukkit.inventory.ItemStack;

public final class CageItem extends GameItem {

    public CageItem(int id, @NonNull GameItemPrice price, @NonNull String itemName, @NonNull ItemStack iconItem) {
        super(id, price, itemName, iconItem);
    }

}
