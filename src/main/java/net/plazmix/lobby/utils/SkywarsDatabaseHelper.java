package net.plazmix.lobby.utils;

import com.google.common.collect.ArrayListMultimap;
import lombok.experimental.UtilityClass;
import net.plazmix.game.GamePluginService;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.mysql.type.BasedGameItemsMysqlDatabase;
import net.plazmix.game.mysql.type.UpgradeGameItemsMysqlDatabase;
import net.plazmix.game.user.GameUser;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class SkywarsDatabaseHelper {

    public void loadDatabasesData(GamePluginService gamePluginService, GameUser gameUser) {
        gamePluginService.getGameDatabase(BasedGameItemsMysqlDatabase.class).loadAll(true, gameUser, (resultSet) -> {
            ArrayListMultimap<Integer, GameItem> gameItemsMap = ArrayListMultimap.create();

            while(resultSet.next()) {
                boolean isSelected = resultSet.getBoolean("State");

                int categoryId = resultSet.getInt("CategoryID");
                int itemId = resultSet.getInt("ItemID");

                GameItem gameItem = gamePluginService.getItemsCategory(categoryId).getItem(itemId);
                if (gameItem != null) {

                    if (isSelected) {
                        gameUser.getCache().set("SItems" + categoryId, Collections.singletonList(gameItem));
                    }

                    gameItemsMap.put(categoryId, gameItem);
                }
            }

            gameItemsMap.keySet().forEach((categoryIdx) -> gameUser.getCache().set("BItems" + categoryIdx, gameItemsMap.get(categoryIdx)));
        });

        gamePluginService.getGameDatabase(UpgradeGameItemsMysqlDatabase.class).loadAll(true, gameUser, (resultSet) -> {
            ArrayListMultimap<Integer, GameItem> gameItemsMap = ArrayListMultimap.create();

            while(resultSet.next()) {
                boolean isSelected = resultSet.getBoolean("State");

                int upgradeLevel = resultSet.getInt("Upgrade");
                int categoryId = resultSet.getInt("CategoryID");
                int itemId = resultSet.getInt("ItemID");

                GameItem gameItem = gamePluginService.getItemsCategory(categoryId).getItem(itemId);
                if (gameItem != null) {

                    if (isSelected) {

                        List<GameItem> selectedItems = gameUser.getSelectedItems(categoryId);
                        selectedItems.add(gameItem);

                        gameUser.getCache().set("SItems" + categoryId, selectedItems);
                    }

                    gameUser.getCache().set("UItemsUpgrade" + categoryId + itemId, upgradeLevel);
                    gameItemsMap.put(categoryId, gameItem);
                }
            }

            gameItemsMap.keySet().forEach((categoryIdx) -> gameUser.getCache().set("BItems" + categoryIdx, gameItemsMap.get(categoryIdx)));
        });
    }
}
