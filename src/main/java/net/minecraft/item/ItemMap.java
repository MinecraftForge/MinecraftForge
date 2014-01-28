package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase
{
    private static final String __OBFID = "CL_00000047";

    protected ItemMap()
    {
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public static MapData func_150912_a(int p_150912_0_, World p_150912_1_)
    {
        String s = "map_" + p_150912_0_;
        MapData mapdata = (MapData)p_150912_1_.loadItemData(MapData.class, s);

        if (mapdata == null)
        {
            mapdata = new MapData(s);
            p_150912_1_.setItemData(s, mapdata);
        }

        return mapdata;
    }

    public MapData getMapData(ItemStack par1ItemStack, World par2World)
    {
        String s = "map_" + par1ItemStack.getItemDamage();
        MapData mapdata = (MapData)par2World.loadItemData(MapData.class, s);

        if (mapdata == null && !par2World.isRemote)
        {
            par1ItemStack.setItemDamage(par2World.getUniqueDataId("map"));
            s = "map_" + par1ItemStack.getItemDamage();
            mapdata = new MapData(s);
            mapdata.scale = 3;
            int i = 128 * (1 << mapdata.scale);
            mapdata.xCenter = Math.round((float)par2World.getWorldInfo().getSpawnX() / (float)i) * i;
            mapdata.zCenter = Math.round((float)(par2World.getWorldInfo().getSpawnZ() / i)) * i;
            mapdata.dimension = par2World.provider.dimensionId;
            mapdata.markDirty();
            par2World.setItemData(s, mapdata);
        }

        return mapdata;
    }

    public void updateMapData(World par1World, Entity par2Entity, MapData par3MapData)
    {
        if (par1World.provider.dimensionId == par3MapData.dimension && par2Entity instanceof EntityPlayer)
        {
            int i = 1 << par3MapData.scale;
            int j = par3MapData.xCenter;
            int k = par3MapData.zCenter;
            int l = MathHelper.floor_double(par2Entity.posX - (double)j) / i + 64;
            int i1 = MathHelper.floor_double(par2Entity.posZ - (double)k) / i + 64;
            int j1 = 128 / i;

            if (par1World.provider.hasNoSky)
            {
                j1 /= 2;
            }

            MapData.MapInfo mapinfo = par3MapData.func_82568_a((EntityPlayer)par2Entity);
            ++mapinfo.field_82569_d;

            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1)
            {
                if ((k1 & 15) == (mapinfo.field_82569_d & 15))
                {
                    int l1 = 255;
                    int i2 = 0;
                    double d0 = 0.0D;

                    for (int j2 = i1 - j1 - 1; j2 < i1 + j1; ++j2)
                    {
                        if (k1 >= 0 && j2 >= -1 && k1 < 128 && j2 < 128)
                        {
                            int k2 = k1 - l;
                            int l2 = j2 - i1;
                            boolean flag = k2 * k2 + l2 * l2 > (j1 - 2) * (j1 - 2);
                            int i3 = (j / i + k1 - 64) * i;
                            int j3 = (k / i + j2 - 64) * i;
                            HashMultiset hashmultiset = HashMultiset.create();
                            Chunk chunk = par1World.getChunkFromBlockCoords(i3, j3);

                            if (!chunk.isEmpty())
                            {
                                int k3 = i3 & 15;
                                int l3 = j3 & 15;
                                int i4 = 0;
                                double d1 = 0.0D;
                                int j4;

                                if (par1World.provider.hasNoSky)
                                {
                                    j4 = i3 + j3 * 231871;
                                    j4 = j4 * j4 * 31287121 + j4 * 11;

                                    if ((j4 >> 20 & 1) == 0)
                                    {
                                        hashmultiset.add(Blocks.dirt.func_149728_f(0), 10);
                                    }
                                    else
                                    {
                                        hashmultiset.add(Blocks.stone.func_149728_f(0), 100);
                                    }

                                    d1 = 100.0D;
                                }
                                else
                                {
                                    for (j4 = 0; j4 < i; ++j4)
                                    {
                                        for (int k4 = 0; k4 < i; ++k4)
                                        {
                                            int l4 = chunk.getHeightValue(j4 + k3, k4 + l3) + 1;
                                            Block block = Blocks.air;
                                            int i5 = 0;

                                            if (l4 > 1)
                                            {
                                                do
                                                {
                                                    --l4;
                                                    block = chunk.func_150810_a(j4 + k3, l4, k4 + l3);
                                                    i5 = chunk.getBlockMetadata(j4 + k3, l4, k4 + l3);
                                                }
                                                while (block.func_149728_f(i5) == MapColor.field_151660_b && l4 > 0);

                                                if (l4 > 0 && block.func_149688_o().isLiquid())
                                                {
                                                    int j5 = l4 - 1;
                                                    Block block1;

                                                    do
                                                    {
                                                        block1 = chunk.func_150810_a(j4 + k3, j5--, k4 + l3);
                                                        ++i4;
                                                    }
                                                    while (j5 > 0 && block1.func_149688_o().isLiquid());
                                                }
                                            }

                                            d1 += (double)l4 / (double)(i * i);
                                            hashmultiset.add(block.func_149728_f(i5));
                                        }
                                    }
                                }

                                i4 /= i * i;
                                double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + j2 & 1) - 0.5D) * 0.4D;
                                byte b1 = 1;

                                if (d2 > 0.6D)
                                {
                                    b1 = 2;
                                }

                                if (d2 < -0.6D)
                                {
                                    b1 = 0;
                                }

                                MapColor mapcolor = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(hashmultiset), MapColor.field_151660_b);

                                if (mapcolor == MapColor.field_151662_n)
                                {
                                    d2 = (double)i4 * 0.1D + (double)(k1 + j2 & 1) * 0.2D;
                                    b1 = 1;

                                    if (d2 < 0.5D)
                                    {
                                        b1 = 2;
                                    }

                                    if (d2 > 0.9D)
                                    {
                                        b1 = 0;
                                    }
                                }

                                d0 = d1;

                                if (j2 >= 0 && k2 * k2 + l2 * l2 < j1 * j1 && (!flag || (k1 + j2 & 1) != 0))
                                {
                                    byte b0 = par3MapData.colors[k1 + j2 * 128];
                                    byte b2 = (byte)(mapcolor.colorIndex * 4 + b1);

                                    if (b0 != b2)
                                    {
                                        if (l1 > j2)
                                        {
                                            l1 = j2;
                                        }

                                        if (i2 < j2)
                                        {
                                            i2 = j2;
                                        }

                                        par3MapData.colors[k1 + j2 * 128] = b2;
                                    }
                                }
                            }
                        }
                    }

                    if (l1 <= i2)
                    {
                        par3MapData.setColumnDirty(k1, l1, i2);
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_77663_a
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
        if (!par2World.isRemote)
        {
            MapData mapdata = this.getMapData(par1ItemStack, par2World);

            if (par3Entity instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)par3Entity;
                mapdata.updateVisiblePlayers(entityplayer, par1ItemStack);
            }

            if (par5)
            {
                this.updateMapData(par2World, par3Entity, mapdata);
            }
        }
    }

    public Packet func_150911_c(ItemStack p_150911_1_, World p_150911_2_, EntityPlayer p_150911_3_)
    {
        byte[] abyte = this.getMapData(p_150911_1_, p_150911_2_).getUpdatePacketData(p_150911_1_, p_150911_2_, p_150911_3_);
        return abyte == null ? null : new S34PacketMaps(p_150911_1_.getItemDamage(), abyte);
    }

    // JAVADOC METHOD $$ func_77622_d
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().getBoolean("map_is_scaling"))
        {
            MapData mapdata = Items.filled_map.getMapData(par1ItemStack, par2World);
            par1ItemStack.setItemDamage(par2World.getUniqueDataId("map"));
            MapData mapdata1 = new MapData("map_" + par1ItemStack.getItemDamage());
            mapdata1.scale = (byte)(mapdata.scale + 1);

            if (mapdata1.scale > 4)
            {
                mapdata1.scale = 4;
            }

            mapdata1.xCenter = mapdata.xCenter;
            mapdata1.zCenter = mapdata.zCenter;
            mapdata1.dimension = mapdata.dimension;
            mapdata1.markDirty();
            par2World.setItemData("map_" + par1ItemStack.getItemDamage(), mapdata1);
        }
    }

    // JAVADOC METHOD $$ func_77624_a
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        MapData mapdata = this.getMapData(par1ItemStack, par2EntityPlayer.worldObj);

        if (par4)
        {
            if (mapdata == null)
            {
                par3List.add("Unknown map");
            }
            else
            {
                par3List.add("Scaling at 1:" + (1 << mapdata.scale));
                par3List.add("(Level " + mapdata.scale + "/" + 4 + ")");
            }
        }
    }
}