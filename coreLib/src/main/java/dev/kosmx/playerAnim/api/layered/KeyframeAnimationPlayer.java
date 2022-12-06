package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.*;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Animation player for EmoteX emote format,
 * It does not mean, you can not use it, It means Emotecraft uses this too!
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public class KeyframeAnimationPlayer implements IAnimation {



    private final KeyframeAnimation data;
    private boolean isRunning = true;
    private int currentTick;
    private boolean isLoopStarted = false;

    protected float tickDelta;

    public final HashMap<String, BodyPart> bodyParts;
    public int perspective = 0;

    /**
     *
     * @param emote emote to play
     * @param t begin playing from tick
     * @param mutable if true, the part data will be copied as a construction step.
     *                The copied version can be changed while playing the animation but the copy takes time.
     */
    public KeyframeAnimationPlayer(KeyframeAnimation emote, int t, boolean mutable) {
        if (emote == null) throw new IllegalArgumentException("Animation can not be null");
        this.data = emote;

        this.bodyParts = new HashMap<>(emote.getBodyParts().size());
        for(Map.Entry<String, KeyframeAnimation.StateCollection> part:emote.getBodyParts().entrySet()){
            this.bodyParts.put(part.getKey(), new BodyPart(mutable ? part.getValue().copy() : part.getValue()));
        }

        this.currentTick = t;
        if(isInfinite() && t > data.returnToTick){
            currentTick = (t - data.returnToTick)%(data.endTick - data.returnToTick + 1) + data.returnToTick;
        }
    }


    /**
     *
     * @param emote emote to play
     * @param t begin playing from tick
     */
    public KeyframeAnimationPlayer(KeyframeAnimation emote, int t) {
        this(emote, t, false);
    }

    public KeyframeAnimationPlayer(KeyframeAnimation animation) {
        this(animation, 0);
    }

    @Override
    public void tick() {
        if (this.isRunning) {
            this.currentTick++;
            if (data.isInfinite && this.currentTick > data.endTick) {
                this.currentTick = data.returnToTick;
                this.isLoopStarted = true;
            }
            if (currentTick >= data.stopTick) {
                this.stop();
            }
        }
    }

    public int getTick() {
        return this.currentTick;
    }


    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isActive() {
        return this.isRunning;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        BodyPart part = bodyParts.get(modelName);
        if (part == null) return value0;
        switch (type) {
            case POSITION:
                return part.getBodyOffset(value0);
            case ROTATION:
                Vector3<Float> rot = part.getBodyRotation(value0);
                return new Vec3f(rot.getX(), rot.getY(), rot.getZ());
            case BEND:
                Pair<Float, Float> bend = part.getBend(new Pair<>(value0.getX(), value0.getY()));
                return new Vec3f(bend.getLeft(), bend.getRight(), 0f);
            default:
                return value0;
        }
    }

    @Override
    public void setupAnim(float tickDelta) {
        this.tickDelta = tickDelta;
    }

    /**
     * is the emote already in an infinite loop?
     *
     * @return :D
     */
    public boolean isLoopStarted() {
        return isLoopStarted;
    }


    public KeyframeAnimation getData() {
        return data;
    }

    public BodyPart getPart(String string){
        BodyPart part = bodyParts.get(string);
        return part == null ? new BodyPart(null) : part;
    }


    public int getStopTick() {
        return this.data.stopTick;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public boolean isInfinite() {
        return data.isInfinite;
    }


    public class BodyPart {
        @Nullable
        public final KeyframeAnimation.StateCollection part;
        public final Axis x;
        public final Axis y;
        public final Axis z;
        public final RotationAxis pitch;
        public final RotationAxis yaw;
        public final RotationAxis roll;
        public final RotationAxis bendAxis;
        public final RotationAxis bend;


        public BodyPart(@Nullable KeyframeAnimation.StateCollection part) {
            this.part = part;
            if(part != null) {
                this.x = new Axis(part.x);
                this.y = new Axis(part.y);
                this.z = new Axis(part.z);
                this.pitch = new RotationAxis(part.pitch);
                this.yaw = new RotationAxis(part.yaw);
                this.roll = new RotationAxis(part.roll);
                this.bendAxis = new RotationAxis(part.bendDirection);
                this.bend = new RotationAxis(part.bend);
            }
            else {
                this.x = null;
                this.y = null;
                this.z = null;
                this.pitch = null;
                this.yaw = null;
                this.roll = null;
                this.bendAxis = null;
                this.bend = null;
            }
        }


        public Pair<Float, Float> getBend(Pair<Float, Float> value0) {
            if(bend == null) return value0;
            return new Pair<>(this.bendAxis.getValueAtCurrentTick(value0.getLeft()), this.bend.getValueAtCurrentTick(value0.getRight()));
        }

        public Vec3f getBodyOffset(Vec3f value0) {
            if(this.part == null) return value0;
            float x = this.x.getValueAtCurrentTick(value0.getX());
            float y = this.y.getValueAtCurrentTick(value0.getY());
            float z = this.z.getValueAtCurrentTick(value0.getZ());
            return new Vec3f(x, y, z);
        }

        public Vec3f getBodyRotation(Vec3f value0) {
            if(this.part == null) return value0;
            return new Vec3f(
                    this.pitch.getValueAtCurrentTick(value0.getX()),
                    this.yaw.getValueAtCurrentTick(value0.getY()),
                    this.roll.getValueAtCurrentTick(value0.getZ())
            );
        }

    }

    public class Axis {
        protected final KeyframeAnimation.StateCollection.State keyframes;


        public Axis(KeyframeAnimation.StateCollection.State keyframes) {
            this.keyframes = keyframes;
        }

        /**
         * Find a keyframe before the current tick
         * If none is given: depending on current tick, returns with none/default
         * If given: returns with before:
         * creates a virtual frame at endTick if not looped
         *
         * @param pos          current tick pos, possible candidate
         * @param currentState none state
         * @return Keyframe
         */
        private KeyframeAnimation.KeyFrame findBefore(int pos, float currentState) {
            if (pos == -1) {
                return (currentTick < data.beginTick) ?
                        new KeyframeAnimation.KeyFrame(0, currentState) :
                        (currentTick < data.endTick) ?
                                new KeyframeAnimation.KeyFrame(data.beginTick, keyframes.defaultValue) :
                                new KeyframeAnimation.KeyFrame(data.endTick, keyframes.defaultValue);
            }
            KeyframeAnimation.KeyFrame frame = this.keyframes.getKeyFrames().get(pos);
            if (!isInfinite() && currentTick >= getData().endTick && pos == keyframes.length() - 1 && frame.tick < getData().endTick) {
                return new KeyframeAnimation.KeyFrame(getData().endTick, frame.value, frame.ease);
            }
            return frame;
        }

        /**
         * Return with keyframe after current
         * If given, return
         * If infinity and no following, returns with one, AFTER the end
         * if needed, creates a virtual at the end or other
         * @param pos          pos
         * @param currentState none state
         * @return Keyframe
         */
        private KeyframeAnimation.KeyFrame findAfter(int pos, float currentState) {
            if (this.keyframes.length() > pos + 1) {
                return this.keyframes.getKeyFrames().get(pos + 1);
            }

            if (isInfinite()) {
                return new KeyframeAnimation.KeyFrame(getData().endTick + 1, keyframes.defaultValue);
            }

            if (currentTick < getData().endTick && this.keyframes.length() > 0) {
                KeyframeAnimation.KeyFrame lastFrame = this.keyframes.getKeyFrames().get(this.keyframes.length() - 1);
                return new KeyframeAnimation.KeyFrame(getData().endTick, lastFrame.value, lastFrame.ease);
            }

            return currentTick >= data.endTick ?
                    new KeyframeAnimation.KeyFrame(data.stopTick, currentState) :
                    currentTick >= getData().beginTick ?
                            new KeyframeAnimation.KeyFrame(getData().endTick, keyframes.defaultValue) :
                            new KeyframeAnimation.KeyFrame(getData().beginTick, keyframes.defaultValue);
        }


        /**
         * Get the current value of this axis.
         *
         * @param currentValue the Current value of the axis
         * @return value
         */
        public float getValueAtCurrentTick(float currentValue) {
            if(keyframes != null && keyframes.isEnabled()) {
                int pos = keyframes.findAtTick(currentTick);
                KeyframeAnimation.KeyFrame keyBefore = findBefore(pos, currentValue);
                if (isLoopStarted && keyBefore.tick < data.returnToTick) {
                    keyBefore = findBefore(keyframes.findAtTick(data.endTick), currentValue);
                }
                KeyframeAnimation.KeyFrame keyAfter = findAfter(pos, currentValue);
                if (data.isInfinite && keyAfter.tick > data.endTick) { //If we found nothing, try finding something from the beginning
                    keyAfter = findAfter(keyframes.findAtTick(data.returnToTick - 1), currentValue);
                }
                return getValueFromKeyframes(keyBefore, keyAfter);
            }
            return currentValue;
        }

        /**
         * Calculate the current value between keyframes
         *
         * @param before Keyframe before
         * @param after  Keyframe after
         * @return value
         */
        protected final float getValueFromKeyframes(KeyframeAnimation.KeyFrame before, KeyframeAnimation.KeyFrame after) {
            int tickBefore = before.tick;
            int tickAfter = after.tick;
            if (tickBefore >= tickAfter) {
                if (currentTick < tickBefore) tickBefore -= data.endTick - data.returnToTick + 1;
                else tickAfter += data.endTick - data.returnToTick + 1;
            }
            if (tickBefore == tickAfter) return before.value;
            float f = (currentTick + tickDelta - (float) tickBefore) / (tickAfter - tickBefore);
            return MathHelper.lerp((data.isEasingBefore ? after.ease : before.ease).invoke(f), before.value, after.value);
        }

    }

    public class RotationAxis extends Axis {

        public RotationAxis(KeyframeAnimation.StateCollection.State keyframes) {
            super(keyframes);
        }

        @Override
        public float getValueAtCurrentTick(float currentValue) {
            return MathHelper.clampToRadian(super.getValueAtCurrentTick(MathHelper.clampToRadian(currentValue)));
        }
    }
}
