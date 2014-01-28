package net.minecraft.client.mco;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class ValueObjectSubscription extends ValueObject
{
    public long field_148790_a;
    public int field_148789_b;
    private static final String __OBFID = "CL_00001172";

    public static ValueObjectSubscription func_148788_a(String p_148788_0_)
    {
        ValueObjectSubscription valueobjectsubscription = new ValueObjectSubscription();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_148788_0_).getAsJsonObject();
            valueobjectsubscription.field_148790_a = jsonobject.get("startDate").getAsLong();
            valueobjectsubscription.field_148789_b = jsonobject.get("daysLeft").getAsInt();
        }
        catch (JsonIOException jsonioexception)
        {
            ;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            ;
        }

        return valueobjectsubscription;
    }
}