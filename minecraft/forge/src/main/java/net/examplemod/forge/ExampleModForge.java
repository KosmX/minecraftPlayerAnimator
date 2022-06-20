package net.examplemod.forge;

import net.examplemod.ExampleMod;
import net.minecraftforge.fml.common.Mod;

@Mod("playerAnimator")
public class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ExampleMod.init();
    }
}
