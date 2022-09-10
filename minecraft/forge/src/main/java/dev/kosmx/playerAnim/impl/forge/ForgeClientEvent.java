package dev.kosmx.playerAnim.impl.forge;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

@Mod.EventBusSubscriber(modid = "playeranimator", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEvent {

    /**
     * I have nothing to do with ParticleFactoryRegisterEvent, but it is an ideal callback to register resource reload listener...
     * Mixin into Minecraft.class is risky
     * @param garbage something, I don't even need
     */
    @SubscribeEvent
    public static void resourceLoadingListener(@NotNull ParticleFactoryRegisterEvent garbage) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener) manager -> {
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
}
