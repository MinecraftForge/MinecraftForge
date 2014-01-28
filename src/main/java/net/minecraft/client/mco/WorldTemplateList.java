package net.minecraft.client.mco;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class WorldTemplateList extends ValueObject
{
    public List field_148782_a;
    private static final String __OBFID = "CL_00001175";

    public static WorldTemplateList func_148781_a(String p_148781_0_)
    {
        WorldTemplateList worldtemplatelist = new WorldTemplateList();
        worldtemplatelist.field_148782_a = new ArrayList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_148781_0_).getAsJsonObject();

            if (jsonobject.get("templates").isJsonArray())
            {
                Iterator iterator = jsonobject.get("templates").getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    worldtemplatelist.field_148782_a.add(WorldTemplate.func_148783_a(((JsonElement)iterator.next()).getAsJsonObject()));
                }
            }
        }
        catch (JsonIOException jsonioexception)
        {
            ;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            ;
        }

        return worldtemplatelist;
    }
}