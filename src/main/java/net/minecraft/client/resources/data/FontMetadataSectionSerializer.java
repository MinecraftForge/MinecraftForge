package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

@SideOnly(Side.CLIENT)
public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    private static final String __OBFID = "CL_00001109";

    public FontMetadataSection deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject jsonobject = par1JsonElement.getAsJsonObject();
        float[] afloat = new float[256];
        float[] afloat1 = new float[256];
        float[] afloat2 = new float[256];
        float f = 1.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;

        if (jsonobject.has("characters"))
        {
            if (!jsonobject.get("characters").isJsonObject())
            {
                throw new JsonParseException("Invalid font->characters: expected object, was " + jsonobject.get("characters"));
            }

            JsonObject jsonobject1 = jsonobject.getAsJsonObject("characters");

            if (jsonobject1.has("default"))
            {
                if (!jsonobject1.get("default").isJsonObject())
                {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonobject1.get("default"));
                }

                JsonObject jsonobject2 = jsonobject1.getAsJsonObject("default");
                f = JsonUtils.func_151221_a(jsonobject2, "width", f);
                Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f), "Invalid default width", new Object[0]);
                f1 = JsonUtils.func_151221_a(jsonobject2, "spacing", f1);
                Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f1), "Invalid default spacing", new Object[0]);
                f2 = JsonUtils.func_151221_a(jsonobject2, "left", f1);
                Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f2), "Invalid default left", new Object[0]);
            }

            for (int i = 0; i < 256; ++i)
            {
                JsonElement jsonelement1 = jsonobject1.get(Integer.toString(i));
                float f3 = f;
                float f4 = f1;
                float f5 = f2;

                if (jsonelement1 != null)
                {
                    JsonObject jsonobject3 = JsonUtils.func_151210_l(jsonelement1, "characters[" + i + "]");
                    f3 = JsonUtils.func_151221_a(jsonobject3, "width", f);
                    Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f3), "Invalid width", new Object[0]);
                    f4 = JsonUtils.func_151221_a(jsonobject3, "spacing", f1);
                    Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f4), "Invalid spacing", new Object[0]);
                    f5 = JsonUtils.func_151221_a(jsonobject3, "left", f2);
                    Validate.inclusiveBetween(Float.valueOf(0.0F), Float.valueOf(Float.MAX_VALUE), Float.valueOf(f5), "Invalid left", new Object[0]);
                }

                afloat[i] = f3;
                afloat1[i] = f4;
                afloat2[i] = f5;
            }
        }

        return new FontMetadataSection(afloat, afloat2, afloat1);
    }

    // JAVADOC METHOD $$ func_110483_a
    public String getSectionName()
    {
        return "font";
    }
}