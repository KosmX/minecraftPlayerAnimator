package dev.kosmx.playerAnim.core.data.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.data.AnimationFormat;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Easing;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serialize movements as emotes from GeckoLib format
 * <a href="https://geckolib.com/">...</a>
 */
public class GeckoLibSerializer implements JsonDeserializer<List<KeyframeAnimation>> {
    @ApiStatus.Internal
    public static final Gson GSON;

    private static boolean readingTorsoBend = false;

    /**
     * TypeToken helper for serializing
     *
     * @return TypeToken for animation deserialization
     */
    @Deprecated
    @ApiStatus.Internal
    public static Type getListedTypeToken() {
        return new TypeToken<List<KeyframeAnimation>>() {}.getType();
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        GeckoLibSerializer gJson = new GeckoLibSerializer();
        builder.registerTypeAdapter(GeckoLibSerializer.getListedTypeToken(), gJson);
        builder.registerTypeAdapter(KeyframeAnimation.class, gJson);
        GSON = builder.create();
    }


    @Override
    public List<KeyframeAnimation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json.getAsJsonObject());
    }

    public static List<KeyframeAnimation> deserialize(JsonObject node){
        try {
            return readAnimations(node.get("animations").getAsJsonObject());
        } catch(NumberFormatException e) {
            throw new JsonParseException(e);
        }
    }

    private static List<KeyframeAnimation> readAnimations(JsonObject jsonEmotes){
        List<KeyframeAnimation> emotes = new ArrayList<>();
        jsonEmotes.entrySet().forEach(stringJsonElementEntry -> {
            KeyframeAnimation.AnimationBuilder builder = new KeyframeAnimation.AnimationBuilder(AnimationFormat.JSON_MC_ANIM);
            String name = stringJsonElementEntry.getKey();
            JsonObject node = stringJsonElementEntry.getValue().getAsJsonObject();
            builder.name = name;
            if (node.has("animation_length")){
                builder.endTick = (int) Math.ceil(node.get("animation_length").getAsFloat() * 20);
                if (node.has("loop")) {
                    builder.isLooped = node.get("loop").getAsJsonPrimitive().isBoolean() && node.get("loop").getAsBoolean();
                    if (!builder.isLooped && node.get("loop").getAsJsonPrimitive().isString() && node.get("loop").getAsString().equals("hold_on_last_frame")) {
                        builder.isLooped = true;
                        builder.returnTick = builder.endTick;
                    } else {
                        builder.endTick--;
                    }
                }
                builder.fullyEnableParts();
                builder.optimizeEmote();

                keyframeSerializer(builder, node.get("bones").getAsJsonObject());

            } else if (node.has("loop") && node.get("loop").getAsBoolean()) {
                builder.endTick = builder.stopTick = 1;
                builder.isLooped = true;
                builder.returnTick = 0;

                keyframeSerializer(builder, node.get("bones").getAsJsonObject());
            } else {
                throw new JsonParseException("Could not recognise GeckoLib animation: " + name);
            }

            emotes.add(builder.build());
        });
        return emotes;
    }

    private static void keyframeSerializer(KeyframeAnimation.AnimationBuilder emoteData, JsonObject node){
        for (Map.Entry<String, JsonElement> entry : node.entrySet()) {
            if (!entry.getKey().endsWith("_bend")) {
                readBone(emoteData.getOrCreatePart(snake2Camel(entry.getKey())), entry.getValue().getAsJsonObject(), emoteData);
            }
            else {
                String name = entry.getKey().replace("_bend", "");
                if (name.equals("torso")) {
                    name = "body";
                    readingTorsoBend = true;
                }
                readBoneBend(emoteData.getPart(snake2Camel(name)), entry.getValue().getAsJsonObject(), emoteData);
                readingTorsoBend = false;
            }
        }
    }

    private static void readBoneBend(KeyframeAnimation.StateCollection stateCollection, JsonObject node, KeyframeAnimation.AnimationBuilder emoteData) {
        if(node.has("rotation")){
            JsonElement jsonRotation = node.get("rotation");
            if(jsonRotation.isJsonArray()){
                readCollection(getTargetVec(stateCollection, TransformType.BEND), 0, Ease.LINEAR, jsonRotation.getAsJsonArray(), emoteData, TransformType.BEND);
            }
            else {
                jsonRotation.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getTargetVec(stateCollection, TransformType.BEND), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.BEND);
                    }
                    else if (!entry.getKey().equals("easing")) {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getTargetVec(stateCollection, TransformType.BEND), tick, Ease.CONSTANT, entry.getValue().getAsJsonArray(), emoteData, TransformType.BEND);
                        }
                        else {
                            readDataAtTick(entry.getValue().getAsJsonObject(), stateCollection, tick, emoteData, TransformType.BEND);
                        }
                    }
                });
            }
        }
    }

    private static void readBone(KeyframeAnimation.StateCollection stateCollection, JsonObject node, KeyframeAnimation.AnimationBuilder emoteData) {
        if(node.has("rotation")){
            JsonElement jsonRotation = node.get("rotation");
            if(jsonRotation.isJsonArray()){
                readCollection(getTargetVec(stateCollection, TransformType.ROTATION), 0, Ease.LINEAR, jsonRotation.getAsJsonArray(), emoteData, TransformType.ROTATION);
            }
            else {
                jsonRotation.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getTargetVec(stateCollection, TransformType.ROTATION), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.ROTATION);
                    }
                    else if (!entry.getKey().equals("easing")) {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getTargetVec(stateCollection, TransformType.ROTATION), tick, Ease.CONSTANT, entry.getValue().getAsJsonArray(), emoteData, TransformType.ROTATION);
                        }
                        else {
                            readDataAtTick(entry.getValue().getAsJsonObject(), stateCollection, tick, emoteData, TransformType.ROTATION);
                        }
                    }
                });
            }
        }
        if(node.has("position")){
            JsonElement jsonPosition = node.get("position");
            if(jsonPosition.isJsonArray()){
                readCollection(getTargetVec(stateCollection, TransformType.POSITION), 0, Ease.LINEAR, jsonPosition.getAsJsonArray(), emoteData, TransformType.POSITION);
            }
            else {
                jsonPosition.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getTargetVec(stateCollection, TransformType.POSITION), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.POSITION);
                    }else if (!entry.getKey().equals("easing")) {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getTargetVec(stateCollection, TransformType.POSITION), tick, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.POSITION);
                        }
                        else {
                            readDataAtTick(entry.getValue().getAsJsonObject(), stateCollection, tick, emoteData, TransformType.POSITION);
                        }
                    }
                });
            }
        }
        if(node.has("scale")){
            JsonElement jsonPosition = node.get("scale");
            if(jsonPosition.isJsonArray()){
                readCollection(getTargetVec(stateCollection, TransformType.SCALE), 0, Ease.LINEAR, jsonPosition.getAsJsonArray(), emoteData, TransformType.SCALE);
            }
            else {
                jsonPosition.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getTargetVec(stateCollection, TransformType.SCALE), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.SCALE);
                    }else if (!entry.getKey().equals("easing")) {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getTargetVec(stateCollection, TransformType.SCALE), tick, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData, TransformType.SCALE);
                        }
                        else {
                            readDataAtTick(entry.getValue().getAsJsonObject(), stateCollection, tick, emoteData, TransformType.SCALE);
                        }
                    }
                });
            }
        }
    }

    private static void readDataAtTick(JsonObject currentNode, KeyframeAnimation.StateCollection stateCollection, int tick, KeyframeAnimation.AnimationBuilder emoteData, TransformType type) {
        Ease ease = Ease.LINEAR;
        Float easingArg = null;
        if (currentNode.has("lerp_mode")) {
            ease = Easing.easeFromString(currentNode.get("lerp_mode").getAsString());
        }
        KeyframeAnimation.StateCollection.State[] targetVec = getTargetVec(stateCollection, type);
        if (currentNode.has("easing")) ease = Easing.easeFromString(currentNode.get("easing").getAsString());
        if (currentNode.has("easingArgs")) easingArg = currentNode.getAsJsonArray("easingArgs").get(0).getAsFloat();
        if (currentNode.has("pre"))
            readCollection(targetVec, tick, ease, easingArg, getVector(currentNode.get("pre")), emoteData, type);
        if (currentNode.has("vector"))
            readCollection(targetVec, tick, ease, easingArg, currentNode.get("vector").getAsJsonArray(), emoteData, type);
        if (currentNode.has("post"))
            readCollection(targetVec, tick, ease, easingArg, getVector(currentNode.get("post")), emoteData, type);
    }

    public static JsonArray getVector(JsonElement element) {
        if (element.isJsonArray()) return element.getAsJsonArray();
        else return ((JsonObject)element).get("vector").getAsJsonArray();
    }

    private static void readCollection(KeyframeAnimation.StateCollection.State[] a, int tick, Ease ease, JsonArray array, KeyframeAnimation.AnimationBuilder emoteData, TransformType type) {
        readCollection(a, tick, ease, null, array, emoteData, type);
    }

    private static void readCollection(KeyframeAnimation.StateCollection.State[] a, int tick, Ease ease, Float easingArg, JsonArray array, KeyframeAnimation.AnimationBuilder emoteData, TransformType type) {
        if (type != TransformType.BEND) {
            if (a.length != 3) throw new ArrayStoreException("wrong array length");
            for (int i = 0; i < 3; i++) {
                float value = array.get(i).getAsFloat();
                if (type == TransformType.POSITION) {
                    if (a[0] == emoteData.body.x) {
                        value = value / 16f;
                        if (i == 0) value = -value;
                    } else if (i == 1) {
                        value = -value;
                    }
                } else if (type == TransformType.ROTATION) {
                    if (a[0] == emoteData.body.pitch && i != 2) {
                        value = -value;
                    }
                }
                if (type != TransformType.SCALE)
                    value += a[i].defaultValue;
                a[i].addKeyFrame(tick, value, ease, 0, true, easingArg);
            }
        }
        else {
            if (a.length != 2) throw new ArrayStoreException("wrong array length");
            a[0].addKeyFrame(tick, array.get(0).getAsFloat(), ease, 0, true, easingArg);
            a[1].addKeyFrame(tick, array.get(1).getAsFloat() * (readingTorsoBend ? 1 : -1), ease, 0, true, easingArg);
        }
    }

    /**
     * Convert snake_case_string to camelCaseString
     * @param original string_to_convert
     * @return         camelCaseString
     */
    public static String snake2Camel(String original) {
        StringBuilder builder = new StringBuilder();
        StringReader reader = new StringReader(original);
        int c;
        boolean upperNext = false;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '_') {
                    upperNext = true;
                    continue;
                }
                if (upperNext) {
                    builder.appendCodePoint(Character.toUpperCase(c));
                } else {
                    builder.appendCodePoint(c);
                }
                upperNext = false;
            }
        } catch(IOException ignore) {
            return original;
        }
        return builder.toString();
    }

    public static KeyframeAnimation.StateCollection.State[] getTargetVec(KeyframeAnimation.StateCollection stateCollection, TransformType type){
        switch (type) {
            case POSITION:
                return new KeyframeAnimation.StateCollection.State[] {
                        stateCollection.x, stateCollection.y, stateCollection.z
                };

            case ROTATION:
                return new KeyframeAnimation.StateCollection.State[] {
                        stateCollection.pitch, stateCollection.yaw, stateCollection.roll
                };

            case SCALE:
                return new KeyframeAnimation.StateCollection.State[] {
                        stateCollection.scaleX, stateCollection.scaleY, stateCollection.scaleZ
                };

            case BEND:
                return new KeyframeAnimation.StateCollection.State[] {
                        stateCollection.bend, stateCollection.bendDirection
                };

            default:
                return new KeyframeAnimation.StateCollection.State[0];
        }
    }
}
