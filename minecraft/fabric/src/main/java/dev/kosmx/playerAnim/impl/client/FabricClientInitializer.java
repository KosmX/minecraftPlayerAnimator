package dev.kosmx.playerAnim.impl.client;

import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.compat.skinLayers.SkinLayersTransformer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricClientInitializer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("player-animator");

    @Override
    public void onInitializeClient() {

        if (Helper.isBendEnabled() && FabricLoader.getInstance().isModLoaded("skinlayers")) {
            try {
                SkinLayersTransformer.init(LOGGER);
            } catch(Error e) {
                LOGGER.error("Failed to initialize 3D Skin Layers module: " + e.getMessage());
            }
        }

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("playeranimator", "animation");
            }

            @Override
            public void onResourceManagerReload(@NotNull ResourceManager manager) {
                PlayerAnimationRegistry.resourceLoaderCallback(manager, LOGGER);
            }
        });
    }

}
