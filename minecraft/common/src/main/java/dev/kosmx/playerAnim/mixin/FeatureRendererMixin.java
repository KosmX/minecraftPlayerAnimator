package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderLayer.class)
public class FeatureRendererMixin implements IUpperPartHelper {
    private boolean Emotecraft_isUpperPart = true;
    @Override
    public boolean isUpperPart() {
        return this.Emotecraft_isUpperPart;
    }

    @Override
    public void setUpperPart(boolean bl) {
        this.Emotecraft_isUpperPart = bl;
    }
}
