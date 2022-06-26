package dev.kosmx.playerAnim.core.data;

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

    private final String extension;


    AnimationFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
