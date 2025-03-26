package dev.kosmx.playerAnim.core.data.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.kosmx.playerAnim.core.data.AnimationFormat;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Easing;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class AnimationJson implements JsonDeserializer<List<KeyframeAnimation>>, JsonSerializer<KeyframeAnimation> {


    private final static int modVersion = 3;

    @ApiStatus.Internal
    public static final Gson GSON;

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
        AnimationJson animationJson = new AnimationJson();
        builder.registerTypeAdapter(AnimationJson.getListedTypeToken(), animationJson);
        builder.registerTypeAdapter(KeyframeAnimation.class, animationJson);
        GSON = builder.create();
    }

    /**
     * I think, we can stick with this <i>legacy</i> name: <code>emote</code>
     */
    @Override
    public List<KeyframeAnimation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject node = json.getAsJsonObject();

        if(!node.has("emote")){
            return GeckoLibSerializer.deserialize(node); // TODO remove
        }

        int version = 1;
        if(node.has("version")) version = node.get("version").getAsInt();
        //Text author = EmoteInstance.instance.getDefaults().emptyTex();
        KeyframeAnimation.AnimationBuilder emote = emoteDeserializer(node.getAsJsonObject("emote"), version);


        //Text name = EmoteInstance.instance.getDefaults().fromJson(node.get("name"));

        for (Map.Entry<String, JsonElement> entry : node.entrySet()) {
            String string = entry.getKey();
            if(string.equals("uuid") || string.equals("comment") || string.equals("version")) continue;
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                JsonPrimitive p = value.getAsJsonPrimitive();
                if (p.isBoolean()) {
                    emote.extraData.put(string, p.getAsBoolean());
                } else if (p.isString()) {
                    emote.extraData.put(string, p.getAsString());
                } else if (p.isNumber()) {
                    emote.extraData.put(string, p.getAsDouble());
                } else emote.extraData.put(string, p.toString()); //Best solution ever :D
            }

        }

        emote.name = node.get("name").toString();
        if(node.has("author")){
            emote.author = node.get("author").toString();
        }

        if(node.has("description")){
            emote.description = node.get("description").toString();
        }

        if(modVersion < version){
            throw new JsonParseException(emote.name + " is version " + version + ". PlayerAnimator library can only process version " + modVersion + ".");
        }


        if(node.has("uuid")){
            emote.uuid = UUID.fromString(node.get("uuid").getAsString());
        }

        emote.optimizeEmote();
        List<KeyframeAnimation> list = new ArrayList<>();
        list.add(emote.build());
        return list;
    }

    private KeyframeAnimation.AnimationBuilder emoteDeserializer(JsonObject node, int version) throws JsonParseException{
        KeyframeAnimation.AnimationBuilder builder = new KeyframeAnimation.AnimationBuilder(AnimationFormat.JSON_EMOTECRAFT);
        if(node.has("beginTick")){
            builder.beginTick = node.get("beginTick").getAsInt();
        }
        builder.endTick = node.get("endTick").getAsInt();
        if(builder.endTick <= 0) throw new JsonParseException("endTick must be bigger than 0");
        if(node.has("isLoop") && node.has("returnTick")){
            builder.isLooped = node.get("isLoop").getAsBoolean();
            builder.returnTick = node.get("returnTick").getAsInt();
            if(builder.isLooped && (builder.returnTick > builder.endTick || builder.returnTick < 0))
                throw new JsonParseException("return tick have to be smaller than endTick and not smaller than 0");
        }

        if(node.has("nsfw")){
            builder.nsfw = node.get("nsfw").getAsBoolean();
        }


        builder.stopTick = node.has("stopTick") ? node.get("stopTick").getAsInt() : builder.endTick;
        boolean degrees = ! node.has("degrees") || node.get("degrees").getAsBoolean();
        //KeyframeAnimation emote = new KeyframeAnimation(beginTick, endTick, resetTick, isLoop, returnTick);
        if(node.has("easeBeforeKeyframe"))builder.isEasingBefore = node.get("easeBeforeKeyframe").getAsBoolean();
        moveDeserializer(builder, node.getAsJsonArray("moves"), degrees, version);

        // builder.fullyEnableParts();

        return builder;
    }

    private void moveDeserializer(KeyframeAnimation.AnimationBuilder emote, JsonArray node, boolean degrees, int version){
        for(JsonElement n : node){
            JsonObject obj = n.getAsJsonObject();
            int tick = obj.get("tick").getAsInt();
            String easing = obj.has("easing") ? obj.get("easing").getAsString() : "linear";
            Float easingArg = null;
            try {
                if (obj.has("easingArg")) {
                    easingArg = obj.get("easingArg").getAsFloat();
                }
            }
            catch (NullPointerException ignore) {}
            int turn = obj.has("turn") ? obj.get("turn").getAsInt() : 0;
            for(Map.Entry<String, JsonElement> entry:obj.entrySet()){
                if(entry.getKey().equals("tick") || entry.getKey().equals("comment") || entry.getKey().equals("easing") || entry.getKey().equals("turn")){
                    continue;
                }
                addBodyPartIfExists(emote, entry.getKey(), entry.getValue(), degrees, tick, easing, easingArg, turn, version);
            }
        }
    }

    private void addBodyPartIfExists(KeyframeAnimation.AnimationBuilder emote, String name, JsonElement node, boolean degrees, int tick, String easing, int turn, int version){
        addBodyPartIfExists(emote, name, node, degrees, tick, easing, null, turn, version);
    }

    private void addBodyPartIfExists(KeyframeAnimation.AnimationBuilder emote, String name, JsonElement node, boolean degrees, int tick, String easing, Float easingArg, int turn, int version) {
        if(version < 3 && name.equals("torso"))name = "body";// rename part
        KeyframeAnimation.StateCollection part = emote.getOrCreatePart(name);
        JsonObject partNode = node.getAsJsonObject();

        addPartIfExists(part.x, "x", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.y, "y", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.z, "z", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.pitch, "pitch", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.yaw, "yaw", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.roll, "roll", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.scaleX, "scaleX", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.scaleY, "scaleY", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.scaleZ, "scaleZ", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.bend, "bend", partNode, degrees, tick, easing, turn);
        addPartIfExists(part.bendDirection, "axis", partNode, degrees, tick, easing, turn);
    }

    private void addPartIfExists(KeyframeAnimation.StateCollection.State part, String name, JsonObject node, boolean degrees, int tick, String easing, int turn){
        addPartIfExists(part, name, node, degrees, tick, easing, null, turn);
    }

    private void addPartIfExists(KeyframeAnimation.StateCollection.State part, String name, JsonObject node, boolean degrees, int tick, String easing, Float easingArg, int turn){
        if(node.has(name)){
            part.addKeyFrame(tick, node.get(name).getAsFloat(), Easing.easeFromString(easing), turn, degrees, easingArg);
        }
    }

    /**
     * Animation to emotecraft format JSON
     * tries to serialize extraData as long as it is string/number/bool/json.     *
     *
     * @param emote source KeyframeAnimation
     * @param typeOfSrc idk
     * @param context :)
     * @return :D
     * Sorry for these really... useful comments
     */
    @Override
    public JsonElement serialize(KeyframeAnimation emote, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject node = new JsonObject();
        KeyframeAnimation.StateCollection torso = emote.getPart("torso");
        if (torso != null && torso.isEnabled()) {
            node.addProperty("version", 3); //to make compatible emotes.
        } else {
            node.addProperty("version", emote.isEasingBefore ? 2 : 1); //to make compatible emotes.
        }
        emote.extraData.forEach((s, o) -> {
            if (o instanceof String) {
                String s1 = (String) o;
                try {
                    node.add(s, asJson(s1));
                } catch (Throwable th) {
                    node.addProperty(s, s1);
                }
            } else if (o instanceof Number) {
                node.addProperty(s, (Number) o);
            } else if (o instanceof Boolean) {
                node.addProperty(s, (boolean) o);
            } else if (o instanceof JsonElement) {
                node.add(s, (JsonElement)o);
            }
        });
        node.add("emote", emoteSerializer(emote));
        return node;
    }

    public static JsonElement asJson(String str){
        return JsonParser.parseString(str);
    }

    /**
     * serialize an emote to json
     * It won't be the same json file (not impossible) but multiple jsons can mean the same emote...
     * <p>
     * Oh, and it's public and static, so you can call it from anywhere.
     *
     * @param emote Emote to serialize
     * @return return Json object
     */
    public static JsonObject emoteSerializer(KeyframeAnimation emote){
        JsonObject node = new JsonObject();
        node.addProperty("beginTick", emote.beginTick);
        node.addProperty("endTick", emote.endTick);
        node.addProperty("stopTick", emote.stopTick);
        node.addProperty("isLoop", emote.isInfinite);
        node.addProperty("returnTick", emote.returnToTick);
        node.addProperty("nsfw", emote.nsfw);
        node.addProperty("degrees", false); //No program uses degrees.
        if(emote.isEasingBefore)node.addProperty("easeBeforeKeyframe", true);
        node.add("moves", moveSerializer(emote));
        return node;
    }

    public static JsonArray moveSerializer(KeyframeAnimation emote){
        JsonArray node = new JsonArray();
        emote.getBodyParts().forEach((s, stateCollection) -> bodyPartSerializer(node, stateCollection, s));
        return node;
    }

    /*
     * from here and below the methods are not public
     * these are really depend on the upper method, and I don't think anyone will use these.
     */
    @SuppressWarnings("ConstantConditions")
    private static void bodyPartSerializer(JsonArray node, KeyframeAnimation.StateCollection bodyPart, String partName){
        partSerialize(node, bodyPart.x, partName);
        partSerialize(node, bodyPart.y, partName);
        partSerialize(node, bodyPart.z, partName);
        partSerialize(node, bodyPart.pitch, partName);
        partSerialize(node, bodyPart.yaw, partName);
        partSerialize(node, bodyPart.roll, partName);
        if(bodyPart.isBendable) {
            partSerialize(node, bodyPart.bend, partName);
            partSerialize(node, bodyPart.bendDirection, partName);
        }
        if(bodyPart.isScalable) {
            partSerialize(node, bodyPart.scaleX, partName);
            partSerialize(node, bodyPart.scaleY, partName);
            partSerialize(node, bodyPart.scaleZ, partName);
        }
    }

    private static void partSerialize(JsonArray array, KeyframeAnimation.StateCollection.State part, String partName){
        for(KeyframeAnimation.KeyFrame keyFrame : part.getKeyFrames()){
            JsonObject node = new JsonObject();
            node.addProperty("tick", keyFrame.tick);
            node.addProperty("easing", keyFrame.ease.toString());
            if (keyFrame.easingArg != null) {
                node.addProperty("easingArg", keyFrame.easingArg);
            }
            JsonObject jsonMove = new JsonObject();
            jsonMove.addProperty(part.name, keyFrame.value);
            node.add(partName, jsonMove);
            array.add(node);
        }
    }
}
