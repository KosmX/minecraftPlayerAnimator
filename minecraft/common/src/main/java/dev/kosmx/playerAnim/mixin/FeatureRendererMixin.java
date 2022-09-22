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
    private boolean isUpperPart = true;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(RenderLayerParent renderLayerParent, CallbackInfo ci) {
        if (this.getClass().getPackageName().contains("skinlayers") && !this.getClass().getSimpleName().toLowerCase().contains("head")) {
            isUpperPart = false;
        }
    }

    @Override
    public boolean isUpperPart() {
        return this.isUpperPart;
    }

    @Override
    public void setUpperPart(boolean bl) {
        this.isUpperPart = bl;
    }
}
