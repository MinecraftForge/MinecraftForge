package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Vector;
import javax.swing.JList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

@SideOnly(Side.SERVER)
public class PlayerListComponent extends JList implements IUpdatePlayerListBox
{
    private MinecraftServer field_120015_a;
    private int field_120014_b;
    private static final String __OBFID = "CL_00001795";

    public PlayerListComponent(MinecraftServer par1MinecraftServer)
    {
        this.field_120015_a = par1MinecraftServer;
        par1MinecraftServer.func_82010_a(this);
    }

    // JAVADOC METHOD $$ func_73660_a
    public void update()
    {
        if (this.field_120014_b++ % 20 == 0)
        {
            Vector vector = new Vector();

            for (int i = 0; i < this.field_120015_a.getConfigurationManager().playerEntityList.size(); ++i)
            {
                vector.add(((EntityPlayerMP)this.field_120015_a.getConfigurationManager().playerEntityList.get(i)).getCommandSenderName());
            }

            this.setListData(vector);
        }
    }
}