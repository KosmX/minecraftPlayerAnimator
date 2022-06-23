package dev.kosmx.playerAnim.impl.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class HelperImpl {
    public static boolean isBendyLibPresent() {
        return FabricLoader.getInstance().isModLoaded("bendy-lib");
    }
}
