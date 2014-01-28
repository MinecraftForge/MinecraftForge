package net.minecraft.client.mco;

import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class WorldTemplate extends ValueObject
{
    public String field_148787_a;
    public String field_148785_b;
    public String field_148786_c;
    public String field_148784_d;
    private static final String __OBFID = "CL_00001174";

    public static WorldTemplate func_148783_a(JsonObject p_148783_0_)
    {
        WorldTemplate worldtemplate = new WorldTemplate();

        try
        {
            worldtemplate.field_148787_a = p_148783_0_.get("id").getAsString();
            worldtemplate.field_148785_b = p_148783_0_.get("name").getAsString();
            worldtemplate.field_148786_c = p_148783_0_.get("version").getAsString();
            worldtemplate.field_148784_d = p_148783_0_.get("author").getAsString();
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return worldtemplate;
    }
}