package dev.kosmx.playerAnim.impl.animation;

import dev.kosmx.playerAnim.core.util.Pair;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;

public class BendHelper implements IBendHelper {


    @Override
    public void bend(ModelPart modelPart, float a, float b){
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> ((BendableCuboid)mutableCuboid.getAndActivateMutator("bend")).applyBend(a, b));
    }

    @Override
    public void bend(ModelPart modelPart, @Nullable Pair<Float, Float> pair){
        if(pair != null) {
            this.bend(modelPart, pair.getLeft(), pair.getRight());
        }
        else {
            //ModelPartAccessor.getCuboid(modelPart, 0).getAndActivateMutator(null);
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> mutableCuboid.getAndActivateMutator(null));
        }
    }


}
