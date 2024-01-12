package dev.kosmx.playerAnim.forge;

import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.compat.skinLayers.SkinLayersTransformer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = "playeranimator", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientEvent {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        if (Helper.isBendEnabled() && ModList.get().isLoaded("skinlayers3d")) {
            try {
                SkinLayersTransformer.init(ForgeModInterface.LOGGER);
            } catch(Error e) {
                ForgeModInterface.LOGGER.error("Failed to initialize 3D skin layers compat: " + e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public static void resourceLoadingListener(@NotNull RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> PlayerAnimationRegistry.resourceLoaderCallback(manager, ForgeModInterface.LOGGER));
    }

}
