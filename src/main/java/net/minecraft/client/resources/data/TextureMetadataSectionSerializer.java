package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;

@SideOnly(Side.CLIENT)
public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    private static final String __OBFID = "CL_00001115";

    public TextureMetadataSection deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject jsonobject = par1JsonElement.getAsJsonObject();
        boolean flag = JsonUtils.func_151209_a(jsonobject, "blur", false);
        boolean flag1 = JsonUtils.func_151209_a(jsonobject, "clamp", false);
        ArrayList arraylist = Lists.newArrayList();

        if (jsonobject.has("mipmaps"))
        {
            try
            {
                JsonArray jsonarray = jsonobject.getAsJsonArray("mipmaps");

                for (int i = 0; i < jsonarray.size(); ++i)
                {
                    JsonElement jsonelement1 = jsonarray.get(i);

                    if (jsonelement1.isJsonPrimitive())
                    {
                        try
                        {
                            arraylist.add(Integer.valueOf(jsonelement1.getAsInt()));
                        }
                        catch (NumberFormatException numberformatexception)
                        {
                            throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement1, numberformatexception);
                        }
                    }
                    else if (jsonelement1.isJsonObject())
                    {
                        throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement1);
                    }
                }
            }
            catch (ClassCastException classcastexception)
            {
                throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonobject.get("mipmaps"), classcastexception);
            }
        }

        return new TextureMetadataSection(flag, flag1, arraylist);
    }

    // JAVADOC METHOD $$ func_110483_a
    public String getSectionName()
    {
        return "texture";
    }
}