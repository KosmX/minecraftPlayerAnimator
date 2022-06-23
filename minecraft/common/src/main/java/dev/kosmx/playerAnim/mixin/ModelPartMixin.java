package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IUpperPartHelper {
    private boolean Emotecraft_upper = false;

    @Override
    public boolean isUpperPart() {
        return Emotecraft_upper;
    }

    @Override
    public void setUpperPart(boolean bl) {
        Emotecraft_upper = bl;
    }
}
