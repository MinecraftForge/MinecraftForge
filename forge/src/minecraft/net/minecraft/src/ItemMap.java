package net.minecraft.src;

public class ItemMap extends ItemMapBase
{
    protected ItemMap(int par1)
    {
        super(par1);
        this.setMaxStackSize(1);
    }

    public static MapData getMPMapData(short par0, World par1World)
    {
        MapData var3 = (MapData)par1World.loadItemData(MapData.class, "map_" + par0);

        if (var3 == null)
        {
            int var4 = par1World.getUniqueDataId("map");
            String var2 = "map_" + var4;
            var3 = new MapData(var2);
            par1World.setItemData(var2, var3);
        }

        return var3;
    }

    public MapData getMapData(ItemStack par1ItemStack, World par2World)
    {
        MapData var4 = (MapData)par2World.loadItemData(MapData.class, "map_" + par1ItemStack.getItemDamage());

        if (var4 == null)
        {
            par1ItemStack.setItemDamage(par2World.getUniqueDataId("map"));
            String var3 = "map_" + par1ItemStack.getItemDamage();
            var4 = new MapData(var3);
            var4.xCenter = par2World.getWorldInfo().getSpawnX();
            var4.zCenter = par2World.getWorldInfo().getSpawnZ();
            var4.scale = 3;
            var4.dimension = (byte)par2World.worldProvider.worldType;
            var4.markDirty();
            par2World.setItemData(var3, var4);
        }

        return var4;
    }

