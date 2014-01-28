package net.minecraft.client.mco;

import com.google.gson.JsonArray;
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
public class ValueObjectList extends ValueObject
{
    public List field_148772_a;
    private static final String __OBFID = "CL_00001169";

    public static ValueObjectList func_148771_a(String p_148771_0_)
    {
        ValueObjectList valueobjectlist = new ValueObjectList();
        valueobjectlist.field_148772_a = new ArrayList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_148771_0_).getAsJsonObject();

            if (jsonobject.get("servers").isJsonArray())
            {
                JsonArray jsonarray = jsonobject.get("servers").getAsJsonArray();
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext())
                {
                    valueobjectlist.field_148772_a.add(McoServer.func_148802_a(((JsonElement)iterator.next()).getAsJsonObject()));
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

        return valueobjectlist;
    }
}