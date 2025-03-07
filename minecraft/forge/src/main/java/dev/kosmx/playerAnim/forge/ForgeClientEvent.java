package dev.kosmx.playerAnim.forge;

import dev.kosmx.playerAnim.compatibility.AzureArmorRenderHandler;
import dev.kosmx.playerAnim.compatibility.AzureLibArmorRenderHandler;
import dev.kosmx.playerAnim.compatibility.GeckoArmorRenderHandler;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.compat.skinLayers.SkinLayersTransformer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = "playeranimator", dist = Dist.CLIENT)
public class ForgeClientEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger("player-animator");

    public ForgeClientEvent(IEventBus bus) {
        bus.addListener(this::resourceLoadingListener);
        bus.addListener(this::clientSetup);

        if (isModLoaded("geckolib")) {
            NeoForge.EVENT_BUS.register(GeckoArmorRenderHandler.class);
            LOGGER.info("GeckoLib and PlayerAnimator detected. Registering GeckoArmorRenderHandler.");
        }
        if (isModLoaded("azurelibarmor")) {
            NeoForge.EVENT_BUS.register(AzureArmorRenderHandler.class);
            LOGGER.info("AzureLibArmor and PlayerAnimator detected. Registering AzureArmorRenderHandler.");
        }
        if (isModLoaded("azurelib")) {
            NeoForge.EVENT_BUS.register(AzureLibArmorRenderHandler.class);
            LOGGER.info("AzureLib and PlayerAnimator detected. Registering AzureLibArmorRenderHandler.");
        }
    }

    public void clientSetup(FMLClientSetupEvent event) {
        if (Helper.isBendEnabled() && isModLoaded("skinlayers3d")) {
            try {
                SkinLayersTransformer.init(ForgeClientEvent.LOGGER);
            } catch(Error e) {
                ForgeClientEvent.LOGGER.error("Failed to initialize 3D skin layers compat: " + e.getMessage());
            }
        }
    }

    public void resourceLoadingListener(@NotNull RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) PlayerAnimationRegistry::resourceLoaderCallback);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
