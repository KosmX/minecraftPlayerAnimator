package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;

@Mod("playeranimator")
public class ForgeModInterface {
    public ForgeModInterface() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::resourceLoadingListener);
    }

    private void resourceLoadingListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> {
            PlayerAnimationRegistry.clearAnimation();
            for (var resource: manager.listResources("player_animation", location -> location.getPath().endsWith(".json")).entrySet()) {
                try (var input = resource.getValue().open()) {

                    //Deserialize the animation json. GeckoLib animation json can contain multiple animations.
                    for (var animation : AnimationSerializing.deserializeAnimation(input)) {

                        //Save the animation for later use.
                        PlayerAnimationRegistry.addAnimation(new ResourceLocation(resource.getKey().getNamespace(), PlayerAnimationRegistry.serializeTextToString((String) animation.extraData.get("name"))), animation);
                    }
                } catch(IOException e) {
                    throw new RuntimeException(e);//Somehow handle invalid animations
                }
            }
        });

    }


    static {
        if (Helper.isBendEnabled()) {
            //Use bend mixin(s) ONLY in bend mode
            Mixins.addConfiguration("playerAnimator-onlyBend.mixins.json");
        }
    }
}
