package me.clickism.clickeventlib.serialization;

import com.google.gson.*;
import me.clickism.clickeventlib.location.SafeLocation;

import java.lang.reflect.Type;

/**
 * Serializes and deserializes safe locations to and from JSON.
 */
public class SafeLocationSerializer implements JsonSerializer<SafeLocation>, JsonDeserializer<SafeLocation> {
    /**
     * Creates a new safe location serializer.
     */
    public SafeLocationSerializer() {
    }

    @Override
    public JsonElement serialize(SafeLocation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        String worldName = src.getWorldName();
        json.addProperty("world", worldName);
        json.addProperty("x", src.getX());
        json.addProperty("y", src.getY());
        json.addProperty("z", src.getZ());
        if (src.getYaw() != 0) json.addProperty("yaw", src.getYaw());
        if (src.getPitch() != 0) json.addProperty("pitch", src.getPitch());
        return json;
    }

    @Override
    public SafeLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String worldName = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.has("yaw") ? jsonObject.get("yaw").getAsFloat() : 0;
        float pitch = jsonObject.has("pitch") ? jsonObject.get("pitch").getAsFloat() : 0;
        return new SafeLocation(worldName, x, y, z, yaw, pitch);
    }
}
