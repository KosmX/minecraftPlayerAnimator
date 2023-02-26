package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Modifies the animation speed.
 * speed = 2 means twice the speed, the animation will take half as long
 * <code>length = 1/speed</code>
 */
@NoArgsConstructor
public class SpeedModifier extends AbstractModifier {
    public float speed = 1;

    private float delta = 0;

    private float shiftedDelta = 0;


    public SpeedModifier(float speed) {
        if (!Float.isFinite(speed)) throw new IllegalArgumentException("Speed must be a finite number");
        this.speed = speed;
    }

    @Override
    public void tick() {
        float delta = 1f - this.delta;
        this.delta = 0;
        step(delta);
    }

    @Override
    public void setupAnim(float tickDelta) {
        float delta = tickDelta - this.delta; //this should stay positive
        this.delta = tickDelta;
        step(delta);
    }

    protected void step(float delta) {
        delta *= speed;
        delta += shiftedDelta;
        while (delta > 1) {
            delta -= 1;
            super.tick();
        }
        super.setupAnim(delta);
        this.shiftedDelta = delta;
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return super.get3DTransform(modelName, type, shiftedDelta, value0);
    }
}
