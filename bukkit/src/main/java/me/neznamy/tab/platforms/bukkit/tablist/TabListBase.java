package me.neznamy.tab.platforms.bukkit.tablist;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.neznamy.chat.component.TabComponent;
import me.neznamy.tab.platforms.bukkit.BukkitTabPlayer;
import me.neznamy.tab.platforms.bukkit.BukkitUtils;
import me.neznamy.tab.platforms.bukkit.header.HeaderFooter;
import me.neznamy.tab.platforms.bukkit.nms.BukkitReflection;
import me.neznamy.tab.shared.platform.decorators.TrackedTabList;
import me.neznamy.tab.shared.util.ReflectionUtils;
import me.neznamy.tab.shared.util.function.FunctionWithException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Base TabList class for all implementations.
 */
public abstract class TabListBase extends TrackedTabList<BukkitTabPlayer> {

    /** Instance function */
    @Getter
    @Setter
    private static FunctionWithException<BukkitTabPlayer, TabListBase> instance;

    @Nullable
    protected static SkinData skinData;

    /**
     * Constructs new instance.
     *
     * @param   player
     *          Player this tablist will belong to
     */
    protected TabListBase(@NotNull BukkitTabPlayer player) {
        super(player);
    }

    /**
     * Finds the best available instance for current server software.
     */
    public static void findInstance() {
        try {
            if (ReflectionUtils.classExists("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket")) {
                PacketTabList1193.loadNew();
                instance = PacketTabList1193::new;
            } else if (BukkitReflection.getMinorVersion() >= 8) {
                PacketTabList18.load();
                instance = PacketTabList18::new;
            } else {
                PacketTabList17.load();
                instance = PacketTabList17::new;
            }
        } catch (Exception e) {
            BukkitUtils.compatibilityError(e, "tablist entry management", "Bukkit API",
                    "Layout feature will not work",
                    "Prevent-spectator-effect feature will not work",
                    "Ping spoof feature will not work",
                    "Tablist formatting missing anti-override",
                    "Tablist formatting not supporting relational placeholders");
            instance = BukkitTabList::new;
        }
    }

    @Override
    public void setPlayerListHeaderFooter(@NonNull TabComponent header, @NonNull TabComponent footer) {
        if (HeaderFooter.getInstance() != null) HeaderFooter.getInstance().set(player, header, footer);
    }

    @Override
    public boolean containsEntry(@NonNull UUID entry) {
        return true; // TODO?
    }

    /**
     * Returns player's skin. If NMS fields did not load or server is in
     * offline mode, returns {@code null}.
     *
     * @return  Player's skin or {@code null}.
     */
    @Nullable
    public Skin getSkin() {
        if (skinData == null) return null;
        return skinData.getSkin(player);
    }
}
