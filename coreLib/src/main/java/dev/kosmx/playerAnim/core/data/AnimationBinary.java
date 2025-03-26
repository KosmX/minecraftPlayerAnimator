package dev.kosmx.playerAnim.core.data;

import dev.kosmx.playerAnim.core.util.Ease;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to convert animation data to a binary format.
 * Includes a size predictor, using {@link java.nio.ByteBuffer}
 * Does <b>not</b> pack extraData, that must be done manually
 */
@SuppressWarnings("unused")
public final class AnimationBinary {
    /**
     * Write the animation into the ByteBuffer.
     * Versioning:
     * 1. Emotecraft 2.1 features
     * 2. New animation format for Animation library - including enable states, dynamic parts
     * format type 1 takes fewer data, but only works for standard models and unable to send data for disabled states
     * @param animation animation
     * @param buf       target byteBuf
     * @param version   Binary version
     * @return          target byteBuf for chaining
     * @param <T> ByteBuffer
     *
     * @throws java.nio.BufferOverflowException if can't write into ByteBuf
     */
    public static <T extends ByteBuffer> T write(KeyframeAnimation animation, T buf, int version) throws BufferOverflowException {
        buf.putInt(animation.beginTick);
        buf.putInt(animation.endTick);
        buf.putInt(animation.stopTick);
        putBoolean(buf, animation.isInfinite());
        buf.putInt(animation.returnToTick);
        putBoolean(buf, animation.isEasingBefore);
        putBoolean(buf, animation.nsfw);
        buf.put(keyframeSize(version));
        if (version >= 2) {
            buf.putInt(animation.getBodyParts().size());
            for (Map.Entry<String, KeyframeAnimation.StateCollection> part : animation.getBodyParts().entrySet()) {
                putString(buf, part.getKey());
                writePart(buf, part.getValue(), version);
            }
        } else {
            writePart(buf, animation.getPart("head"), version);
            writePart(buf, animation.getPart("body"), version);
            writePart(buf, animation.getPart("rightArm"), version);
            writePart(buf, animation.getPart("leftArm"), version);
            writePart(buf, animation.getPart("rightLeg"), version);
            writePart(buf, animation.getPart("leftLeg"), version);
        }
        buf.putLong(animation.getUuid().getMostSignificantBits());
        buf.putLong(animation.getUuid().getLeastSignificantBits());

        return buf;
    }

    /**
     * Write the animation into the ByteBuffer using the latest format version
     * @param animation animation
     * @param buf       target byteBuf
     * @return          target byteBuf for chaining
     * @param <T>       ByteBuffer
     * @throws BufferOverflowException if can't write into byteBuf
     */
    public static <T extends ByteBuffer> T write(KeyframeAnimation animation, T buf) throws BufferOverflowException {
        return write(animation, buf, getCurrentVersion());
    }

    @SuppressWarnings("ConstantConditions")
    private static void writePart(ByteBuffer buf, KeyframeAnimation.StateCollection part, int version) {
        writeKeyframes(buf, part.x, version);
        writeKeyframes(buf, part.y, version);
        writeKeyframes(buf, part.z, version);
        writeKeyframes(buf, part.pitch, version);
        writeKeyframes(buf, part.yaw, version);
        writeKeyframes(buf, part.roll, version);
        if(part.isBendable) {
            writeKeyframes(buf, part.bendDirection, version);
            writeKeyframes(buf, part.bend, version);
        }
        if (part.isScalable && version >= 3) {
            writeKeyframes(buf, part.scaleX, version);
            writeKeyframes(buf, part.scaleY, version);
            writeKeyframes(buf, part.scaleZ, version);
        }
    }

