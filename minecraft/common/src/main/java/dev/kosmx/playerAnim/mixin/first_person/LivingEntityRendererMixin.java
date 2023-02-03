package dev.kosmx.playerAnim.mixin.first_person;

import dev.kosmx.playerAnim.api.first_person.FirstPersonRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Final
    @Shadow List<Object> layers;

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;",
                    opcode = Opcodes.GETFIELD))
    private List<Object> getFeaturesConditionally(LivingEntityRenderer renderer) {
        if (FirstPersonRenderState.isRenderCycleFirstPerson()) {
            // Inside first person render cycle
            // We don't want to render any feature except the held items
            return layers.stream()
                    .filter( item -> {
                        return item instanceof PlayerItemInHandLayer;
                    }).collect(Collectors.toList());
        } else {
            // Normal operations
            return layers;
        }
    }
}