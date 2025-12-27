package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class AnimationCodecs {
    private static final Logger logger = LoggerFactory.getLogger(AnimationCodecs.class);
    public static final AnimationCodecs INSTANCE = new AnimationCodecs();
    private AnimationCodecs() {}

    private final List<AnimationCodec<?>> codecs = new ArrayList<>();

    public void registerCodec(AnimationCodec<?> codec) {
        codecs.add(codec);
    }

    /**
     * Return decoder(s) for the given file extension.
     * An extension might have more decoders, like .json might be emotecraft or bedrock
     * @param fileExtension extension without the dot (.) like <code>json</code>
     * @return stream, the stream might be empty.
     */
    @NotNull
    public List<AnimationCodec<?>> getCodec(@NotNull String fileExtension) {
        return codecs.stream().filter(it -> Objects.equals(it.getExtension(), fileExtension)).toList();
    }

    /**
     *
     * @return list of all available codecs. Do not modify the list, use {@link AnimationCodecs#registerCodec(AnimationCodec)} to add stuff.
     */
    @NotNull
    public List<AnimationCodec<?>> getAllCodecs() {
        return codecs;
    }


    public static @Nullable String getExtension(@NotNull String filename) {
        if (filename.isEmpty()) return null;

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            filename = filename.substring(i + 1);
        }
        return filename;
    }

    @NotNull
    @ApiStatus.Experimental
    public static Collection<IPlayable> deserialize(@Nullable String extension, @NotNull InputStream inputStream) throws IOException {
        var data = inputStream.readAllBytes(); // I might need to try multiple times, don't break the input stream
        try {
            inputStream.close();
        } catch (Exception ignored) {}
        return deserialize(extension, () -> new ByteArrayInputStream(data));
    }

    @NotNull
    public static Collection<IPlayable> deserialize(@Nullable String extension, @NotNull Supplier<InputStream> inputStreamSupplier) {

        List<IPlayable> animations = new LinkedList<>();

        for (AnimationCodec<?> deserializer: extension == null ? AnimationCodecs.INSTANCE.getAllCodecs() : AnimationCodecs.INSTANCE.getCodec(extension)) {
            try (var reader = inputStreamSupplier.get()) {
                if (reader == null) break;
                final var result = deserializer.decode(new BufferedInputStream(reader));

                animations.addAll(result);
                break;

            } catch (IOException e) {
                // this is normal to happen
                logger.info(String.format("Failed to apply %s", deserializer.getFormatName()), e);
            } catch (Exception e) {
                logger.error(String.format("Unknown error when trying to apply %s", deserializer.getFormatName()), e);
            }
        }
        return animations;
    }

    static {
        INSTANCE.registerCodec(EmotecraftGsonCodec.INSTANCE);
        INSTANCE.registerCodec(LegacyGeckoJsonCodec.INSTANCE);
    }
}
