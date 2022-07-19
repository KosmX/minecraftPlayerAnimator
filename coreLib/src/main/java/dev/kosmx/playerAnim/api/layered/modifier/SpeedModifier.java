package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;

/**
 * Modifies the animation speed.
 * speed = 2 means twice the speed, the animation will take half as long
 * <code>length = 1/speed</code>
 */
public class SpeedModifier extends AbstractModifier {
    public float speed;

    private float delta = 0;

    private float shiftedDelta = 0;

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
        while (delta > 1) {
            delta -= 1;
            tick();
        }
        setupAnim(delta);
        this.shiftedDelta = delta;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        return super.get3DTransform(modelName, type, shiftedDelta, value0);
    }
}
