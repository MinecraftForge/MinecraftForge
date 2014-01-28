package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

@SideOnly(Side.CLIENT)
public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    private static final String __OBFID = "CL_00001111";

    public LanguageMetadataSection deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject jsonobject = par1JsonElement.getAsJsonObject();
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = jsonobject.entrySet().iterator();
        String s;
        String s1;
        String s2;
        boolean flag;

        do
        {
            if (!iterator.hasNext())
            {
                return new LanguageMetadataSection(hashset);
            }

            Entry entry = (Entry)iterator.next();
            s = (String)entry.getKey();
            JsonObject jsonobject1 = JsonUtils.func_151210_l((JsonElement)entry.getValue(), "language");
            s1 = JsonUtils.func_151200_h(jsonobject1, "region");
            s2 = JsonUtils.func_151200_h(jsonobject1, "name");
            flag = JsonUtils.func_151209_a(jsonobject1, "bidirectional", false);

            if (s1.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + s + "\'->region: empty value");
            }

            if (s2.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + s + "\'->name: empty value");
            }
        }
        while (hashset.add(new Language(s, s1, s2, flag)));

        throw new JsonParseException("Duplicate language->\'" + s + "\' defined");
    }

    // JAVADOC METHOD $$ func_110483_a
    public String getSectionName()
    {
        return "language";
    }
}