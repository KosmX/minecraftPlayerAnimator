package dev.kosmx.playerAnim.impl.animation;

import dev.kosmx.playerAnim.core.util.Pair;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BendHelper implements IBendHelper {


    @Override
    public void bend(ModelPart modelPart, float axis, float rotation){
        // Don't enable bend until rotation is bigger than epsilon. This should avoid unnecessary heavy calculations.
        if (Math.abs(rotation) >= 0.0001f) {
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> ((BendableCuboid) mutableCuboid.getAndActivateMutator("bend")).applyBend(axis, rotation));
        } else {
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> mutableCuboid.getAndActivateMutator(null));
        }
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

    @Override
    public void initBend(ModelPart modelPart, Direction direction) {
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(direction).build(data)));
    }


}
