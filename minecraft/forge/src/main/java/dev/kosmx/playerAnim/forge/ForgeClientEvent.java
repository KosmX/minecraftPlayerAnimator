package dev.kosmx.playerAnim.forge;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = "playeranimator", dist = Dist.CLIENT)
public class ForgeClientEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger("player-animator");

    public ForgeClientEvent(IEventBus bus) {
        bus.addListener(this::resourceLoadingListener);
        bus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {

    }

    public void resourceLoadingListener(AddClientReloadListenersEvent event) {
        event.addListener(PlayerAnimationRegistry.KEY,
                (ResourceManagerReloadListener) PlayerAnimationRegistry::resourceLoaderCallback
        );
    }
}