    private static void writeKeyframes(ByteBuffer buf, KeyframeAnimation.StateCollection.State part, int version) {
        List<KeyframeAnimation.KeyFrame> list = part.getKeyFrames();
        if (version >= 2) {
            putBoolean(buf, part.isEnabled());
            buf.putInt(list.size());
        } else {
            buf.putInt(part.isEnabled() ? list.size() : -1);
        }
        if (part.isEnabled() || version >= 2) {
            for (KeyframeAnimation.KeyFrame move : list) {
                buf.putInt(move.tick);
                buf.putFloat(move.value);
                buf.put(move.ease.getId());
                if (version >= 4) {
                    buf.putFloat(move.easingArg != null ? move.easingArg : Float.NaN);
                }
            }
        }
    }


    /**
     * Read keyframe animation from binary data.
     * creates a Bool extra property with validation data with name <code>valid</code>
     * @param buf       byteBuf
     * @param version   format version (not stored in binary)
     * @return          KeyframeAnimation
     * @throws java.nio.BufferUnderflowException if there is not enough data in ByteBuffer
     * @throws IOException if encounters invalid data
     */
    public static KeyframeAnimation read(ByteBuffer buf, int version) throws IOException {
        KeyframeAnimation.AnimationBuilder animation = new KeyframeAnimation.AnimationBuilder(AnimationFormat.BINARY);
        animation.beginTick = buf.getInt();
        animation.endTick = buf.getInt();
        animation.stopTick = buf.getInt();
        animation.isLooped = getBoolean(buf);
        animation.returnTick = buf.getInt();
        animation.isEasingBefore = getBoolean(buf);
        animation.nsfw = getBoolean(buf);
        int keyframeSize = buf.get();
        if(!(keyframeSize > 0)) throw new IOException("keyframe size must be greater than 0, current: " + keyframeSize);
        boolean valid = true;
        if (version >= 2) {
            int count = buf.getInt();
            for (int i = 0; i < count; i++) {
                String name = getString(buf);
                KeyframeAnimation.StateCollection part = animation.getOrCreatePart(name);
                valid = readPart(buf, part, version, keyframeSize) && valid;
            }
        } else {
            valid = readPart(buf, animation.head, version, keyframeSize);
            valid = readPart(buf, animation.body, version, keyframeSize) && valid;
            valid = readPart(buf, animation.rightArm, version, keyframeSize) && valid;
            valid = readPart(buf, animation.leftArm, version, keyframeSize) && valid;
            valid = readPart(buf, animation.rightLeg, version, keyframeSize) && valid;
            valid = readPart(buf, animation.leftLeg, version, keyframeSize) && valid;
        }
        long msb = buf.getLong();
        long lsb = buf.getLong();
        animation.uuid = new UUID(msb, lsb);
        animation.extraData.put("valid", valid);

        return animation.build();
    }

    private static boolean readPart(ByteBuffer buf, KeyframeAnimation.StateCollection part, int version, int keyframeSize){
        boolean bl;
        bl = readKeyframes(buf, part.x, version, keyframeSize);
        bl = readKeyframes(buf, part.y, version, keyframeSize) && bl;
        bl = readKeyframes(buf, part.z, version, keyframeSize) && bl;
        bl = readKeyframes(buf, part.pitch, version, keyframeSize) && bl;
        bl = readKeyframes(buf, part.yaw, version, keyframeSize) && bl;
        bl = readKeyframes(buf, part.roll, version, keyframeSize) && bl;
        if(part.isBendable()) {
            bl = readKeyframes(buf, part.bendDirection, version, keyframeSize) && bl;
            bl = readKeyframes(buf, part.bend, version, keyframeSize) && bl;
        }
        if (part.isScalable() && version >= 3) {
            bl = readKeyframes(buf, part.scaleX, version, keyframeSize) && bl;
            bl = readKeyframes(buf, part.scaleY, version, keyframeSize) && bl;
            bl = readKeyframes(buf, part.scaleZ, version, keyframeSize) && bl;
        }
        return bl;
    }



