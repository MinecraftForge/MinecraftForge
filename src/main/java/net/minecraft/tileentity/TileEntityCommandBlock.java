package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TileEntityCommandBlock extends TileEntity
{
    private final CommandBlockLogic field_145994_a = new CommandBlockLogic()
    {
        private static final String __OBFID = "CL_00000348";
        // JAVADOC METHOD $$ func_82114_b
        public ChunkCoordinates getPlayerCoordinates()
        {
            return new ChunkCoordinates(TileEntityCommandBlock.this.field_145851_c, TileEntityCommandBlock.this.field_145848_d, TileEntityCommandBlock.this.field_145849_e);
        }
        public World getEntityWorld()
        {
            return TileEntityCommandBlock.this.func_145831_w();
        }
        public void func_145752_a(String p_145752_1_)
        {
            super.func_145752_a(p_145752_1_);
            TileEntityCommandBlock.this.onInventoryChanged();
        }
        public void func_145756_e()
        {
            TileEntityCommandBlock.this.func_145831_w().func_147471_g(TileEntityCommandBlock.this.field_145851_c, TileEntityCommandBlock.this.field_145848_d, TileEntityCommandBlock.this.field_145849_e);
        }
        @SideOnly(Side.CLIENT)
        public int func_145751_f()
        {
            return 0;
        }
        @SideOnly(Side.CLIENT)
        public void func_145757_a(ByteBuf p_145757_1_)
        {
            p_145757_1_.writeInt(TileEntityCommandBlock.this.field_145851_c);
            p_145757_1_.writeInt(TileEntityCommandBlock.this.field_145848_d);
            p_145757_1_.writeInt(TileEntityCommandBlock.this.field_145849_e);
        }
    };
    private static final String __OBFID = "CL_00000347";

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        this.field_145994_a.func_145758_a(p_145841_1_);
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_145994_a.func_145759_b(p_145839_1_);
    }

    public Packet func_145844_m()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 2, nbttagcompound);
    }

    public CommandBlockLogic func_145993_a()
    {
        return this.field_145994_a;
    }
}