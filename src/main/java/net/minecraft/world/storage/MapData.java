package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public int dimension;
    public byte scale;
    // JAVADOC FIELD $$ field_76198_e
    public byte[] colors = new byte[16384];
    // JAVADOC FIELD $$ field_76196_g
    public List playersArrayList = new ArrayList();
    // JAVADOC FIELD $$ field_76202_j
    private Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();
    private static final String __OBFID = "CL_00000577";

    public MapData(String par1Str)
    {
        super(par1Str);
    }

    // JAVADOC METHOD $$ func_76184_a
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTBase dimension = par1NBTTagCompound.getTag("dimension");

        if (dimension instanceof NBTTagByte)
        {
            this.dimension = ((NBTTagByte)dimension).func_150290_f();
        }
        else
        {
            this.dimension = ((NBTTagInt)dimension).func_150287_d();
        }

        this.xCenter = par1NBTTagCompound.getInteger("xCenter");
        this.zCenter = par1NBTTagCompound.getInteger("zCenter");
        this.scale = par1NBTTagCompound.getByte("scale");

        if (this.scale < 0)
        {
            this.scale = 0;
        }

        if (this.scale > 4)
        {
            this.scale = 4;
        }

        short short1 = par1NBTTagCompound.getShort("width");
        short short2 = par1NBTTagCompound.getShort("height");

        if (short1 == 128 && short2 == 128)
        {
            this.colors = par1NBTTagCompound.getByteArray("colors");
        }
        else
        {
            byte[] abyte = par1NBTTagCompound.getByteArray("colors");
            this.colors = new byte[16384];
            int i = (128 - short1) / 2;
            int j = (128 - short2) / 2;

            for (int k = 0; k < short2; ++k)
            {
                int l = k + j;

                if (l >= 0 || l < 128)
                {
                    for (int i1 = 0; i1 < short1; ++i1)
                    {
                        int j1 = i1 + i;

                        if (j1 >= 0 || j1 < 128)
                        {
                            this.colors[j1 + l * 128] = abyte[i1 + k * short1];
                        }
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_76187_b
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("dimension", this.dimension);
        par1NBTTagCompound.setInteger("xCenter", this.xCenter);
        par1NBTTagCompound.setInteger("zCenter", this.zCenter);
        par1NBTTagCompound.setByte("scale", this.scale);
        par1NBTTagCompound.setShort("width", (short)128);
        par1NBTTagCompound.setShort("height", (short)128);
        par1NBTTagCompound.setByteArray("colors", this.colors);
    }

    // JAVADOC METHOD $$ func_76191_a
    public void updateVisiblePlayers(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        if (!this.playersHashMap.containsKey(par1EntityPlayer))
        {
            MapData.MapInfo mapinfo = new MapData.MapInfo(par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        if (!par1EntityPlayer.inventory.hasItemStack(par2ItemStack))
        {
            this.playersVisibleOnMap.remove(par1EntityPlayer.getCommandSenderName());
        }

        for (int i = 0; i < this.playersArrayList.size(); ++i)
        {
            MapData.MapInfo mapinfo1 = (MapData.MapInfo)this.playersArrayList.get(i);

            if (!mapinfo1.entityplayerObj.isDead && (mapinfo1.entityplayerObj.inventory.hasItemStack(par2ItemStack) || par2ItemStack.isOnItemFrame()))
            {
                if (!par2ItemStack.isOnItemFrame() && mapinfo1.entityplayerObj.dimension == this.dimension)
                {
                    this.func_82567_a(0, mapinfo1.entityplayerObj.worldObj, mapinfo1.entityplayerObj.getCommandSenderName(), mapinfo1.entityplayerObj.posX, mapinfo1.entityplayerObj.posZ, (double)mapinfo1.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                this.playersHashMap.remove(mapinfo1.entityplayerObj);
                this.playersArrayList.remove(mapinfo1);
            }
        }

        if (par2ItemStack.isOnItemFrame())
        {
            this.func_82567_a(1, par1EntityPlayer.worldObj, "frame-" + par2ItemStack.getItemFrame().func_145782_y(), (double)par2ItemStack.getItemFrame().field_146063_b, (double)par2ItemStack.getItemFrame().field_146062_d, (double)(par2ItemStack.getItemFrame().hangingDirection * 90));
        }
    }

    private void func_82567_a(int par1, World par2World, String par3Str, double par4, double par6, double par8)
    {
        int j = 1 << this.scale;
        float f = (float)(par4 - (double)this.xCenter) / (float)j;
        float f1 = (float)(par6 - (double)this.zCenter) / (float)j;
        byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
        byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
        byte b3 = 63;
        byte b2;

        if (f >= (float)(-b3) && f1 >= (float)(-b3) && f <= (float)b3 && f1 <= (float)b3)
        {
            par8 += par8 < 0.0D ? -8.0D : 8.0D;
            b2 = (byte)((int)(par8 * 16.0D / 360.0D));

            if (par2World.provider.shouldMapSpin(par3Str, par4, par6, par8))
            {
                int k = (int)(par2World.getWorldInfo().getWorldTime() / 10L);
                b2 = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(f) >= 320.0F || Math.abs(f1) >= 320.0F)
            {
                this.playersVisibleOnMap.remove(par3Str);
                return;
            }

            par1 = 6;
            b2 = 0;

            if (f <= (float)(-b3))
            {
                b0 = (byte)((int)((double)(b3 * 2) + 2.5D));
            }

            if (f1 <= (float)(-b3))
            {
                b1 = (byte)((int)((double)(b3 * 2) + 2.5D));
            }

            if (f >= (float)b3)
            {
                b0 = (byte)(b3 * 2 + 1);
            }

            if (f1 >= (float)b3)
            {
                b1 = (byte)(b3 * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(par3Str, new MapData.MapCoord((byte)par1, b0, b1, b2));
    }

    // JAVADOC METHOD $$ func_76193_a
    public byte[] getUpdatePacketData(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MapData.MapInfo mapinfo = (MapData.MapInfo)this.playersHashMap.get(par3EntityPlayer);
        return mapinfo == null ? null : mapinfo.getPlayersOnMap(par1ItemStack);
    }

    // JAVADOC METHOD $$ func_76194_a
    public void setColumnDirty(int par1, int par2, int par3)
    {
        super.markDirty();

        for (int l = 0; l < this.playersArrayList.size(); ++l)
        {
            MapData.MapInfo mapinfo = (MapData.MapInfo)this.playersArrayList.get(l);

            if (mapinfo.field_76209_b[par1] < 0 || mapinfo.field_76209_b[par1] > par2)
            {
                mapinfo.field_76209_b[par1] = par2;
            }

            if (mapinfo.field_76210_c[par1] < 0 || mapinfo.field_76210_c[par1] < par3)
            {
                mapinfo.field_76210_c[par1] = par3;
            }
        }
    }

    // JAVADOC METHOD $$ func_76192_a
    @SideOnly(Side.CLIENT)
    public void updateMPMapData(byte[] par1ArrayOfByte)
    {
        int i;

        if (par1ArrayOfByte[0] == 0)
        {
            i = par1ArrayOfByte[1] & 255;
            int j = par1ArrayOfByte[2] & 255;

            for (int k = 0; k < par1ArrayOfByte.length - 3; ++k)
            {
                this.colors[(k + j) * 128 + i] = par1ArrayOfByte[k + 3];
            }

            this.markDirty();
        }
        else if (par1ArrayOfByte[0] == 1)
        {
            this.playersVisibleOnMap.clear();

            for (i = 0; i < (par1ArrayOfByte.length - 1) / 3; ++i)
            {
                byte b2 = (byte)(par1ArrayOfByte[i * 3 + 1] >> 4);
                byte b3 = par1ArrayOfByte[i * 3 + 2];
                byte b0 = par1ArrayOfByte[i * 3 + 3];
                byte b1 = (byte)(par1ArrayOfByte[i * 3 + 1] & 15);
                this.playersVisibleOnMap.put("icon-" + i, new MapData.MapCoord(b2, b3, b0, b1));
            }
        }
        else if (par1ArrayOfByte[0] == 2)
        {
            this.scale = par1ArrayOfByte[1];
        }
    }

    public MapData.MapInfo func_82568_a(EntityPlayer par1EntityPlayer)
    {
        MapData.MapInfo mapinfo = (MapData.MapInfo)this.playersHashMap.get(par1EntityPlayer);

        if (mapinfo == null)
        {
            mapinfo = new MapData.MapInfo(par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        return mapinfo;
    }

    public class MapCoord
    {
        public byte iconSize;
        public byte centerX;
        public byte centerZ;
        public byte iconRotation;
        private static final String __OBFID = "CL_00000579";

        public MapCoord(byte par2, byte par3, byte par4, byte par5)
        {
            this.iconSize = par2;
            this.centerX = par3;
            this.centerZ = par4;
            this.iconRotation = par5;
        }
    }

    public class MapInfo
    {
        // JAVADOC FIELD $$ field_76211_a
        public final EntityPlayer entityplayerObj;
        public int[] field_76209_b = new int[128];
        public int[] field_76210_c = new int[128];
        // JAVADOC FIELD $$ field_76208_e
        private int currentRandomNumber;
        private int ticksUntilPlayerLocationMapUpdate;
        // JAVADOC FIELD $$ field_76206_g
        private byte[] lastPlayerLocationOnMap;
        public int field_82569_d;
        private boolean field_82570_i;
        private static final String __OBFID = "CL_00000578";

        public MapInfo(EntityPlayer par2EntityPlayer)
        {
            this.entityplayerObj = par2EntityPlayer;

            for (int i = 0; i < this.field_76209_b.length; ++i)
            {
                this.field_76209_b[i] = 0;
                this.field_76210_c[i] = 127;
            }
        }

        // JAVADOC METHOD $$ func_76204_a
        public byte[] getPlayersOnMap(ItemStack par1ItemStack)
        {
            byte[] abyte;

            if (!this.field_82570_i)
            {
                abyte = new byte[] {(byte)2, MapData.this.scale};
                this.field_82570_i = true;
                return abyte;
            }
            else
            {
                int i;
                int l;

                if (--this.ticksUntilPlayerLocationMapUpdate < 0)
                {
                    this.ticksUntilPlayerLocationMapUpdate = 4;
                    abyte = new byte[MapData.this.playersVisibleOnMap.size() * 3 + 1];
                    abyte[0] = 1;
                    i = 0;

                    for (Iterator iterator = MapData.this.playersVisibleOnMap.values().iterator(); iterator.hasNext(); ++i)
                    {
                        MapData.MapCoord mapcoord = (MapData.MapCoord)iterator.next();
                        abyte[i * 3 + 1] = (byte)(mapcoord.iconSize << 4 | mapcoord.iconRotation & 15);
                        abyte[i * 3 + 2] = mapcoord.centerX;
                        abyte[i * 3 + 3] = mapcoord.centerZ;
                    }

                    boolean flag = !par1ItemStack.isOnItemFrame();

                    if (this.lastPlayerLocationOnMap != null && this.lastPlayerLocationOnMap.length == abyte.length)
                    {
                        for (l = 0; l < abyte.length; ++l)
                        {
                            if (abyte[l] != this.lastPlayerLocationOnMap[l])
                            {
                                flag = false;
                                break;
                            }
                        }
                    }
                    else
                    {
                        flag = false;
                    }

                    if (!flag)
                    {
                        this.lastPlayerLocationOnMap = abyte;
                        return abyte;
                    }
                }

                for (int k = 0; k < 1; ++k)
                {
                    i = this.currentRandomNumber++ * 11 % 128;

                    if (this.field_76209_b[i] >= 0)
                    {
                        int i1 = this.field_76210_c[i] - this.field_76209_b[i] + 1;
                        l = this.field_76209_b[i];
                        byte[] abyte1 = new byte[i1 + 3];
                        abyte1[0] = 0;
                        abyte1[1] = (byte)i;
                        abyte1[2] = (byte)l;

                        for (int j = 0; j < abyte1.length - 3; ++j)
                        {
                            abyte1[j + 3] = MapData.this.colors[(j + l) * 128 + i];
                        }

                        this.field_76210_c[i] = -1;
                        this.field_76209_b[i] = -1;
                        return abyte1;
                    }
                }

                return null;
            }
        }
    }
}