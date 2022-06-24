package dev.kosmx.playerAnim.impl.forge;

import net.minecraftforge.fml.ModList;

public class HelperImpl {
    private static boolean isBendyLibPresent() {
        return ModList.get().isLoaded("bendylib");
    }
}
