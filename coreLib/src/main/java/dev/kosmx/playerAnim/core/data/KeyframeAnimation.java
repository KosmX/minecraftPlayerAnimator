package dev.kosmx.playerAnim.core.data;


import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.layered.IActualAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.opennbs.NBS;
import dev.kosmx.playerAnim.core.util.Ease;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Used to store Emote data
 * <p>
 * Animation data <br>
 * Notable key points in <i>time</i>: <br>
 * begin: probably the first keyframe, before this, the default model can move to starting pose <br>
 * end: last animation keyframe <br>
 * stop: animating ends, after end the character can go back to its default pose <br>
 * <p>
 * isInfinite: <br>
 * if true, the animation will jump back to returnToTick after endTick <i>inclusive</i>
 * <p>
 * To play an animation use {@link KeyframeAnimationPlayer}
 *
 */
@Immutable
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class KeyframeAnimation implements IPlayable {
    //Time, while the player can move to the beginning pose

    public static final StateCollection.State EMPTY_STATE = new StateCollection.State("empty", 0, 0, false);

    public final int beginTick;
    public final int endTick;
    public final int stopTick;
    public final boolean isInfinite;
    //if infinite, where to return
    public final int returnToTick;

    @Getter
    private final Map<PartKey, StateCollection> keyBodyParts;

    //Deprecated variables will be removed in the animation rework part.
    public final boolean isEasingBefore;
    public final boolean nsfw;

    /**
     * -- GETTER --
     *  Uuid of the emote. used for key binding and for server-client identification
     *
     * @return UUID
     */
    //Emote identifier code.
    @Getter
    private final UUID uuid;
    /**
     * Is the uuid generated when loading or was loaded from a file
     */
    public final boolean isUUIDGenerated;

    /**
     * <b>Mutable</b> extra members for extra information store
     */
    public final HashMap<String, Object> extraData = new HashMap<>();

    /**
     * Where is the animation from, not used in equals or hash.
     */
    public final AnimationFormat animationFormat;


    private KeyframeAnimation(int beginTick, int endTick, int stopTick, boolean isInfinite, int returnToTick, HashMap<String, StateCollection> bodyParts, boolean isEasingBefore, boolean nsfw, UUID uuid, AnimationFormat emoteFormat, HashMap<String, Object> extraData) {
        this.beginTick = Math.max(beginTick, 0);
        this.endTick = Math.max(beginTick + 1, endTick);
        this.stopTick = stopTick <= endTick ? endTick + 3 : stopTick;
        this.isInfinite = isInfinite;
        if (isInfinite && (returnToTick < 0 || returnToTick > endTick)) throw new IllegalArgumentException("Trying to construct invalid animation");
        this.returnToTick = returnToTick;
        HashMap<PartKey, StateCollection> bodyMap = new HashMap<>();
        for (Map.Entry<String, StateCollection> entry : bodyParts.entrySet()) {
            bodyMap.put(PartKey.keyForId(entry.getKey()), entry.getValue().copy());
        }
        bodyMap.forEach((s, stateCollection) -> stateCollection.verifyAndLock(getLength()));
        this.keyBodyParts = Collections.unmodifiableMap(bodyMap);

        this.isEasingBefore = isEasingBefore;
        this.nsfw = nsfw;
        if (uuid == null) {
            this.isUUIDGenerated = true;
            uuid = this.generateUuid();
        } else {
            this.isUUIDGenerated = false;
        }
        this.uuid = uuid;
        this.animationFormat = emoteFormat;
        assert emoteFormat != null;
        this.extraData.putAll(extraData);
    }

    /**
     * ExtraData from source are ignored
     *
     * @param o other
     * @return are the object equals or the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyframeAnimation)) return false;

        KeyframeAnimation emoteData = (KeyframeAnimation) o;

        if (beginTick != emoteData.beginTick) return false;
        if (endTick != emoteData.endTick) return false;
        if (stopTick != emoteData.stopTick) return false;
        if (isInfinite != emoteData.isInfinite) return false;
        if (returnToTick != emoteData.returnToTick) return false;
        if (isEasingBefore != emoteData.isEasingBefore) return false;
        //if (!Objects.equals(this.extraData, emoteData.extraData)) return false;

        return keyBodyParts.equals(emoteData.keyBodyParts);
    }

    @Override
    public int hashCode() {
        int result = beginTick;
        result = 31 * result + endTick;
        result = 31 * result + stopTick;
        result = 31 * result + (isInfinite ? 1 : 0);
        result = 31 * result + returnToTick;
        result = 31 * result + (isEasingBefore ? 1 : 0);
        result = 31 * result + keyBodyParts.hashCode();
        return result;
    }

    private UUID generateUuid() {
        int result = beginTick;
        result = 31 * result + endTick;
        result = 31 * result + stopTick;
        result = 31 * result + (isInfinite ? 1 : 0);
        result = 31 * result + returnToTick;
        result = 31 * result + (isEasingBefore ? 1 : 0);

        // slow, but reliable :D
        long dataHash = result * 31L + this.getBodyParts().hashCode();

        long nameHash = this.extraData.hashCode();
        long descHash = 0;
        long authHash = result * 31L + this.extraData.hashCode();
        //long iconHash = this.iconData == null ? 0 : iconData.hashCode() + authHash * 31;


        return new UUID(dataHash << Integer.SIZE + nameHash, descHash << Integer.SIZE + authHash);
    }


    public KeyframeAnimation copy() {
        return this.mutableCopy().build();
    }

    public AnimationBuilder mutableCopy() {
        HashMap<String, StateCollection> newParts = new HashMap<>();
        for (Map.Entry<PartKey, StateCollection> part : this.keyBodyParts.entrySet()) {
            newParts.put(part.getKey().getKey(), part.getValue().copy());
        }
        return new AnimationBuilder(beginTick, endTick, stopTick, isInfinite, returnToTick, newParts, isEasingBefore, nsfw, uuid, animationFormat, extraData);
    }


    public boolean isPlayingAt(int tick) {
        return isInfinite || tick < stopTick && tick > 0;
    }

    @Override
    public UUID get() {
        return this.uuid;
    }

    /**
     * Will return invalid information if {@link KeyframeAnimation#isInfinite} is true
     *
     * @return The length of the emote in ticks (20 t/s)
     */
    public int getLength() {
        return stopTick;
    }

    public boolean isInfinite() {
        return isInfinite;
    }


    /**
     * @param partID part ID string
     * @deprecated use {@link KeyframeAnimation#getPart(PartKey)} instead. that is faster if you know the key. (PartKey.HEAD) for example
     */
    @Nullable
    @Deprecated
    public StateCollection getPart(String partID) {
        return this.keyBodyParts.get(PartKey.keyForId(partID));
    }

    @Nullable
    public StateCollection getPart(@NotNull PartKey partID) {
        return this.keyBodyParts.get(partID);
    }

    /**
     * @param id part ID string
     * @deprecated use {@link KeyframeAnimation#getPartOptional(PartKey)} instead. that is faster if you know the key.
     */
    public @NotNull Optional<StateCollection> getPartOptional(@NotNull String id) {
        return Optional.ofNullable(getPart(id));
    }

    public @NotNull Optional<StateCollection> getPartOptional(@NotNull PartKey id) {
        return Optional.ofNullable(getPart(id));
    }

    @Override
    public @NotNull IActualAnimation<KeyframeAnimationPlayer> playAnimation() {
        return new KeyframeAnimationPlayer(this);
    }

    @Override
    public @NotNull String getName() {
        return ((String)extraData.get("name")).toLowerCase(Locale.ROOT);
    }

    //@Deprecated

    /**
     * The old string to part map getter, it may be the only method that does not get removed after the PartKey refactor.
     * It is still used by network an many other tools.
     * @return Parts in string to part map
     */
    public @NotNull Map<String, StateCollection> getBodyParts() {
        Map<String, StateCollection> newMap = new HashMap<>();
        for (var entry: keyBodyParts.entrySet()) {
            newMap.put(entry.getKey().getKey(), entry.getValue());
        }
        return newMap;
    }


    @SuppressWarnings("ConstantConditions")
    public static final class StateCollection {
        public final State x;
        public final State y;
        public final State z;
        public final State pitch;
        public final State yaw;
        public final State roll;
        @Nullable
        public final State bend;
        @Nullable
        public final State bendDirection;
        @Getter
        public final boolean isBendable;
        @Nullable
        public final State scaleX;
        @Nullable
        public final State scaleY;
        @Nullable
        public final State scaleZ;
        @Getter
        public final boolean isScalable;

        public StateCollection(float x, float y, float z, float pitch, float yaw, float roll, float scaleX, float scaleY, float scaleZ, float translationThreshold, boolean bendable, boolean scalable) {
            this.x = new State("x", x, translationThreshold, false);
            this.y = new State("y", y, translationThreshold, false);
            this.z = new State("z", z, translationThreshold, false);
            this.pitch = new State("pitch", pitch, 0, true);
            this.yaw = new State("yaw", yaw, 0, true);
            this.roll = new State("roll", roll, 0, true);
            if (bendable) {
                this.bendDirection = new State("axis", 0, 0, true);
                this.bend = new State("bend", 0, 0, true);
            } else {
                this.bend = null;
                this.bendDirection = null; //This will cause some errors, but fixes the invalid data problem
            }
            this.isBendable = bendable;
            if (scalable) {
                this.scaleX = new State("scaleX", scaleX, 0, false);
                this.scaleY = new State("scaleY", scaleY, 0, false);
                this.scaleZ = new State("scaleZ", scaleZ, 0, false);
            } else {
                this.scaleX = null;
                this.scaleY = null;
                this.scaleZ = null;
            }
            this.isScalable = scalable;
        }

        public StateCollection(StateCollection stateCollection) {
            this.x = stateCollection.x.copy();
            this.y = stateCollection.y.copy();
            this.z = stateCollection.z.copy();
            this.pitch = stateCollection.pitch.copy();
            this.yaw = stateCollection.yaw.copy();
            this.roll = stateCollection.roll.copy();
            this.isBendable = stateCollection.isBendable;
            if (stateCollection.isBendable) {
                this.bendDirection = stateCollection.bendDirection.copy();
                this.bend = stateCollection.bend.copy();
            } else {
                this.bend = null;
                this.bendDirection = null;
            }
            this.isScalable = stateCollection.isScalable;
            if (stateCollection.isScalable) {
                this.scaleX = stateCollection.scaleX.copy();
                this.scaleY = stateCollection.scaleY.copy();
                this.scaleZ = stateCollection.scaleZ.copy();
            } else {
                this.scaleX = null;
                this.scaleY = null;
                this.scaleZ = null;
            }
        }

        public StateCollection(float x, float y, float z, float pitch, float yaw, float roll, float threshold, boolean bendable) {
            this(x, y, z, pitch, yaw, roll, 1.0F, 1.0F, 1.0F, threshold, true, false);
        }

        public StateCollection(float threshold) {
            this(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, threshold, true, true);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StateCollection)) return false;

            StateCollection that = (StateCollection) o;

            if (isBendable != that.isBendable) return false;
            if (isScalable != that.isScalable) return false;

            if (!x.equals(that.x)) return false;
            if (!y.equals(that.y)) return false;
            if (!z.equals(that.z)) return false;
            if (!pitch.equals(that.pitch)) return false;
            if (!yaw.equals(that.yaw)) return false;
            if (!roll.equals(that.roll)) return false;
            if (!Objects.equals(scaleX, that.scaleX)) return false;
            if (!Objects.equals(scaleY, that.scaleY)) return false;
            if (!Objects.equals(scaleZ, that.scaleZ)) return false;
            if (!Objects.equals(bend, that.bend)) return false;
            return Objects.equals(bendDirection, that.bendDirection);
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + x.hashCode();
            result = 31 * result + y.hashCode();
            result = 31 * result + z.hashCode();
            result = 31 * result + pitch.hashCode();
            result = 31 * result + yaw.hashCode();
            result = 31 * result + roll.hashCode();
            result = 31 * result + (bend != null ? bend.hashCode() : 0);
            result = 31 * result + (bendDirection != null ? bendDirection.hashCode() : 0);
            result = 31 * result + (isBendable ? 1 : 0);
            result = 31 * result + (scaleX != null ? scaleX.hashCode() : 0);
            result = 31 * result + (scaleY != null ? scaleY.hashCode() : 0);
            result = 31 * result + (scaleZ != null ? scaleZ.hashCode() : 0);
            result = 31 * result + (isScalable ? 1 : 0);
            return result;
        }


        public void fullyEnablePart(boolean always) {
            if (always || x.isEnabled || y.isEnabled || z.isEnabled || pitch.isEnabled || yaw.isEnabled || roll.isEnabled || (isBendable && (bend.isEnabled || bendDirection.isEnabled)) || (isScalable && (scaleX.isEnabled || scaleY.isEnabled || scaleZ.isEnabled))) {
                this.setEnabled(true);
            }
        }

        public void setEnabled(boolean enabled) {
            x.setEnabled(enabled);
            y.setEnabled(enabled);
            z.setEnabled(enabled);
            pitch.setEnabled(enabled);
            yaw.setEnabled(enabled);
            roll.setEnabled(enabled);
            if (isBendable) {
                bend.setEnabled(enabled);
                bendDirection.setEnabled(enabled);
            }
            if (isScalable) {
                scaleX.setEnabled(enabled);
                scaleY.setEnabled(enabled);
                scaleZ.setEnabled(enabled);
            }
        }

        public boolean isEnabled() {
            return x.isEnabled()
                            || y.isEnabled()
                            || z.isEnabled()
                            || pitch.isEnabled()
                            || yaw.isEnabled()
                            || roll.isEnabled()
                            || bend != null && bend.isEnabled()
                            || bendDirection != null && bend.isEnabled()
                            || scaleX != null && scaleX.isEnabled()
                            || scaleY != null && scaleY.isEnabled()
                            || scaleZ != null && scaleZ.isEnabled();
        }

        public void verifyAndLock(int maxLength) {
            x.lockAndVerify(maxLength);
            y.lockAndVerify(maxLength);
            z.lockAndVerify(maxLength);
            pitch.lockAndVerify(maxLength);
            yaw.lockAndVerify(maxLength);
            roll.lockAndVerify(maxLength);
            if (bend != null) bend.lockAndVerify(maxLength);
            if (bendDirection != null) bendDirection.lockAndVerify(maxLength);
            if (scaleX != null) scaleX.lockAndVerify(maxLength);
            if (scaleY != null) scaleY.lockAndVerify(maxLength);
            if (scaleZ != null) scaleZ.lockAndVerify(maxLength);
        }


        private void optimize(boolean isLooped, int ret) {
            x.optimize(isLooped, ret);
            y.optimize(isLooped, ret);
            z.optimize(isLooped, ret);
            pitch.optimize(isLooped, ret);
            yaw.optimize(isLooped, ret);
            roll.optimize(isLooped, ret);
            if (isBendable) {
                bend.optimize(isLooped, ret);
                bendDirection.optimize(isLooped, ret);
            }
            if (isScalable) {
                scaleX.optimize(isLooped, ret);
                scaleY.optimize(isLooped, ret);
                scaleZ.optimize(isLooped, ret);
            }
        }

        public StateCollection copy() {
            return new StateCollection(this);
        }

        public static final class State {
            private boolean isModifiable = true;
            public final float defaultValue;
            public final float threshold;
            @Getter
            private List<KeyFrame> keyFrames = new ArrayList<>();
            public final String name;
            private final boolean isAngle;
            @Getter
            private boolean isEnabled = false;

            /**
             * Creates a <b>mutable</b> copy
             * @param state deep copy this, non-null
             */
            @SuppressWarnings("CopyConstructorMissesField") //I know, I want to make mutable copy of this
            public State(State state) {
                this.defaultValue = state.defaultValue;
                this.threshold = state.threshold;
                this.keyFrames.addAll(state.keyFrames); //KeyFrames are immutable, copying them is safe
                this.name = state.name;
                this.isAngle = state.isAngle;
                this.setEnabled(state.isEnabled);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof State)) return false;

                State state = (State) o;

                if (Float.compare(state.defaultValue, defaultValue) != 0) return false;
                if (isAngle != state.isAngle) return false;
                if (!keyFrames.equals(state.keyFrames)) return false;
                if (isEnabled != state.isEnabled) return false;
                return Objects.equals(name, state.name);
            }

            /**
             * Locks the object making it effectively immutable
             */
            private void lock() {
                this.isModifiable = false;
                this.keyFrames = Collections.unmodifiableList(keyFrames);
            }

            /**
             * Locks the object, throws exception if contains invalid data
             * @param maxLength length of animation
             */
            public void lockAndVerify(int maxLength) {
                for (KeyFrame keyFrame : getKeyFrames()) {
                    if (keyFrame == null || keyFrame.tick < 0 || keyFrame.ease == null || !Float.isFinite(keyFrame.value)) throw new IllegalArgumentException("Animation is invalid: " + keyFrame);
                }
                this.lock();
            }

            public void setEnabled(boolean newValue) {
                if (this.isModifiable) {
                    this.isEnabled = newValue;
                } else {
                    throw new AssertionError("Can not modify locked things");
                }
            }

            @Override
            public int hashCode() {
                int result = (defaultValue != 0.0f ? Float.floatToIntBits(defaultValue) : 0);
                result = 31 * result + keyFrames.hashCode();
                result = 31 * result + (isAngle ? 1 : 0);
                result = 31 * result + (isEnabled ? 1 : 0);
                return result;
            }

            /**
             * @param name         Name (for import stuff)
             * @param defaultValue default value
             * @param threshold    threshold for validation
             * @param isAngle      isAngle value (if false then it's a translation)
             */
            private State(String name, float defaultValue, float threshold, boolean isAngle) {
                this.defaultValue = defaultValue;
                this.threshold = threshold;
                this.name = name;
                this.isAngle = isAngle;
            }

            public int length() {
                return keyFrames.size();
            }

            /**
             * Find the last keyframe's number before the tick
             *
             * @param tick tick
             * @return given keyframe
             */
            public int findAtTick(int tick) {
                int i = Collections.binarySearch(this.keyFrames, null, (frame, ignore) -> Integer.compare(frame.tick, tick));
                if (i < 0) {
                    i = -i - 2;
                }

                // small correction for edge-case: it is possible to have two keyframes with the same tick in the array, in that case, I should return the later one.
                if (i + 1 < keyFrames.size() && keyFrames.get(i + 1).tick == tick) {
                    return i + 1;
                }
                return i;
            }

            /**
             * Add a new keyframe to the emote
             *
             * @param tick    where
             * @param value   what value
             * @param ease    with what easing
             * @param rotate  360 degrees turn
             * @param degrees is the value in degrees (or radians if false)
             * @return is the keyframe valid
             */
            public boolean addKeyFrame(int tick, float value, Ease ease, int rotate, boolean degrees) {
                return addKeyFrame(tick, value, ease, rotate, degrees, null);
            }

            /**
             * Add a new keyframe to the emote
             *
             * @param tick    where
             * @param value   what value
             * @param ease    with what easing
             * @param rotate  360 degrees turn
             * @param degrees is the value in degrees (or radians if false)
             * @param easingArg self-explanatory
             * @return is the keyframe valid
             */
            public boolean addKeyFrame(int tick, float value, Ease ease, int rotate, boolean degrees, Float easingArg) {
                if (degrees && this.isAngle) value *= 0.01745329251f;
                boolean bl = this.addKeyFrame(new KeyFrame(tick, value, ease, easingArg));
                if (isAngle && rotate != 0) {
                    bl = this.addKeyFrame(new KeyFrame(tick, (float) (value + Math.PI * 2d * rotate), ease, easingArg)) && bl;
                }
                return bl;
            }

            /**
             * Add a new keyframe to the emote
             *
             * @param tick  where
             * @param value what value
             * @param ease  with what easing
             * @return is the keyframe valid
             */
            public boolean addKeyFrame(int tick, float value, Ease ease) {
                return addKeyFrame(tick, value, ease, null);
            }

            /**
             * Add a new keyframe to the emote
             *
             * @param tick  where
             * @param value what value
             * @param ease  with what easing
             * @param easingArg self-explanatory
             * @return is the keyframe valid
             */
            public boolean addKeyFrame(int tick, float value, Ease ease, Float easingArg) {
                if (Float.isNaN(value)) throw new IllegalArgumentException("value can't be NaN");
                return this.addKeyFrame(new KeyFrame(tick, value, ease, easingArg));
            }

            /**
             * Internal add keyframe method
             *
             * @param keyFrame what
             * @return is valid keyframe
             */
            private boolean addKeyFrame(KeyFrame keyFrame) {
                this.setEnabled(true);
                int i = findAtTick(keyFrame.tick) + 1;
                this.keyFrames.add(i, keyFrame);
                return this.isAngle || !(Math.abs(this.defaultValue - keyFrame.value) > this.threshold);
            }

            public void replace(KeyFrame keyFrame, int pos) {
                this.keyFrames.remove(pos);
                this.keyFrames.add(pos, keyFrame);
            }

            public void replaceEase(int pos, Ease ease) {
                KeyFrame original = this.keyFrames.get(pos);
                replace(new KeyFrame(original.tick, original.value, ease), pos);
            }

            private void optimize(boolean isLooped, int returnToTick) {
                for (int i = 1; i < this.keyFrames.size() - 1; i++) {
                    if (keyFrames.get(i - 1).value != keyFrames.get(i).value) {
                        continue;
                    }
                    if (keyFrames.size() <= i + 1 || keyFrames.get(i).value != keyFrames.get(i + 1).value) {
                        continue;
                    }
                    if (isLooped && keyFrames.get(i - 1).tick < returnToTick && keyFrames.get(i).tick >= returnToTick) {
                        continue;
                    }
                    keyFrames.remove(i--);
                }
            }

            public State copy() {
                return new State(this);
            }
        }
    }

    @Immutable
    public static final class KeyFrame {

        public final int tick;
        public final float value;
        public final Ease ease;
        public final Float easingArg;

        public KeyFrame(int tick, float value, Ease ease, Float easingArg) {
            this.tick = tick;
            this.value = value;
            this.ease = ease;
            this.easingArg = easingArg;
        }

        public KeyFrame(int tick, float value, Ease ease) {
            this(tick, value, ease, null);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof KeyFrame) {
                return ((KeyFrame) other).ease == this.ease && Objects.equals(((KeyFrame) other).easingArg, this.easingArg) && ((KeyFrame) other).tick == this.tick && ((KeyFrame) other).value == this.value;
            } else return super.equals(other);
        }

        public KeyFrame(int tick, float value) {
            this(tick, value, Ease.INOUTSINE);
        }

        @Override
        public int hashCode() {
            int result = tick;
            result = 31 * result + Float.hashCode(value);
            result = 31 * result + ease.getId();
            result = 31 * result + (int)Math.floor((easingArg == null ? 0 : easingArg) * 100);
            return result;
        }

        @Override
        public String toString() {
            String string = "KeyFrame{" +
                    "tick=" + tick +
                    ", value=" + value +
                    ", ease=" + ease;
            if (easingArg != null) {
                string += ", easingArg=";
                string += easingArg;
            }
            string += '}';
            return string;
        }
    }

    public static class AnimationBuilder {

        /**
         * Statically set validation threshold, just a hint
         */
        public static float staticThreshold = 8;


        public final StateCollection head;
        public final StateCollection body;
        public final StateCollection rightArm;
        public final StateCollection leftArm;
        public final StateCollection rightLeg;
        public final StateCollection leftLeg;
        public final StateCollection leftItem;
        public final StateCollection rightItem;
        public final StateCollection torso;
        public boolean isEasingBefore = false;
        //public float validationThreshold = staticThreshold;
        public boolean nsfw = false;
        @Getter
        private final HashMap<String, StateCollection> bodyParts = new HashMap<>();

        /**
         * If you want auto-uuid, leave it null
         */
        @Nullable
        public UUID uuid = null;

        public int beginTick = 0;
        public int endTick;
        public int stopTick = 0;
        public boolean isLooped = false;
        public int returnTick;
        final AnimationFormat emoteEmoteFormat;

        private final float validationThreshold;

        public String name = null;

        //Common names used in Emotecraft
        //If not null, it will be added to extraData
        @Nullable
        public String description = null;
        @Nullable
        public String author = null;

        @Nullable
        public NBS song = null;

        @Nullable
        public ByteBuffer iconData;

        public HashMap<String, Object> extraData = new HashMap<>();

        public AnimationBuilder(AnimationFormat source) {
            this(staticThreshold, source);
        }

        public AnimationBuilder(float validationThreshold, AnimationFormat emoteFormat) {
            this.validationThreshold = validationThreshold;
            head = new StateCollection(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, false, true);
            body = new StateCollection(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold / 8f, true, true);
            rightArm = new StateCollection(-5, 2, 0, 0, 0, 0f, 1.0F, 1.0F, 1.0F, validationThreshold, true, true);
            leftArm = new StateCollection(5, 2, 0, 0, 0, 0f, 1.0F, 1.0F, 1.0F, validationThreshold, true, true);
            leftLeg = new StateCollection(1.9f, 12, 0.1f, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, true, true);
            rightLeg = new StateCollection(-1.9f, 12, 0.1f, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, true, true);
            leftItem = new StateCollection(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, false, true);
            rightItem = new StateCollection(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, false, true);
            torso = new StateCollection(0, 0, 0, 0, 0, 0, 1.0F, 1.0F, 1.0F, validationThreshold, true, true);

            bodyParts.put("head", head);
            bodyParts.put("body", body);
            bodyParts.put("rightArm", rightArm);
            bodyParts.put("rightLeg", rightLeg);
            bodyParts.put("leftArm", leftArm);
            bodyParts.put("leftLeg", leftLeg);
            bodyParts.put("leftItem", leftItem);
            bodyParts.put("rightItem", rightItem);
            bodyParts.put("torso", torso);
            this.emoteEmoteFormat = emoteFormat;
        }

        private AnimationBuilder(int beginTick, int endTick, int stopTick, boolean isInfinite,
                                 int returnToTick, HashMap<String, StateCollection> bodyParts, boolean isEasingBefore, boolean nsfw, @Nullable UUID uuid, AnimationFormat emoteFormat, HashMap<String, Object> extraData) {
            this.validationThreshold = staticThreshold;
            this.bodyParts.putAll(bodyParts);

            head = bodyParts.get("head");
            body = bodyParts.get("body");
            rightArm = bodyParts.get("rightArm");
            rightLeg = bodyParts.get("rightLeg");
            leftArm = bodyParts.get("leftArm");
            leftLeg = bodyParts.get("leftLeg");
            leftItem = bodyParts.get("leftItem");
            rightItem = bodyParts.get("rightItem");
            torso = bodyParts.get("torso");

            this.beginTick = beginTick;
            this.endTick = endTick;
            this.stopTick = stopTick;
            this.isLooped = isInfinite;
            this.returnTick = returnToTick;
            this.isEasingBefore = isEasingBefore;
            this.nsfw = nsfw;
            this.uuid = uuid;
            this.extraData.putAll(extraData);
            this.name = extraData.containsKey("name") && extraData.get("name") instanceof String ? (String) extraData.get("name") : null;
            this.description = extraData.containsKey("description") && extraData.get("description") instanceof String ? (String) extraData.get("description") : null;
            this.author = extraData.containsKey("author") && extraData.get("author") instanceof String ? (String) extraData.get("author") : null;
            this.emoteEmoteFormat = emoteFormat;
            this.iconData = extraData.containsKey("iconData") && extraData.get("iconData") instanceof ByteBuffer ? (ByteBuffer) extraData.get("iconData") : null;
            this.song = extraData.containsKey("song") && extraData.get("song") instanceof NBS ? (NBS) extraData.get("song") : null;

        }

        public AnimationBuilder setDescription(String s) {
            description = s;
            return this;
        }

        public AnimationBuilder setName(String s) {
            name = s;
            return this;
        }

        public AnimationBuilder setAuthor(String s) {
            author = s;
            return this;
        }

        /**
         * Create a new part. X, Y, Z the default offsets, pitch, yaw, roll are the default rotations.
         *
         * @param name     name
         * @param x        x
         * @param y        y
         * @param z        z
         * @param pitch    pitch
         * @param yaw      yaw
         * @param roll     roll
         * @param bendable is it bendable
         * @return ...
         */
        public StateCollection getOrCreateNewPart(String name, float x, float y, float z, float pitch, float yaw, float roll, boolean bendable) {
            if (!bodyParts.containsKey(name)) {
                bodyParts.put(name, new StateCollection(x, y, z, pitch, yaw, roll, validationThreshold, bendable));
            }
            return bodyParts.get(name);
        }

        /**
         * Create a new part. X, Y, Z the default offsets, pitch, yaw, roll are the default rotations, scaleX, scaleY, scaleZ are the default scale.
         *
         * @param name     name
         * @param x        x
         * @param y        y
         * @param z        z
         * @param pitch    pitch
         * @param yaw      yaw
         * @param roll     roll
         * @param scaleX   scaleX
         * @param scaleY   scaleY
         * @param scaleZ   scaleZ
         * @param bendable is it bendable
         * @param scalable is it scalable
         * @return ...
         */
        public StateCollection getOrCreateNewPart(String name, float x, float y, float z, float pitch, float yaw, float roll, float scaleX, float scaleY, float scaleZ, boolean bendable, boolean scalable) {
            if (!bodyParts.containsKey(name)) {
                bodyParts.put(name, new StateCollection(x, y, z, pitch, yaw, roll, scaleX, scaleY, scaleZ, validationThreshold, bendable, scalable));
            }
            return bodyParts.get(name);
        }

        /**
         * Get a part with a name.
         *
         * @param name name
         * @return ...
         */
        @Nullable
        public StateCollection getPart(String name) {
            return bodyParts.get(name);
        }

        public StateCollection getOrCreatePart(String name) {
            if (!bodyParts.containsKey(name)) {
                bodyParts.put(name, new StateCollection(this.validationThreshold));
            }
            return bodyParts.get(name);
        }

        public AnimationBuilder fullyEnableParts() {
            for (Map.Entry<String, StateCollection> part : bodyParts.entrySet()) {
                part.getValue().fullyEnablePart(false);
            }
            return this;
        }

        /**
         * Remove unnecessary keyframes from this emote.
         * If the keyframe before and after are the same as the currently checked, the keyframe will be removed
         */
        public AnimationBuilder optimizeEmote() {
            for (Map.Entry<String, StateCollection> part : bodyParts.entrySet()) {
                part.getValue().optimize(isLooped, returnTick);
            }
            return this;
        }

        /**
         *
         * @return Immutable copy of this
         * @throws IllegalArgumentException if trying to build with invalid data.
         */
        public KeyframeAnimation build() throws IllegalArgumentException {
            if (name != null) extraData.put("name", name);
            if (description != null) extraData.put("description", description);
            if (author != null) extraData.put("author", author);
            if (iconData != null) extraData.put("iconData", iconData);
            if (song != null) extraData.put("song", song);

            return new KeyframeAnimation(beginTick, endTick, stopTick, isLooped, returnTick, bodyParts, isEasingBefore, nsfw, uuid, emoteEmoteFormat, extraData);
        }

        public AnimationBuilder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public String toString() {
            return "AnimationBuilder{" +
                    "uuid=" + uuid +
                    ", extra=" + extraData +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "KeyframeAnimation{" +
                "uuid=" + uuid +
                ", length=" + this.getLength() +
                ", extra=" + extraData +
                '}';
    }
}