    public void updateMapData(World par1World, Entity par2Entity, MapData par3MapData)
    {
        if (par1World.worldProvider.worldType == par3MapData.dimension)
        {
            short var4 = 128;
            short var5 = 128;
            int var6 = 1 << par3MapData.scale;
            int var7 = par3MapData.xCenter;
            int var8 = par3MapData.zCenter;
            int var9 = MathHelper.floor_double(par2Entity.posX - (double)var7) / var6 + var4 / 2;
            int var10 = MathHelper.floor_double(par2Entity.posZ - (double)var8) / var6 + var5 / 2;
            int var11 = 128 / var6;

            if (par1World.worldProvider.hasNoSky)
            {
                var11 /= 2;
            }

            ++par3MapData.field_28175_g;

            for (int var12 = var9 - var11 + 1; var12 < var9 + var11; ++var12)
            {
                if ((var12 & 15) == (par3MapData.field_28175_g & 15))
                {
                    int var13 = 255;
                    int var14 = 0;
                    double var15 = 0.0D;

                    for (int var17 = var10 - var11 - 1; var17 < var10 + var11; ++var17)
                    {
                        if (var12 >= 0 && var17 >= -1 && var12 < var4 && var17 < var5)
                        {
                            int var18 = var12 - var9;
                            int var19 = var17 - var10;
                            boolean var20 = var18 * var18 + var19 * var19 > (var11 - 2) * (var11 - 2);
                            int var21 = (var7 / var6 + var12 - var4 / 2) * var6;
                            int var22 = (var8 / var6 + var17 - var5 / 2) * var6;
                            byte var23 = 0;
                            byte var24 = 0;
                            byte var25 = 0;
                            int[] var26 = new int[256];
                            Chunk var27 = par1World.getChunkFromBlockCoords(var21, var22);
                            int var28 = var21 & 15;
                            int var29 = var22 & 15;
                            int var30 = 0;
                            double var31 = 0.0D;
                            int var34;
                            int var35;
                            int var33;
                            int var38;

                            if (par1World.worldProvider.hasNoSky)
                            {
                                var33 = var21 + var22 * 231871;
                                var33 = var33 * var33 * 31287121 + var33 * 11;

                                if ((var33 >> 20 & 1) == 0)
                                {
                                    var26[Block.dirt.blockID] += 10;
                                }
                                else
                                {
                                    var26[Block.stone.blockID] += 10;
                                }

                                var31 = 100.0D;
                            }
                            else
                            {
                                for (var33 = 0; var33 < var6; ++var33)
                                {
                                    for (var34 = 0; var34 < var6; ++var34)
                                    {
                                        var35 = var27.getHeightValue(var33 + var28, var34 + var29) + 1;
                                        int var36 = 0;

                                        if (var35 > 1)
                                        {
                                            boolean var37 = false;

                                            do
                                            {
                                                var37 = true;
                                                var36 = var27.getBlockID(var33 + var28, var35 - 1, var34 + var29);

                                                if (var36 == 0)
                                                {
                                                    var37 = false;
                                                }
                                                else if (var35 > 0 && var36 > 0 && Block.blocksList[var36].blockMaterial.materialMapColor == MapColor.airColor)
                                                {
                                                    var37 = false;
                                                }

                                                if (!var37)
                                                {
                                                    --var35;
                                                    var36 = var27.getBlockID(var33 + var28, var35 - 1, var34 + var29);
                                                }
                                            }
                                            while (var35 > 0 && !var37);

                                            if (var35 > 0 && var36 != 0 && Block.blocksList[var36].blockMaterial.isLiquid())
                                            {
                                                var38 = var35 - 1;
                                                boolean var39 = false;
                                                int var41;

                                                do
                                                {
                                                    var41 = var27.getBlockID(var33 + var28, var38--, var34 + var29);
                                                    ++var30;
                                                }
                                                while (var38 > 0 && var41 != 0 && Block.blocksList[var41].blockMaterial.isLiquid());
                                            }
                                        }

                                        var31 += (double)var35 / (double)(var6 * var6);
                                        ++var26[var36];
                                    }
                                }
                            }

                            var30 /= var6 * var6;
                            int var10000 = var23 / (var6 * var6);
                            var10000 = var24 / (var6 * var6);
                            var10000 = var25 / (var6 * var6);
                            var33 = 0;
                            var34 = 0;

                            for (var35 = 0; var35 < 256; ++var35)
                            {
                                if (var26[var35] > var33)
                                {
                                    var34 = var35;
                                    var33 = var26[var35];
                                }
                            }

                            double var43 = (var31 - var15) * 4.0D / (double)(var6 + 4) + ((double)(var12 + var17 & 1) - 0.5D) * 0.4D;
                            byte var42 = 1;

                            if (var43 > 0.6D)
                            {
                                var42 = 2;
                            }

                            if (var43 < -0.6D)
                            {
                                var42 = 0;
                            }

                            var38 = 0;

                            if (var34 > 0)
                            {
                                MapColor var45 = Block.blocksList[var34].blockMaterial.materialMapColor;

                                if (var45 == MapColor.waterColor)
                                {
                                    var43 = (double)var30 * 0.1D + (double)(var12 + var17 & 1) * 0.2D;
                                    var42 = 1;

                                    if (var43 < 0.5D)
                                    {
                                        var42 = 2;
                                    }

                                    if (var43 > 0.9D)
                                    {
                                        var42 = 0;
                                    }
                                }

                                var38 = var45.colorIndex;
                            }

                            var15 = var31;

                            if (var17 >= 0 && var18 * var18 + var19 * var19 < var11 * var11 && (!var20 || (var12 + var17 & 1) != 0))
                            {
                                byte var44 = par3MapData.colors[var12 + var17 * var4];
                                byte var40 = (byte)(var38 * 4 + var42);

                                if (var44 != var40)
                                {
                                    if (var13 > var17)
                                    {
                                        var13 = var17;
                                    }

                                    if (var14 < var17)
                                    {
                                        var14 = var17;
                                    }

                                    par3MapData.colors[var12 + var17 * var4] = var40;
                                }
                            }
                        }
                    }

                    if (var13 <= var14)
                    {
                        par3MapData.func_28170_a(var12, var13, var14);
                    }
                }
            }
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
        if (!par2World.isRemote)
        {
            MapData var6 = this.getMapData(par1ItemStack, par2World);

            if (par3Entity instanceof EntityPlayer)
            {
                EntityPlayer var7 = (EntityPlayer)par3Entity;
                var6.func_28169_a(var7, par1ItemStack);
            }

            if (par5)
            {
                this.updateMapData(par2World, par3Entity, var6);
            }
        }
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par1ItemStack.setItemDamage(par2World.getUniqueDataId("map"));
        String var4 = "map_" + par1ItemStack.getItemDamage();
        MapData var5 = new MapData(var4);
        par2World.setItemData(var4, var5);
        var5.xCenter = MathHelper.floor_double(par3EntityPlayer.posX);
        var5.zCenter = MathHelper.floor_double(par3EntityPlayer.posZ);
        var5.scale = 3;
        var5.dimension = (byte)par2World.worldProvider.worldType;
        var5.markDirty();
    }
}
