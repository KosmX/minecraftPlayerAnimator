package dev.kosmx.playerAnim.impl;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

@Mod("playeranimator")
public class ForgeModInterface {

    public static final Logger LOGGER = LogManager.getLogger("player-animator");

    public ForgeModInterface() {
    }


    static {
        if (Helper.isBendEnabled()) {
            //Use bend mixin(s) ONLY in bend mode
            Mixins.addConfiguration("playerAnimator-onlyBend.mixins.json");
        }
    }
}
