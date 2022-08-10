package net.plazmix.lobby.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum SkywarsMode {

    SOLO(ChatColor.GREEN, "Solo"),
    TEAM(ChatColor.AQUA, "Team"),
    CRAZY(ChatColor.RED, "Crazy"),
    RANKED(ChatColor.GOLD, "Ranked");

    public static final SkywarsMode[] VALUES = SkywarsMode.values();

    public static SkywarsMode fromTitle(@NonNull String modeTitle) {
        for (SkywarsMode skywarsMode : VALUES) {

            if (skywarsMode.title.equalsIgnoreCase(modeTitle)) {
                return skywarsMode;
            }
        }

        return null;
    }

    ChatColor color;
    String title;
}
