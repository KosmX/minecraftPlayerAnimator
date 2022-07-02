package dev.kosmx.playerAnim.core.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Easing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serialize movements as emotes from GeckoLib format
 * <a href="https://geckolib.com/">...</a>
 */
public class GeckoLibSerializer {
    public static List<KeyframeAnimation> serialize(JsonObject node){
        return readAnimations(node.get("animations").getAsJsonObject());
    }

    private static List<KeyframeAnimation> readAnimations(JsonObject jsonEmotes){
        List<KeyframeAnimation> emotes = new ArrayList<>();
        jsonEmotes.entrySet().forEach(stringJsonElementEntry -> {
            KeyframeAnimation.AnimationBuilder builder = new KeyframeAnimation.AnimationBuilder(AnimationFormat.JSON_MC_ANIM);
            String name = stringJsonElementEntry.getKey();
            JsonObject node = stringJsonElementEntry.getValue().getAsJsonObject();
            builder.endTick = (int) Math.ceil(node.get("animation_length").getAsFloat() * 20);
            if(node.has("loop")){
                builder.isLooped = node.get("loop").getAsJsonPrimitive().isBoolean() && node.get("loop").getAsBoolean();
                if(!builder.isLooped && node.get("loop").getAsJsonPrimitive().isString() && node.get("loop").getAsString().equals("hold_on_last_frame")){
                    builder.isLooped = true;
                    builder.returnTick = builder.endTick;
                }
            }
            builder.fullyEnableParts();
            builder.optimizeEmote();
            builder.name = name;

            keyframeSerializer(builder, node.get("bones").getAsJsonObject());
            KeyframeAnimation emoteData = builder.build();
            //EmoteHolder emoteHolder = new EmoteHolder(emoteData, name, EmoteInstance.instance.getDefaults().textFromString("").formatted(EmotesTextFormatting.YELLOW), EmoteInstance.instance.getDefaults().emptyTex(), node.hashCode());
            //emoteHolder.isFromGeckoLib = true;
            emotes.add(emoteData);
        });
        return emotes;
    }

    private static void keyframeSerializer(KeyframeAnimation.AnimationBuilder emoteData, JsonObject node){
        for (Map.Entry<String, JsonElement> entry : node.entrySet()) {
            readBone(emoteData.getOrCreatePart(snake2Camel(entry.getKey())), entry.getValue().getAsJsonObject(), emoteData);
        }
    }

    private static void readBone(KeyframeAnimation.StateCollection stateCollection, JsonObject node, KeyframeAnimation.AnimationBuilder emoteData){
        if(node.has("rotation")){
            JsonElement jsonRotation = node.get("rotation");
            if(jsonRotation.isJsonArray()){
                readCollection(getRots(stateCollection), 0, Ease.LINEAR, jsonRotation.getAsJsonArray(), emoteData);
            }
            else {
                jsonRotation.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getRots(stateCollection), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData);
                    }
                    else {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getRots(stateCollection), tick, Ease.CONSTANT, entry.getValue().getAsJsonArray(), emoteData);
                        }
                        else {
                            Ease ease = Ease.LINEAR;
                            JsonObject currentNode = entry.getValue().getAsJsonObject();
                            if (currentNode.has("lerp_mode")) {
                                String lerp = currentNode.get("lerp_mode").getAsString();
                                ease = lerp.equals("catmullrom") ? Ease.INOUTSINE : Easing.easeFromString(lerp); //IDK what am I doing
                            }
                            if (currentNode.has("easing")) ease = Easing.easeFromString(currentNode.get("easing").getAsString());
                            if (currentNode.has("pre"))
                                readCollection(getRots(stateCollection), tick, ease, currentNode.get("pre").getAsJsonArray(), emoteData);
                            if (currentNode.has("vector"))
                                readCollection(getRots(stateCollection), tick, ease, currentNode.get("vector").getAsJsonArray(), emoteData);
                            if (currentNode.has("post"))
                                readCollection(getRots(stateCollection), tick, ease, currentNode.get("post").getAsJsonArray(), emoteData);
                        }
                    }
                });
            }
        }
        if(node.has("position")){
            JsonElement jsonPosition = node.get("position");
            if(jsonPosition.isJsonArray()){
                readCollection(getOffs(stateCollection), 0, Ease.LINEAR, jsonPosition.getAsJsonArray(), emoteData);
            }
            else {
                jsonPosition.getAsJsonObject().entrySet().forEach(entry -> {
                    if(entry.getKey().equals("vector")){
                        readCollection(getOffs(stateCollection), 0, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData);
                    }else {
                        int tick = (int) (Float.parseFloat(entry.getKey()) * 20);
                        if (entry.getValue().isJsonArray()) {
                            readCollection(getOffs(stateCollection), tick, Ease.LINEAR, entry.getValue().getAsJsonArray(), emoteData);
                        }
                        else {
                            Ease ease = Ease.LINEAR;
                            JsonObject currentNode = entry.getValue().getAsJsonObject();
                            if (currentNode.has("lerp_mode")) {
                                String lerp = currentNode.get("lerp_mode").getAsString();
                                ease = lerp.equals("catmullrom") ? Ease.INOUTSINE : Easing.easeFromString(lerp); //IDK what am I doing
                            }
                            if (currentNode.has("easing")) ease = Easing.easeFromString(currentNode.get("easing").getAsString());
                            if (currentNode.has("pre"))
                                readCollection(getOffs(stateCollection), tick, ease, currentNode.get("pre").getAsJsonArray(), emoteData);
                            if (currentNode.has("vector"))
                                readCollection(getOffs(stateCollection), tick, ease, currentNode.get("vector").getAsJsonArray(), emoteData);
                            if (currentNode.has("post"))
                                readCollection(getOffs(stateCollection), tick, ease, currentNode.get("post").getAsJsonArray(), emoteData);
                        }
                    }
                });
            }
        }
    }

    private static void readCollection(KeyframeAnimation.StateCollection.State[] a, int tick, Ease ease, JsonArray array, KeyframeAnimation.AnimationBuilder emoteData){
        if(a.length != 3)throw new ArrayStoreException("wrong array length");
        for(int i = 0; i < 3; i++){
            float value = array.get(i).getAsFloat();
            if(a[0] == emoteData.body.x) value = value / 16f;
            else if(a[0] == emoteData.body.pitch) value = -value;
            value += a[i].defaultValue;
            a[i].addKeyFrame(tick, value, ease, 0, true);
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
                    builder.append(Character.toUpperCase(c));
                } else {
                    builder.append(c);
                }
                upperNext = false;
            }
        } catch(IOException ignore) {
            return original;
        }
        return builder.toString();
    }

    private static KeyframeAnimation.StateCollection.State[] getRots(KeyframeAnimation.StateCollection stateCollection){
        return new KeyframeAnimation.StateCollection.State[] {stateCollection.pitch, stateCollection.yaw, stateCollection.roll};
    }

    private static KeyframeAnimation.StateCollection.State[] getOffs(KeyframeAnimation.StateCollection stateCollection){
        return new KeyframeAnimation.StateCollection.State[] {stateCollection.x, stateCollection.y, stateCollection.z};
    }

}
