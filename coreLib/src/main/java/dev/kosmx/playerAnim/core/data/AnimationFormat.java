package dev.kosmx.playerAnim.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Where is the emote from
 */
public enum AnimationFormat {
    JSON_EMOTECRAFT("json"),
    JSON_MC_ANIM("json"),
    QUARK("emote"),
    BINARY("emotecraft"),
    SERVER(null),
    UNKNOWN(null);

    private static final Map<String, AnimationFormat> FORMATS;

    static {
        AnimationFormat[] formatsValues = values();

        FORMATS = new HashMap<>(formatsValues.length);

        for (AnimationFormat format : formatsValues) {
            if (format.extension != null) 
                FORMATS.putIfAbsent(format.extension, format);
        }
    }

    public static AnimationFormat byFileName(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileName = fileName.substring(i + 1);
        }

        return byExtension(fileName);
    }

    public static AnimationFormat byExtension(String extension) {
        return FORMATS.getOrDefault(extension, AnimationFormat.UNKNOWN);
    }

    private final String extension;

    AnimationFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
