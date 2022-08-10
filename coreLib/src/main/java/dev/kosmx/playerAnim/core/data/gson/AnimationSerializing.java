package dev.kosmx.playerAnim.core.data.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

import java.io.*;
import java.util.List;

/**
 * (De)Serialize {@link KeyframeAnimation}
 * Can load emotecraft and basic gecko-lib animations
 * always create emotecraft animation
 * <p>
 * Use {@link AnimationSerializing#deserializeAnimation(Reader)} to deserialize<br>
 * or {@link AnimationSerializing#serializeAnimation(KeyframeAnimation)} to serialize.
 */
public class AnimationSerializing {

    /**
     * Static initialized serializer instance for Emotecraft and GeckoLib animation json
     */
    public static final Gson SERIALIZER;

    /**
     * Deserialize animations from Emotecraft or GeckoLib InputStreamReader
     * @param stream inputStreamReader
     * @return List of animations
     */
    public static List<KeyframeAnimation> deserializeAnimation(Reader stream) {
        return SERIALIZER.fromJson(stream, AnimationJson.getListedTypeToken());
    }

    /**
     * Deserialize animations from Emotecraft or GeckoLib InputStream
     * @param stream inputStream
     * @return List of animations
     */
    public static List<KeyframeAnimation> deserializeAnimation(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return deserializeAnimation(reader);
        }
    }

    //Emotecraft binary is an emotecraft-specific thing.

    /**
     * Serialize animation into Emotecraft JSON format
     * @param animation animation
     * @return string
     */
    public static String serializeAnimation(KeyframeAnimation animation) {
         return SERIALIZER.toJson(animation, KeyframeAnimation.class);
    }

    /**
     * Write the animation to output stream
     * @param animation animation
     * @param writer    writer
     * @return writer
     * @throws IOException writer errors
     */
    public static Writer writeAnimation(KeyframeAnimation animation, Writer writer) throws IOException {
        writer.write(serializeAnimation(animation));
        return writer;
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        AnimationJson animationJson = new AnimationJson();
        builder.registerTypeAdapter(AnimationJson.getListedTypeToken(), animationJson);
        builder.registerTypeAdapter(KeyframeAnimation.class, animationJson);
        SERIALIZER = builder.create();
    }
}
