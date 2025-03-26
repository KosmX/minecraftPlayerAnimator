package dev.kosmx.playerAnim.fabric.client;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class FabricClientInitializer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("player-animator");

    @Override
    public void onInitializeClient() {

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath("playeranimator", "animation");
            }

            @Override
            public void onResourceManagerReload(@NotNull ResourceManager manager) {
                PlayerAnimationRegistry.resourceLoaderCallback(manager);
            }
        });
    }

}
