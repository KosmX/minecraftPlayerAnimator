package dev.kosmx.playerAnim.impl.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HelperImpl {
    public static boolean isBendyLibPresent() {
        return FabricLoader.getInstance().isModLoaded("bendy-lib");
    }
}