    private static boolean readKeyframes(ByteBuffer buf, KeyframeAnimation.StateCollection.State part, int version, int keyframeSize) {
        int length;
        boolean valid = true;
        boolean enabled;
        if (version >= 2) {
            enabled = getBoolean(buf);
            length = buf.getInt();
        } else {
            length = buf.getInt();
            enabled = length >= 0;
        }
        for (int i = 0; i < length; i++) {
            int currentPos = buf.position();

            int tick = buf.getInt();
            float value = buf.getFloat();
            Ease ease = Ease.getEase(buf.get());
            Float easingArg = null;

            if (version >= 4) {
                easingArg = buf.getFloat();

                if (Float.isNaN(easingArg)) {
                    easingArg = null;
                }
            }

            if (!part.addKeyFrame(tick, value, ease, easingArg)) {
                valid = false;
            }
            buf.position(currentPos + keyframeSize);
        }
        part.setEnabled(enabled);
        return valid;
    }

    /**
     * Current animation binary version
     * @return version
     */
    public static int getCurrentVersion() {
        return 4;
    }



    public static int calculateSize(KeyframeAnimation animation, int version) {
        //I will create less efficient loops but these will be more easily fixable
        int size = 36;//The header makes xx bytes IIIBIBBBLL
        if (version < 2) {
            size += partSize(animation.getPart("head"), version);
            size += partSize(animation.getPart("body"), version);
            size += partSize(animation.getPart("rightArm"), version);
            size += partSize(animation.getPart("leftArm"), version);
            size += partSize(animation.getPart("rightLeg"), version);
            size += partSize(animation.getPart("leftLeg"), version);
        } else {
            size += 4;
            for (Map.Entry<String, KeyframeAnimation.StateCollection> entry : animation.getBodyParts().entrySet()) {
                size += stringSize(entry.getKey()) + partSize(entry.getValue(), version);
            }
        }
        //The size of an empty emote is 230 bytes.
        //but that makes the size to be 230 + keyframes count*9 bytes.
        //46 axis, including bends for every body-part except head.
        return size;
    }

    private static int stringSize(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return bytes.length + 4;
    }

    private static int partSize(@Nullable KeyframeAnimation.StateCollection part, int version){
        int size = 0;
        size += axisSize(part.x, version);
        size += axisSize(part.y, version);
        size += axisSize(part.z, version);
        size += axisSize(part.pitch, version);
        size += axisSize(part.yaw, version);
        size += axisSize(part.roll, version);
        if(part.isBendable) {
            size += axisSize(part.bend, version);
            size += axisSize(part.bendDirection, version);
        }
        if (part.isScalable && version >= 3) {
            size += axisSize(part.scaleX, version);
            size += axisSize(part.scaleY, version);
            size += axisSize(part.scaleZ, version);
        }
        return size;
    }

    private static int axisSize(KeyframeAnimation.StateCollection.State axis, int version){
        return axis.getKeyFrames().size()*keyframeSize(version) + (version >= 2 ? 5 : 4);// count*IFB + I (for count)
    }

    /**
     * Size needed for one keyframe
     */
    private static byte keyframeSize(int version) {
        return version < 4 ? (byte) 9 /* 4 (int) + 4 (float) + 1 (byte) */ : (byte) 13; /* + 4 (float) */
    }

    /**
     * Writes a bool value into byteBuffer, using 1 byte per bool
     * @param byteBuffer buf
     * @param bl         bool
     */
    public static void putBoolean(ByteBuffer byteBuffer, boolean bl){
        byteBuffer.put((byte) (bl ? 1 : 0));
    }

    /**
     * Reads a bool value from byteBuffer
     * @param buf buf
     * @return    bool
     */
    public static boolean getBoolean(ByteBuffer buf) {
        return buf.get() != (byte) 0;
    }

    /**
     * Writes a binary string into byteBuf
     * first 4 bytes for size, then string data
     * @param buf buf
     * @param str str
     */
    public static void putString(ByteBuffer buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.putInt(bytes.length);
        buf.put(bytes);
    }

    /**
     * Reads string from buf, see {@link AnimationBinary#putString(ByteBuffer, String)}
     * @param buf buf
     * @return str
     */
    public static String getString(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] bytes = new byte[len];
        buf.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
