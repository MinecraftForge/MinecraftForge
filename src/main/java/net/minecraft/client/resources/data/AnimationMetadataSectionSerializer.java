package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

@SideOnly(Side.CLIENT)
public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    private static final String __OBFID = "CL_00001107";

    public AnimationMetadataSection deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        ArrayList arraylist = Lists.newArrayList();
        JsonObject jsonobject = JsonUtils.func_151210_l(par1JsonElement, "metadata section");
        int i = JsonUtils.func_151208_a(jsonobject, "frametime", 1);

        if (i != 1)
        {
            Validate.inclusiveBetween(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(i), "Invalid default frame time", new Object[0]);
        }

        int j;

        if (jsonobject.has("frames"))
        {
            try
            {
                JsonArray jsonarray = JsonUtils.func_151214_t(jsonobject, "frames");

                for (j = 0; j < jsonarray.size(); ++j)
                {
                    JsonElement jsonelement1 = jsonarray.get(j);
                    AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement1);

                    if (animationframe != null)
                    {
                        arraylist.add(animationframe);
                    }
                }
            }
            catch (ClassCastException classcastexception)
            {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"), classcastexception);
            }
        }

        int k = JsonUtils.func_151208_a(jsonobject, "width", -1);
        j = JsonUtils.func_151208_a(jsonobject, "height", -1);

        if (k != -1)
        {
            Validate.inclusiveBetween(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(k), "Invalid width", new Object[0]);
        }

        if (j != -1)
        {
            Validate.inclusiveBetween(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(j), "Invalid height", new Object[0]);
        }

        return new AnimationMetadataSection(arraylist, k, j, i);
    }

    private AnimationFrame parseAnimationFrame(int par1, JsonElement par2JsonElement)
    {
        if (par2JsonElement.isJsonPrimitive())
        {
            return new AnimationFrame(JsonUtils.func_151215_f(par2JsonElement, "frames[" + par1 + "]"));
        }
        else if (par2JsonElement.isJsonObject())
        {
            JsonObject jsonobject = JsonUtils.func_151210_l(par2JsonElement, "frames[" + par1 + "]");
            int j = JsonUtils.func_151208_a(jsonobject, "time", -1);

            if (jsonobject.has("time"))
            {
                Validate.inclusiveBetween(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(j), "Invalid frame time", new Object[0]);
            }

            int k = JsonUtils.func_151203_m(jsonobject, "index");
            Validate.inclusiveBetween(Integer.valueOf(0), Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(k), "Invalid frame index", new Object[0]);
            return new AnimationFrame(k, j);
        }
        else
        {
            return null;
        }
    }

    public JsonElement serialize(AnimationMetadataSection par1AnimationMetadataSection, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("frametime", Integer.valueOf(par1AnimationMetadataSection.getFrameTime()));

        if (par1AnimationMetadataSection.getFrameWidth() != -1)
        {
            jsonobject.addProperty("width", Integer.valueOf(par1AnimationMetadataSection.getFrameWidth()));
        }

        if (par1AnimationMetadataSection.getFrameHeight() != -1)
        {
            jsonobject.addProperty("height", Integer.valueOf(par1AnimationMetadataSection.getFrameHeight()));
        }

        if (par1AnimationMetadataSection.getFrameCount() > 0)
        {
            JsonArray jsonarray = new JsonArray();

            for (int i = 0; i < par1AnimationMetadataSection.getFrameCount(); ++i)
            {
                if (par1AnimationMetadataSection.frameHasTime(i))
                {
                    JsonObject jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("index", Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(i)));
                    jsonobject1.addProperty("time", Integer.valueOf(par1AnimationMetadataSection.getFrameTimeSingle(i)));
                    jsonarray.add(jsonobject1);
                }
                else
                {
                    jsonarray.add(new JsonPrimitive(Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(i))));
                }
            }

            jsonobject.add("frames", jsonarray);
        }

        return jsonobject;
    }

    // JAVADOC METHOD $$ func_110483_a
    public String getSectionName()
    {
        return "animation";
    }

    public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        return this.serialize((AnimationMetadataSection)par1Obj, par2Type, par3JsonSerializationContext);
    }
}