package dev.kosmx.playerAnim.impl.forge;

import dev.kosmx.playerAnim.impl.ForgeModInterface;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = "playeranimator", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEvent {

    @SubscribeEvent
    public static void resourceLoadingListener(@NotNull RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> PlayerAnimationRegistry.resourceLoaderCallback(manager, ForgeModInterface.LOGGER));
    }

}
