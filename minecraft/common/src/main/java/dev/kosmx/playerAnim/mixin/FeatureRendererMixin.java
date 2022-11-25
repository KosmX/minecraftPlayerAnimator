package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderLayer.class)
public class FeatureRendererMixin implements IUpperPartHelper {
    @Unique
    private boolean isUpperPart = true;


    @Override
    public boolean isUpperPart() {
        return this.isUpperPart;
    }

    @Override
    public void setUpperPart(boolean bl) {
        this.isUpperPart = bl;
    }
}
