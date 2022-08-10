package net.plazmix.lobby.item.killeffect;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.cosmetics.game.KillEffectManager;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.lobby.utils.game.GameConstants;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class KillEffectCategory
        extends GameItemsCategory {

    public KillEffectCategory() {
        super(GameConstants.KILL_EFFECTS_ID, 32, "Эффекты при убийстве", new ItemStack(Material.SKULL_ITEM));

        for (KillEffectManager killEffect : KillEffectManager.values()) {
            addItem(new KillEffectItem(killEffect.ordinal(), killEffect));
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
