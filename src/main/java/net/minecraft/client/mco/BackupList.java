package net.minecraft.client.mco;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BackupList
{
    public List field_148797_a;
    private static final String __OBFID = "CL_00001165";

    public static BackupList func_148796_a(String p_148796_0_)
    {
        JsonParser jsonparser = new JsonParser();
        BackupList backuplist = new BackupList();
        backuplist.field_148797_a = new ArrayList();

        try
        {
            JsonElement jsonelement = jsonparser.parse(p_148796_0_).getAsJsonObject().get("backups");

            if (jsonelement.isJsonArray())
            {
                Iterator iterator = jsonelement.getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    backuplist.field_148797_a.add(Backup.func_148777_a((JsonElement)iterator.next()));
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

        return backuplist;
    }
}