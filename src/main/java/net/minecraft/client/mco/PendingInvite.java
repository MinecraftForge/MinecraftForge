package net.minecraft.client.mco;

import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class PendingInvite extends ValueObject
{
    public String field_148776_a;
    public String field_148774_b;
    public String field_148775_c;
    private static final String __OBFID = "CL_00001170";

    public static PendingInvite func_148773_a(JsonObject p_148773_0_)
    {
        PendingInvite pendinginvite = new PendingInvite();

        try
        {
            pendinginvite.field_148776_a = p_148773_0_.get("invitationId").getAsString();
            pendinginvite.field_148774_b = p_148773_0_.get("worldName").getAsString();
            pendinginvite.field_148775_c = p_148773_0_.get("worldOwnerName").getAsString();
        }
        catch (Exception exception)
        {
            ;
        }

        return pendinginvite;
    }
}