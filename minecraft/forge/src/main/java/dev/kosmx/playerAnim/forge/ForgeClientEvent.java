package dev.kosmx.playerAnim.forge;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = "playeranimator", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEvent {

    /**
     * I have nothing to do with ParticleFactoryRegisterEvent, but it is an ideal callback to register resource reload listener...
     * Mixin into Minecraft.class is risky
     * @param garbage something, I don't even need
     */
    @SubscribeEvent
    public static void resourceLoadingListener(@NotNull ParticleFactoryRegisterEvent garbage) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener) manager -> PlayerAnimationRegistry.resourceLoaderCallback(manager, ForgeModInterface.LOGGER));
    }
}
