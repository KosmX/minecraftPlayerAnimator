package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;
import java.io.InputStream;

@Mod("playeranimator")
public class ForgeModInterface {
    public ForgeModInterface() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::resourceLoadingListener);
    }

    private void resourceLoadingListener(FMLClientSetupEvent event) {
        ((ReloadableResourceManager)(event.getMinecraftSupplier().get().getResourceManager())).registerReloadListener((ResourceManagerReloadListener) manager -> {
            PlayerAnimationRegistry.clearAnimation();
            for (ResourceLocation resource: manager.listResources("player_animation", location -> location.endsWith(".json"))) {
                try (InputStream input = manager.getResource(resource).getInputStream()) {

                    //Deserialize the animation json. GeckoLib animation json can contain multiple animations.
                    for (KeyframeAnimation animation : AnimationSerializing.deserializeAnimation(input)) {

                        //Save the animation for later use.
                        PlayerAnimationRegistry.addAnimation(new ResourceLocation(resource.getNamespace(), PlayerAnimationRegistry.serializeTextToString((String) animation.extraData.get("name"))), animation);
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
