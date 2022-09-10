package dev.kosmx.playerAnim.impl;

import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixins;

@Mod("playeranimator")
public class ForgeModInterface {
    public ForgeModInterface() {
    }


    static {
        if (Helper.isBendEnabled()) {
            //Use bend mixin(s) ONLY in bend mode
            Mixins.addConfiguration("playerAnimator-onlyBend.mixins.json");
        }
    }
}
