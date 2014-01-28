package net.minecraft.client.mco;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class PendingInvitesList extends ValueObject
{
    public List field_148768_a = Lists.newArrayList();
    private static final String __OBFID = "CL_00001171";

    public static PendingInvitesList func_148767_a(String p_148767_0_)
    {
        PendingInvitesList pendinginviteslist = new PendingInvitesList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_148767_0_).getAsJsonObject();

            if (jsonobject.get("invites").isJsonArray())
            {
                Iterator iterator = jsonobject.get("invites").getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    pendinginviteslist.field_148768_a.add(PendingInvite.func_148773_a(((JsonElement)iterator.next()).getAsJsonObject()));
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

        return pendinginviteslist;
    }
}