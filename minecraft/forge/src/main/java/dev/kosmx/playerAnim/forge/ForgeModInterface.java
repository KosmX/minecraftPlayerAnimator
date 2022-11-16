package dev.kosmx.playerAnim.forge;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

@Mod("playeranimator")
public class ForgeModInterface {

    public static final Logger LOGGER = LogManager.getLogger("player-animator");

    public ForgeModInterface() {
    }

}
