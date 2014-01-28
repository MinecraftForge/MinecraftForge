package net.minecraft.client.mco;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class McoServerAddress extends ValueObject
{
    public String field_148770_a;
    private static final String __OBFID = "CL_00001168";

    public static McoServerAddress func_148769_a(String p_148769_0_)
    {
        JsonParser jsonparser = new JsonParser();
        McoServerAddress mcoserveraddress = new McoServerAddress();

        try
        {
            JsonObject jsonobject = jsonparser.parse(p_148769_0_).getAsJsonObject();
            mcoserveraddress.field_148770_a = jsonobject.get("address").getAsString();
        }
        catch (JsonIOException jsonioexception)
        {
            ;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            ;
        }

        return mcoserveraddress;
    }
}