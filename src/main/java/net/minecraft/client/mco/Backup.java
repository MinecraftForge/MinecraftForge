package net.minecraft.client.mco;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Date;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class Backup extends ValueObject
{
    public String field_148780_a;
    public Date field_148778_b;
    public long field_148779_c;
    private static final String __OBFID = "CL_00001164";

    public static Backup func_148777_a(JsonElement p_148777_0_)
    {
        JsonObject jsonobject = p_148777_0_.getAsJsonObject();
        Backup backup = new Backup();

        try
        {
            backup.field_148780_a = jsonobject.get("backupId").getAsString();
            backup.field_148778_b = new Date(Long.parseLong(jsonobject.get("lastModifiedDate").getAsString()));
            backup.field_148779_c = Long.parseLong(jsonobject.get("size").getAsString());
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return backup;
    }
}