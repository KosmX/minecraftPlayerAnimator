package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.class)
public class FeatureRendererMixin implements IUpperPartHelper {
    @Unique
    private boolean playerAnimator$isUpperPart = true;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(RenderLayerParent<?, ?> renderLayerParent, CallbackInfo ci) {
        if (this.getClass().getPackageName().contains("skinlayers") && !this.getClass().getSimpleName().toLowerCase().contains("head")) {
            playerAnimator$isUpperPart = false;
        }
    }

    @Override
    public boolean playerAnimator$isUpperPart() {
        return this.playerAnimator$isUpperPart;
    }

    @Override
    public void playerAnimator$setUpperPart(boolean bl) {
        this.playerAnimator$isUpperPart = bl;
    }
}
