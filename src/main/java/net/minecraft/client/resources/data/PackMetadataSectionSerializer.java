package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

@SideOnly(Side.CLIENT)
public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    private static final String __OBFID = "CL_00001113";

    public PackMetadataSection deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject jsonobject = par1JsonElement.getAsJsonObject();
        String s = JsonUtils.func_151200_h(jsonobject, "description");
        int i = JsonUtils.func_151203_m(jsonobject, "pack_format");
        return new PackMetadataSection(s, i);
    }

    public JsonElement serialize(PackMetadataSection par1PackMetadataSection, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("pack_format", Integer.valueOf(par1PackMetadataSection.getPackFormat()));
        jsonobject.addProperty("description", par1PackMetadataSection.getPackDescription());
        return jsonobject;
    }

    // JAVADOC METHOD $$ func_110483_a
    public String getSectionName()
    {
        return "pack";
    }

    public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        return this.serialize((PackMetadataSection)par1Obj, par2Type, par3JsonSerializationContext);
    }
}