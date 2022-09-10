package dev.kosmx.playerAnim.impl.client;

import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FabricClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("playeranimator", "animation");
            }

            @Override
            public void onResourceManagerReload(@NotNull ResourceManager manager) {
                PlayerAnimationRegistry.clearAnimation();
                for (var resource: manager.listResources("player_animation", location -> location.endsWith(".json"))) {
                    try (var input = manager.getResource(resource).getInputStream()) {

                        //Deserialize the animation json. GeckoLib animation json can contain multiple animations.
                        for (var animation : AnimationSerializing.deserializeAnimation(input)) {

                            //Save the animation for later use.
                            PlayerAnimationRegistry.addAnimation(new ResourceLocation(resource.getNamespace(), PlayerAnimationRegistry.serializeTextToString((String) animation.extraData.get("name"))), animation);
                        }
                    } catch(IOException e) {
                        throw new RuntimeException(e);//Somehow handle invalid animations
                    }
                }
            }
        });
    }

}
