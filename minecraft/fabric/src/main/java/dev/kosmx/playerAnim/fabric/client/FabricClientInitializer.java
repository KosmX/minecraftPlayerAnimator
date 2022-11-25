package dev.kosmx.playerAnim.fabric.client;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class FabricClientInitializer implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("player-animator");

    @Override
    public void onInitializeClient() {

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
