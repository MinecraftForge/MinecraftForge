package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import static net.minecraftforge.common.ChestGenHooks.*;

public class StructureVillagePieces
{
    private static final String __OBFID = "CL_00000516";

    public static void func_143016_a()
    {
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House1.class, "ViBH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Field1.class, "ViDF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Field2.class, "ViF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Torch.class, "ViL");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Hall.class, "ViPH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House4Garden.class, "ViSH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.WoodHut.class, "ViSmH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Church.class, "ViST");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House2.class, "ViS");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Start.class, "ViStart");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Path.class, "ViSR");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House3.class, "ViTRH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Well.class, "ViW");
    }

    public static List getStructureVillageWeightedPieceList(Random par0Random, int par1)
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 4 + par1 * 2)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 1 + par1)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 2 + par1)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 3)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 2 + par1)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 1 + par1, 4 + par1)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 4 + par1 * 2)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getRandomIntegerInRange(par0Random, 0, 1 + par1)));
        arraylist.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 3 + par1 * 2)));
        VillagerRegistry.addExtraVillageComponents(arraylist, par0Random, par1);

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext())
        {
            if (((StructureVillagePieces.PieceWeight)iterator.next()).villagePiecesLimit == 0)
            {
                iterator.remove();
            }
        }

        return arraylist;
    }

    private static int func_75079_a(List par0List)
    {
        boolean flag = false;
        int i = 0;
        StructureVillagePieces.PieceWeight pieceweight;

        for (Iterator iterator = par0List.iterator(); iterator.hasNext(); i += pieceweight.villagePieceWeight)
        {
            pieceweight = (StructureVillagePieces.PieceWeight)iterator.next();

            if (pieceweight.villagePiecesLimit > 0 && pieceweight.villagePiecesSpawned < pieceweight.villagePiecesLimit)
            {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private static StructureVillagePieces.Village func_75083_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, StructureVillagePieces.PieceWeight par1StructureVillagePieceWeight, List par2List, Random par3Random, int par4, int par5, int par6, int par7, int par8)
    {
        Class oclass = par1StructureVillagePieceWeight.villagePieceClass;
        Object object = null;

        if (oclass == StructureVillagePieces.House4Garden.class)
        {
            object = StructureVillagePieces.House4Garden.func_74912_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.Church.class)
        {
            object = StructureVillagePieces.Church.func_74919_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.House1.class)
        {
            object = StructureVillagePieces.House1.func_74898_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.WoodHut.class)
        {
            object = StructureVillagePieces.WoodHut.func_74908_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.Hall.class)
        {
            object = StructureVillagePieces.Hall.func_74906_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.Field1.class)
        {
            object = StructureVillagePieces.Field1.func_74900_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.Field2.class)
        {
            object = StructureVillagePieces.Field2.func_74902_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.House2.class)
        {
            object = StructureVillagePieces.House2.func_74915_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (oclass == StructureVillagePieces.House3.class)
        {
            object = StructureVillagePieces.House3.func_74921_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else
        {
            object = VillagerRegistry.getVillageComponent(par1StructureVillagePieceWeight, par0ComponentVillageStartPiece , par2List, par3Random, par4, par5, par6, par7, par8);
        }

        return (StructureVillagePieces.Village)object;
    }

    // JAVADOC METHOD $$ func_75081_c
    private static StructureVillagePieces.Village getNextVillageComponent(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        int j1 = func_75079_a(par0ComponentVillageStartPiece.structureVillageWeightedPieceList);

        if (j1 <= 0)
        {
            return null;
        }
        else
        {
            int k1 = 0;

            while (k1 < 5)
            {
                ++k1;
                int l1 = par2Random.nextInt(j1);
                Iterator iterator = par0ComponentVillageStartPiece.structureVillageWeightedPieceList.iterator();

                while (iterator.hasNext())
                {
                    StructureVillagePieces.PieceWeight pieceweight = (StructureVillagePieces.PieceWeight)iterator.next();
                    l1 -= pieceweight.villagePieceWeight;

                    if (l1 < 0)
                    {
                        if (!pieceweight.canSpawnMoreVillagePiecesOfType(par7) || pieceweight == par0ComponentVillageStartPiece.structVillagePieceWeight && par0ComponentVillageStartPiece.structureVillageWeightedPieceList.size() > 1)
                        {
                            break;
                        }

                        StructureVillagePieces.Village village = func_75083_a(par0ComponentVillageStartPiece, pieceweight, par1List, par2Random, par3, par4, par5, par6, par7);

                        if (village != null)
                        {
                            ++pieceweight.villagePiecesSpawned;
                            par0ComponentVillageStartPiece.structVillagePieceWeight = pieceweight;

                            if (!pieceweight.canSpawnMoreVillagePieces())
                            {
                                par0ComponentVillageStartPiece.structureVillageWeightedPieceList.remove(pieceweight);
                            }

                            return village;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = StructureVillagePieces.Torch.func_74904_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

            if (structureboundingbox != null)
            {
                return new StructureVillagePieces.Torch(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6);
            }
            else
            {
                return null;
            }
        }
    }

    // JAVADOC METHOD $$ func_75077_d
    private static StructureComponent getNextVillageStructureComponent(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 50)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) <= 112)
        {
            StructureVillagePieces.Village village = getNextVillageComponent(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7 + 1);

            if (village != null)
            {
                int j1 = (village.boundingBox.minX + village.boundingBox.maxX) / 2;
                int k1 = (village.boundingBox.minZ + village.boundingBox.maxZ) / 2;
                int l1 = village.boundingBox.maxX - village.boundingBox.minX;
                int i2 = village.boundingBox.maxZ - village.boundingBox.minZ;
                int j2 = l1 > i2 ? l1 : i2;

                if (par0ComponentVillageStartPiece.getWorldChunkManager().areBiomesViable(j1, k1, j2 / 2 + 4, MapGenVillage.villageSpawnBiomes))
                {
                    par1List.add(village);
                    par0ComponentVillageStartPiece.field_74932_i.add(village);
                    return village;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    private static StructureComponent getNextComponentVillagePath(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 3 + par0ComponentVillageStartPiece.terrainType)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) <= 112)
        {
            StructureBoundingBox structureboundingbox = StructureVillagePieces.Path.func_74933_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

            if (structureboundingbox != null && structureboundingbox.minY > 10)
            {
                StructureVillagePieces.Path path = new StructureVillagePieces.Path(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6);
                int j1 = (path.boundingBox.minX + path.boundingBox.maxX) / 2;
                int k1 = (path.boundingBox.minZ + path.boundingBox.maxZ) / 2;
                int l1 = path.boundingBox.maxX - path.boundingBox.minX;
                int i2 = path.boundingBox.maxZ - path.boundingBox.minZ;
                int j2 = l1 > i2 ? l1 : i2;

                if (par0ComponentVillageStartPiece.getWorldChunkManager().areBiomesViable(j1, k1, j2 / 2 + 4, MapGenVillage.villageSpawnBiomes))
                {
                    par1List.add(path);
                    par0ComponentVillageStartPiece.field_74930_j.add(path);
                    return path;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    public static class Well extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000533";

            public Well() {}

            public Well(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, int par4, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par3Random.nextInt(4);

                switch (this.coordBaseMode)
                {
                    case 0:
                    case 2:
                        this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
                        break;
                    default:
                        this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
                }
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, 1, this.getComponentType());
                StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, 3, this.getComponentType());
                StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ - 1, 2, this.getComponentType());
                StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 3, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 4, 12, 4, Blocks.cobblestone, Blocks.flowing_water, false);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 12, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 3, 12, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 12, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 3, 12, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 13, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 14, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 4, 13, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 4, 14, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 13, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 14, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 4, 13, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 4, 14, 4, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 15, 1, 4, 15, 4, Blocks.cobblestone, Blocks.cobblestone, false);

                for (int i = 0; i <= 5; ++i)
                {
                    for (int j = 0; j <= 5; ++j)
                    {
                        if (j == 0 || j == 5 || i == 0 || i == 5)
                        {
                            this.func_151550_a(par1World, Blocks.gravel, 0, j, 11, i, par3StructureBoundingBox);
                            this.clearCurrentPositionBlocksUpwards(par1World, j, 12, i, par3StructureBoundingBox);
                        }
                    }
                }

                return true;
            }
        }

    abstract static class Village extends StructureComponent
        {
            protected int field_143015_k = -1;
            // JAVADOC FIELD $$ field_74896_a
            private int villagersSpawned;
            private boolean field_143014_b;
            private static final String __OBFID = "CL_00000531";
            private StructureVillagePieces.Start startPiece;

            public Village() {}

            protected Village(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2)
            {
                super(par2);

                if (par1ComponentVillageStartPiece != null)
                {
                    this.field_143014_b = par1ComponentVillageStartPiece.inDesert;
                    startPiece = par1ComponentVillageStartPiece;
                }
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                par1NBTTagCompound.setInteger("HPos", this.field_143015_k);
                par1NBTTagCompound.setInteger("VCount", this.villagersSpawned);
                par1NBTTagCompound.setBoolean("Desert", this.field_143014_b);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                this.field_143015_k = par1NBTTagCompound.getInteger("HPos");
                this.villagersSpawned = par1NBTTagCompound.getInteger("VCount");
                this.field_143014_b = par1NBTTagCompound.getBoolean("Desert");
            }

            // JAVADOC METHOD $$ func_74891_a
            protected StructureComponent getNextComponentNN(StructureVillagePieces.Start par1ComponentVillageStartPiece, List par2List, Random par3Random, int par4, int par5)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());
                    case 1:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    case 2:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());
                    case 3:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74894_b
            protected StructureComponent getNextComponentPP(StructureVillagePieces.Start par1ComponentVillageStartPiece, List par2List, Random par3Random, int par4, int par5)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());
                    case 1:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    case 2:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());
                    case 3:
                        return StructureVillagePieces.getNextVillageStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74889_b
            protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox)
            {
                int i = 0;
                int j = 0;

                for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
                {
                    for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l)
                    {
                        if (par2StructureBoundingBox.isVecInside(l, 64, k))
                        {
                            i += Math.max(par1World.getTopSolidOrLiquidBlock(l, k), par1World.provider.getAverageGroundLevel());
                            ++j;
                        }
                    }
                }

                if (j == 0)
                {
                    return -1;
                }
                else
                {
                    return i / j;
                }
            }

            protected static boolean canVillageGoDeeper(StructureBoundingBox par0StructureBoundingBox)
            {
                return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
            }

            // JAVADOC METHOD $$ func_74893_a
            protected void spawnVillagers(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int par6)
            {
                if (this.villagersSpawned < par6)
                {
                    for (int i1 = this.villagersSpawned; i1 < par6; ++i1)
                    {
                        int j1 = this.getXWithOffset(par3 + i1, par5);
                        int k1 = this.getYWithOffset(par4);
                        int l1 = this.getZWithOffset(par3 + i1, par5);

                        if (!par2StructureBoundingBox.isVecInside(j1, k1, l1))
                        {
                            break;
                        }

                        ++this.villagersSpawned;
                        EntityVillager entityvillager = new EntityVillager(par1World, this.getVillagerType(i1));
                        entityvillager.setLocationAndAngles((double)j1 + 0.5D, (double)k1, (double)l1 + 0.5D, 0.0F, 0.0F);
                        par1World.spawnEntityInWorld(entityvillager);
                    }
                }
            }

            // JAVADOC METHOD $$ func_74888_b
            protected int getVillagerType(int par1)
            {
                return 0;
            }

            protected Block func_151558_b(Block p_151558_1_, int p_151558_2_)
            {
                BiomeEvent.GetVillageBlockID event = new BiomeEvent.GetVillageBlockID(startPiece == null ? null : startPiece.biome, p_151558_1_, p_151558_2_);
                MinecraftForge.TERRAIN_GEN_BUS.post(event);
                if (event.getResult() == Result.DENY) return event.replacement;
                if (this.field_143014_b)
                {
                    if (p_151558_1_ == Blocks.log || p_151558_1_ == Blocks.log2)
                    {
                        return Blocks.sandstone;
                    }

                    if (p_151558_1_ == Blocks.cobblestone)
                    {
                        return Blocks.sandstone;
                    }

                    if (p_151558_1_ == Blocks.planks)
                    {
                        return Blocks.sandstone;
                    }

                    if (p_151558_1_ == Blocks.oak_stairs)
                    {
                        return Blocks.sandstone_stairs;
                    }

                    if (p_151558_1_ == Blocks.stone_stairs)
                    {
                        return Blocks.sandstone_stairs;
                    }

                    if (p_151558_1_ == Blocks.gravel)
                    {
                        return Blocks.sandstone;
                    }
                }

                return p_151558_1_;
            }

            protected int func_151557_c(Block p_151557_1_, int p_151557_2_)
            {
                BiomeEvent.GetVillageBlockMeta event = new BiomeEvent.GetVillageBlockMeta(startPiece == null ? null : startPiece.biome, p_151557_1_, p_151557_2_);
                MinecraftForge.TERRAIN_GEN_BUS.post(event);
                if (event.getResult() == Result.DENY) return event.replacement;
                if (this.field_143014_b)
                {
                    if (p_151557_1_ == Blocks.log || p_151557_1_ == Blocks.log2)
                    {
                        return 0;
                    }

                    if (p_151557_1_ == Blocks.cobblestone)
                    {
                        return 0;
                    }

                    if (p_151557_1_ == Blocks.planks)
                    {
                        return 2;
                    }
                }

                return p_151557_2_;
            }

            protected void func_151550_a(World p_151550_1_, Block p_151550_2_, int p_151550_3_, int p_151550_4_, int p_151550_5_, int p_151550_6_, StructureBoundingBox p_151550_7_)
            {
                Block block1 = this.func_151558_b(p_151550_2_, p_151550_3_);
                int i1 = this.func_151557_c(p_151550_2_, p_151550_3_);
                super.func_151550_a(p_151550_1_, block1, i1, p_151550_4_, p_151550_5_, p_151550_6_, p_151550_7_);
            }

            protected void func_151549_a(World p_151549_1_, StructureBoundingBox p_151549_2_, int p_151549_3_, int p_151549_4_, int p_151549_5_, int p_151549_6_, int p_151549_7_, int p_151549_8_, Block p_151549_9_, Block p_151549_10_, boolean p_151549_11_)
            {
                Block block2 = this.func_151558_b(p_151549_9_, 0);
                int k1 = this.func_151557_c(p_151549_9_, 0);
                Block block3 = this.func_151558_b(p_151549_10_, 0);
                int l1 = this.func_151557_c(p_151549_10_, 0);
                super.func_151556_a(p_151549_1_, p_151549_2_, p_151549_3_, p_151549_4_, p_151549_5_, p_151549_6_, p_151549_7_, p_151549_8_, block2, k1, block3, l1, p_151549_11_);
            }

            protected void func_151554_b(World p_151554_1_, Block p_151554_2_, int p_151554_3_, int p_151554_4_, int p_151554_5_, int p_151554_6_, StructureBoundingBox p_151554_7_)
            {
                Block block1 = this.func_151558_b(p_151554_2_, p_151554_3_);
                int i1 = this.func_151557_c(p_151554_2_, p_151554_3_);
                super.func_151554_b(p_151554_1_, block1, i1, p_151554_4_, p_151554_5_, p_151554_6_, p_151554_7_);
            }
        }

    public static class Hall extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000522";

            public Hall() {}

            public Hall(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureVillagePieces.Hall func_74906_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 7, 11, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.Hall(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 7 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 0, 6, 8, 0, 10, Blocks.dirt, Blocks.dirt, false);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, 0, 6, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 1, 6, 2, 1, 10, Blocks.fence, Blocks.fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 1, 6, 8, 1, 10, Blocks.fence, Blocks.fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 10, 7, 1, 10, Blocks.fence, Blocks.fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 0, 7, 3, 0, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 5, 7, 3, 5, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 1, 8, 4, 1, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 4, 8, 4, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 2, 8, 5, 3, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.planks, 0, 0, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 0, 4, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 4, 3, par3StructureBoundingBox);
                int i = this.func_151555_a(Blocks.oak_stairs, 3);
                int j = this.func_151555_a(Blocks.oak_stairs, 2);
                int k;
                int l;

                for (k = -1; k <= 2; ++k)
                {
                    for (l = 0; l <= 8; ++l)
                    {
                        this.func_151550_a(par1World, Blocks.oak_stairs, i, l, 4 + k, k, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.oak_stairs, j, l, 4 + k, 5 - k, par3StructureBoundingBox);
                    }
                }

                this.func_151550_a(par1World, Blocks.log, 0, 0, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 0, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 3, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 6, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 2, 1, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wooden_pressure_plate, 0, 2, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 1, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, this.func_151555_a(Blocks.oak_stairs, 3), 2, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, this.func_151555_a(Blocks.oak_stairs, 1), 1, 1, 3, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 0, 1, 7, 0, 3, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
                this.func_151550_a(par1World, Blocks.double_stone_slab, 0, 6, 1, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.double_stone_slab, 0, 6, 1, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 2, 3, 1, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 2, 1, 0, this.func_151555_a(Blocks.wooden_door, 1));

                if (this.func_151548_a(par1World, 2, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 2, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 2, 0, -1, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.air, 0, 6, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 6, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 6, 3, 4, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 6, 1, 5, this.func_151555_a(Blocks.wooden_door, 1));

                for (k = 0; k < 5; ++k)
                {
                    for (l = 0; l < 9; ++l)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, l, 7, k, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, l, -1, k, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 4, 1, 2, 2);
                return true;
            }

            // JAVADOC METHOD $$ func_74888_b
            protected int getVillagerType(int par1)
            {
                return par1 == 0 ? 4 : 0;
            }
        }

    public static class House1 extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000517";

            public House1() {}

            public House1(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureVillagePieces.House1 func_74898_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 9, 6, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.House1(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 9 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 7, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 0, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 8, 5, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 1, 8, 6, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 7, 2, 8, 7, 3, Blocks.cobblestone, Blocks.cobblestone, false);
                int i = this.func_151555_a(Blocks.oak_stairs, 3);
                int j = this.func_151555_a(Blocks.oak_stairs, 2);
                int k;
                int l;

                for (k = -1; k <= 2; ++k)
                {
                    for (l = 0; l <= 8; ++l)
                    {
                        this.func_151550_a(par1World, Blocks.oak_stairs, i, l, 6 + k, k, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.oak_stairs, j, l, 6 + k, 5 - k, par3StructureBoundingBox);
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 0, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 5, 8, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 1, 0, 8, 1, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 1, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 4, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 5, 0, 4, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 2, 5, 8, 4, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 2, 0, 8, 4, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 1, 0, 4, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 5, 7, 4, 5, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 2, 1, 8, 4, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 0, 7, 4, 0, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 6, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 6, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 3, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 3, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 3, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 3, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 3, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 6, 2, 5, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 1, 7, 4, 1, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 4, 7, 4, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 4, 7, 3, 4, Blocks.bookshelf, Blocks.bookshelf, false);
                this.func_151550_a(par1World, Blocks.planks, 0, 7, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, this.func_151555_a(Blocks.oak_stairs, 0), 7, 1, 3, par3StructureBoundingBox);
                k = this.func_151555_a(Blocks.oak_stairs, 3);
                this.func_151550_a(par1World, Blocks.oak_stairs, k, 6, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, k, 5, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, k, 4, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, k, 3, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 6, 1, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wooden_pressure_plate, 0, 6, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 4, 1, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wooden_pressure_plate, 0, 4, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.crafting_table, 0, 7, 1, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 1, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 1, 2, 0, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 1, 1, 0, this.func_151555_a(Blocks.wooden_door, 1));

                if (this.func_151548_a(par1World, 1, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 1, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 1, 0, -1, par3StructureBoundingBox);
                }

                for (l = 0; l < 6; ++l)
                {
                    for (int i1 = 0; i1 < 9; ++i1)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, i1, 9, l, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, i1, -1, l, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 2, 1, 2, 1);
                return true;
            }

            // JAVADOC METHOD $$ func_74888_b
            protected int getVillagerType(int par1)
            {
                return 1;
            }
        }

    public static class Church extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000525";

            public Church() {}

            public Church(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureVillagePieces.Church func_74919_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 12, 9, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.Church(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 12 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 3, 3, 7, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 1, 3, 9, 3, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 3, 0, 8, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 0, 3, 10, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 10, 3, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 10, 3, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 4, 0, 4, 7, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 4, 4, 4, 7, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 8, 3, 4, 8, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 4, 3, 10, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 5, 3, 5, 7, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 9, 0, 4, 9, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 0, 4, 4, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 11, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 11, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 2, 11, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 2, 11, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 1, 1, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 1, 1, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 2, 1, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 3, 1, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 3, 1, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 1, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 2, 1, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 3, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 1), 1, 2, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 0), 3, 2, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 3, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 3, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 6, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 7, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 6, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 7, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 6, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 7, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 6, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 7, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 3, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 3, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 3, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 2, 4, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 1, 4, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 3, 4, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 2, 4, 5, par3StructureBoundingBox);
                int i = this.func_151555_a(Blocks.ladder, 4);
                int j;

                for (j = 1; j <= 9; ++j)
                {
                    this.func_151550_a(par1World, Blocks.ladder, i, 3, j, 3, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.air, 0, 2, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 2, 0, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 2, 1, 0, this.func_151555_a(Blocks.wooden_door, 1));

                if (this.func_151548_a(par1World, 2, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 2, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 2, 0, -1, par3StructureBoundingBox);
                }

                for (j = 0; j < 9; ++j)
                {
                    for (int k = 0; k < 5; ++k)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, k, 12, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, k, -1, j, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 2, 1, 2, 1);
                return true;
            }

            // JAVADOC METHOD $$ func_74888_b
            protected int getVillagerType(int par1)
            {
                return 2;
            }
        }

    public static class House4Garden extends StructureVillagePieces.Village
        {
            private boolean isRoofAccessible;
            private static final String __OBFID = "CL_00000523";

            public House4Garden() {}

            public House4Garden(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
                this.isRoofAccessible = par3Random.nextBoolean();
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Terrace", this.isRoofAccessible);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.isRoofAccessible = par1NBTTagCompound.getBoolean("Terrace");
            }

            public static StructureVillagePieces.House4Garden func_74912_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 6, 5, par6);
                return StructureComponent.findIntersecting(par1List, structureboundingbox) != null ? null : new StructureVillagePieces.House4Garden(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6);
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 6 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 0, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 0, 4, 4, 4, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 1, 3, 4, 3, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 0, 3, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 3, 4, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 4, 3, 3, 4, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 1, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 1, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 1, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 2, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 3, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 3, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 3, 1, 0, par3StructureBoundingBox);

                if (this.func_151548_a(par1World, 2, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 2, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 2, 0, -1, par3StructureBoundingBox);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 3, 3, 3, Blocks.air, Blocks.air, false);

                if (this.isRoofAccessible)
                {
                    this.func_151550_a(par1World, Blocks.fence, 0, 0, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 1, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 2, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 3, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 4, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 0, 5, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 1, 5, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 2, 5, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 3, 5, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 4, 5, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 4, 5, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 4, 5, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 4, 5, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 0, 5, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 0, 5, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 0, 5, 3, par3StructureBoundingBox);
                }

                int i;

                if (this.isRoofAccessible)
                {
                    i = this.func_151555_a(Blocks.ladder, 3);
                    this.func_151550_a(par1World, Blocks.ladder, i, 3, 1, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.ladder, i, 3, 2, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.ladder, i, 3, 3, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.ladder, i, 3, 4, 3, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.torch, 0, 2, 3, 1, par3StructureBoundingBox);

                for (i = 0; i < 5; ++i)
                {
                    for (int j = 0; j < 5; ++j)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j, 6, i, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 1, 1, 2, 1);
                return true;
            }
        }

    public static class Path extends StructureVillagePieces.Road
        {
            private int averageGroundLevel;
            private static final String __OBFID = "CL_00000528";

            public Path() {}

            public Path(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
                this.averageGroundLevel = Math.max(par4StructureBoundingBox.getXSize(), par4StructureBoundingBox.getZSize());
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("Length", this.averageGroundLevel);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.averageGroundLevel = par1NBTTagCompound.getInteger("Length");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                boolean flag = false;
                int i;
                StructureComponent structurecomponent1;

                for (i = par3Random.nextInt(5); i < this.averageGroundLevel - 8; i += 2 + par3Random.nextInt(5))
                {
                    structurecomponent1 = this.getNextComponentNN((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, 0, i);

                    if (structurecomponent1 != null)
                    {
                        i += Math.max(structurecomponent1.boundingBox.getXSize(), structurecomponent1.boundingBox.getZSize());
                        flag = true;
                    }
                }

                for (i = par3Random.nextInt(5); i < this.averageGroundLevel - 8; i += 2 + par3Random.nextInt(5))
                {
                    structurecomponent1 = this.getNextComponentPP((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, 0, i);

                    if (structurecomponent1 != null)
                    {
                        i += Math.max(structurecomponent1.boundingBox.getXSize(), structurecomponent1.boundingBox.getZSize());
                        flag = true;
                    }
                }

                if (flag && par3Random.nextInt(3) > 0)
                {
                    switch (this.coordBaseMode)
                    {
                        case 0:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 1, this.getComponentType());
                            break;
                        case 1:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
                            break;
                        case 2:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, this.getComponentType());
                            break;
                        case 3:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    }
                }

                if (flag && par3Random.nextInt(3) > 0)
                {
                    switch (this.coordBaseMode)
                    {
                        case 0:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 3, this.getComponentType());
                            break;
                        case 1:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                            break;
                        case 2:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, this.getComponentType());
                            break;
                        case 3:
                            StructureVillagePieces.getNextComponentVillagePath((StructureVillagePieces.Start)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    }
                }
            }

            public static StructureBoundingBox func_74933_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6)
            {
                for (int i1 = 7 * MathHelper.getRandomIntegerInRange(par2Random, 3, 5); i1 >= 7; i1 -= 7)
                {
                    StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 3, i1, par6);

                    if (StructureComponent.findIntersecting(par1List, structureboundingbox) == null)
                    {
                        return structureboundingbox;
                    }
                }

                return null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                Block block = this.func_151558_b(Blocks.gravel, 0);

                for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i)
                {
                    for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j)
                    {
                        if (par3StructureBoundingBox.isVecInside(i, 64, j))
                        {
                            int k = par1World.getTopSolidOrLiquidBlock(i, j) - 1;
                            par1World.func_147465_d(i, k, j, block, 0, 2);
                        }
                    }
                }

                return true;
            }
        }

    public static class House2 extends StructureVillagePieces.Village
        {
            // JAVADOC FIELD $$ field_74918_a
            public static final WeightedRandomChestContent[] villageBlacksmithChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5), new WeightedRandomChestContent(Item.func_150898_a(Blocks.obsidian), 0, 3, 7, 5), new WeightedRandomChestContent(Item.func_150898_a(Blocks.sapling), 0, 3, 7, 5), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
            private boolean hasMadeChest;
            private static final String __OBFID = "CL_00000526";

            public House2() {}

            public House2(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureVillagePieces.House2 func_74915_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 10, 6, 7, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.House2(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Chest", this.hasMadeChest);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.hasMadeChest = par1NBTTagCompound.getBoolean("Chest");
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 6 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 9, 4, 6, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 9, 0, 6, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 0, 9, 4, 6, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 9, 5, 6, Blocks.stone_slab, Blocks.stone_slab, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 1, 8, 5, 5, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 0, 2, 3, 0, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 0, 4, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 0, 3, 4, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 6, 0, 4, 6, Blocks.log, Blocks.log, false);
                this.func_151550_a(par1World, Blocks.planks, 0, 3, 3, 1, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 3, 2, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 3, 5, 3, 3, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 5, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 6, 5, 3, 6, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 1, 0, 5, 3, 0, Blocks.fence, Blocks.fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 1, 0, 9, 3, 0, Blocks.fence, Blocks.fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 1, 4, 9, 4, 6, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151550_a(par1World, Blocks.flowing_lava, 0, 7, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.flowing_lava, 0, 8, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.iron_bars, 0, 9, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.iron_bars, 0, 9, 2, 4, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 2, 4, 8, 2, 5, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, 1, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.furnace, 0, 6, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.furnace, 0, 6, 3, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.double_stone_slab, 0, 8, 1, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 4, 2, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 2, 1, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wooden_pressure_plate, 0, 2, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 1, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, this.func_151555_a(Blocks.oak_stairs, 3), 2, 1, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, this.func_151555_a(Blocks.oak_stairs, 1), 1, 1, 4, par3StructureBoundingBox);
                int i;
                int j;

                if (!this.hasMadeChest)
                {
                    i = this.getYWithOffset(1);
                    j = this.getXWithOffset(5, 5);
                    int k = this.getZWithOffset(5, 5);

                    if (par3StructureBoundingBox.isVecInside(j, i, k))
                    {
                        this.hasMadeChest = true;
                        this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 5, 1, 5, ChestGenHooks.getItems(VILLAGE_BLACKSMITH, par2Random), ChestGenHooks.getCount(VILLAGE_BLACKSMITH, par2Random));
                    }
                }

                for (i = 6; i <= 8; ++i)
                {
                    if (this.func_151548_a(par1World, i, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, i, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                    {
                        this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), i, 0, -1, par3StructureBoundingBox);
                    }
                }

                for (i = 0; i < 7; ++i)
                {
                    for (j = 0; j < 10; ++j)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j, 6, i, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 7, 1, 1, 1);
                return true;
            }

            // JAVADOC METHOD $$ func_74888_b
            protected int getVillagerType(int par1)
            {
                return 3;
            }
        }

    public static class Start extends StructureVillagePieces.Well
        {
            public WorldChunkManager worldChunkMngr;
            // JAVADOC FIELD $$ field_74927_b
            public boolean inDesert;
            // JAVADOC FIELD $$ field_74928_c
            public int terrainType;
            public StructureVillagePieces.PieceWeight structVillagePieceWeight;
            // JAVADOC FIELD $$ field_74931_h
            public List structureVillageWeightedPieceList;
            public List field_74932_i = new ArrayList();
            public List field_74930_j = new ArrayList();
            private static final String __OBFID = "CL_00000527";
            public BiomeGenBase biome;

            public Start() {}

            public Start(WorldChunkManager par1WorldChunkManager, int par2, Random par3Random, int par4, int par5, List par6List, int par7)
            {
                super((StructureVillagePieces.Start)null, 0, par3Random, par4, par5);
                this.worldChunkMngr = par1WorldChunkManager;
                this.structureVillageWeightedPieceList = par6List;
                this.terrainType = par7;
                BiomeGenBase biomegenbase = par1WorldChunkManager.getBiomeGenAt(par4, par5);
                this.inDesert = biomegenbase == BiomeGenBase.desert || biomegenbase == BiomeGenBase.desertHills;
                this.biome = biomegenbase;
            }

            public WorldChunkManager getWorldChunkManager()
            {
                return this.worldChunkMngr;
            }
        }

    public static class House3 extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000530";

            public House3() {}

            public House3(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureVillagePieces.House3 func_74921_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 7, 12, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.House3(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 7 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 0, 5, 8, 0, 10, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 0, 0, 8, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 7, 2, 0, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 5, 2, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 0, 6, 2, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 0, 10, 7, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 0, 7, 3, 0, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 5, 2, 3, 5, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 1, 8, 4, 1, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 4, 4, 3, 4, 4, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 2, 8, 5, 3, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.planks, 0, 0, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 0, 4, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 4, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 4, 4, par3StructureBoundingBox);
                int i = this.func_151555_a(Blocks.oak_stairs, 3);
                int j = this.func_151555_a(Blocks.oak_stairs, 2);
                int k;
                int l;

                for (k = -1; k <= 2; ++k)
                {
                    for (l = 0; l <= 8; ++l)
                    {
                        this.func_151550_a(par1World, Blocks.oak_stairs, i, l, 4 + k, k, par3StructureBoundingBox);

                        if ((k > -1 || l <= 1) && (k > 0 || l <= 3) && (k > 1 || l <= 4 || l >= 6))
                        {
                            this.func_151550_a(par1World, Blocks.oak_stairs, j, l, 4 + k, 5 - k, par3StructureBoundingBox);
                        }
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 4, 5, 3, 4, 10, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 4, 2, 7, 4, 10, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 4, 4, 5, 10, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 5, 4, 6, 5, 10, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 6, 3, 5, 6, 10, Blocks.planks, Blocks.planks, false);
                k = this.func_151555_a(Blocks.oak_stairs, 0);
                int i1;

                for (l = 4; l >= 1; --l)
                {
                    this.func_151550_a(par1World, Blocks.planks, 0, l, 2 + l, 7 - l, par3StructureBoundingBox);

                    for (i1 = 8 - l; i1 <= 10; ++i1)
                    {
                        this.func_151550_a(par1World, Blocks.oak_stairs, k, l, 2 + l, i1, par3StructureBoundingBox);
                    }
                }

                l = this.func_151555_a(Blocks.oak_stairs, 1);
                this.func_151550_a(par1World, Blocks.planks, 0, 6, 6, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 7, 5, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.oak_stairs, l, 6, 6, 4, par3StructureBoundingBox);
                int j1;

                for (i1 = 6; i1 <= 8; ++i1)
                {
                    for (j1 = 5; j1 <= 10; ++j1)
                    {
                        this.func_151550_a(par1World, Blocks.oak_stairs, l, i1, 12 - i1, j1, par3StructureBoundingBox);
                    }
                }

                this.func_151550_a(par1World, Blocks.log, 0, 0, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 0, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 4, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 6, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 8, 2, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 8, 2, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 8, 2, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 2, 2, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 2, 2, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 2, 2, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 4, 4, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 5, 4, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 6, 4, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.planks, 0, 5, 5, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 2, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 2, 3, 1, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 2, 1, 0, this.func_151555_a(Blocks.wooden_door, 1));
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, -1, 3, 2, -1, Blocks.air, Blocks.air, false);

                if (this.func_151548_a(par1World, 2, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 2, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 2, 0, -1, par3StructureBoundingBox);
                }

                for (i1 = 0; i1 < 5; ++i1)
                {
                    for (j1 = 0; j1 < 9; ++j1)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j1, 7, i1, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, j1, -1, i1, par3StructureBoundingBox);
                    }
                }

                for (i1 = 5; i1 < 11; ++i1)
                {
                    for (j1 = 2; j1 < 9; ++j1)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j1, 7, i1, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, j1, -1, i1, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 4, 1, 2, 2);
                return true;
            }
        }

    public static class WoodHut extends StructureVillagePieces.Village
        {
            private boolean isTallHouse;
            private int tablePosition;
            private static final String __OBFID = "CL_00000524";

            public WoodHut() {}

            public WoodHut(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
                this.isTallHouse = par3Random.nextBoolean();
                this.tablePosition = par3Random.nextInt(3);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("T", this.tablePosition);
                par1NBTTagCompound.setBoolean("C", this.isTallHouse);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.tablePosition = par1NBTTagCompound.getInteger("T");
                this.isTallHouse = par1NBTTagCompound.getBoolean("C");
            }

            public static StructureVillagePieces.WoodHut func_74908_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 4, 6, 5, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.WoodHut(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 6 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 3, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 3, 0, 4, Blocks.cobblestone, Blocks.cobblestone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 3, Blocks.dirt, Blocks.dirt, false);

                if (this.isTallHouse)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 1, 2, 4, 3, Blocks.log, Blocks.log, false);
                }
                else
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 1, 2, 5, 3, Blocks.log, Blocks.log, false);
                }

                this.func_151550_a(par1World, Blocks.log, 0, 1, 4, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 2, 4, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 1, 4, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 2, 4, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 0, 4, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 0, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 0, 4, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 3, 4, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 3, 4, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.log, 0, 3, 4, 3, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 0, 3, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 0, 3, 3, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 4, 0, 3, 4, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 4, 3, 3, 4, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 1, 3, 3, 3, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 0, 2, 3, 0, Blocks.planks, Blocks.planks, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 4, 2, 3, 4, Blocks.planks, Blocks.planks, false);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 0, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.glass_pane, 0, 3, 2, 2, par3StructureBoundingBox);

                if (this.tablePosition > 0)
                {
                    this.func_151550_a(par1World, Blocks.fence, 0, this.tablePosition, 1, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wooden_pressure_plate, 0, this.tablePosition, 2, 3, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.air, 0, 1, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 1, 2, 0, par3StructureBoundingBox);
                this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 1, 1, 0, this.func_151555_a(Blocks.wooden_door, 1));

                if (this.func_151548_a(par1World, 1, 0, -1, par3StructureBoundingBox).func_149688_o() == Material.field_151579_a && this.func_151548_a(par1World, 1, -1, -1, par3StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                {
                    this.func_151550_a(par1World, Blocks.stone_stairs, this.func_151555_a(Blocks.stone_stairs, 3), 1, 0, -1, par3StructureBoundingBox);
                }

                for (int i = 0; i < 5; ++i)
                {
                    for (int j = 0; j < 4; ++j)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j, 6, i, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.cobblestone, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                this.spawnVillagers(par1World, par3StructureBoundingBox, 1, 1, 2, 1);
                return true;
            }
        }

    public static class Field1 extends StructureVillagePieces.Village
        {
            // JAVADOC FIELD $$ field_82679_b
            private Block cropTypeA;
            // JAVADOC FIELD $$ field_82680_c
            private Block cropTypeB;
            // JAVADOC FIELD $$ field_82678_d
            private Block cropTypeC;
            // JAVADOC FIELD $$ field_82681_h
            private Block cropTypeD;
            private static final String __OBFID = "CL_00000518";

            public Field1() {}

            public Field1(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
                this.cropTypeA = this.func_151559_a(par3Random);
                this.cropTypeB = this.func_151559_a(par3Random);
                this.cropTypeC = this.func_151559_a(par3Random);
                this.cropTypeD = this.func_151559_a(par3Random);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("CA", Block.field_149771_c.func_148757_b(this.cropTypeA));
                par1NBTTagCompound.setInteger("CB", Block.field_149771_c.func_148757_b(this.cropTypeB));
                par1NBTTagCompound.setInteger("CC", Block.field_149771_c.func_148757_b(this.cropTypeC));
                par1NBTTagCompound.setInteger("CD", Block.field_149771_c.func_148757_b(this.cropTypeD));
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.cropTypeA = Block.func_149729_e(par1NBTTagCompound.getInteger("CA"));
                this.cropTypeB = Block.func_149729_e(par1NBTTagCompound.getInteger("CB"));
                this.cropTypeC = Block.func_149729_e(par1NBTTagCompound.getInteger("CC"));
                this.cropTypeD = Block.func_149729_e(par1NBTTagCompound.getInteger("CD"));
            }

            private Block func_151559_a(Random p_151559_1_)
            {
                switch (p_151559_1_.nextInt(5))
                {
                    case 0:
                        return Blocks.carrots;
                    case 1:
                        return Blocks.potatoes;
                    default:
                        return Blocks.wheat;
                }
            }

            public static StructureVillagePieces.Field1 func_74900_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.Field1(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 4 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 12, 4, 8, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 0, 1, 8, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 10, 0, 1, 11, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 0, 0, 6, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 12, 0, 0, 12, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 11, 0, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 8, 11, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.water, Blocks.water, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 0, 1, 9, 0, 7, Blocks.water, Blocks.water, false);
                int i;

                for (i = 1; i <= 7; ++i)
                {
                    this.func_151550_a(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 1, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 2, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 4, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 5, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeC, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 7, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeC, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 8, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeD, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 10, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeD, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 11, 1, i, par3StructureBoundingBox);
                }

                for (i = 0; i < 9; ++i)
                {
                    for (int j = 0; j < 13; ++j)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j, 4, i, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.dirt, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Field2 extends StructureVillagePieces.Village
        {
            // JAVADOC FIELD $$ field_82675_b
            private Block cropTypeA;
            // JAVADOC FIELD $$ field_82676_c
            private Block cropTypeB;
            private static final String __OBFID = "CL_00000519";

            public Field2() {}

            public Field2(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
                this.cropTypeA = this.func_151560_a(par3Random);
                this.cropTypeB = this.func_151560_a(par3Random);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("CA", Block.field_149771_c.func_148757_b(this.cropTypeA));
                par1NBTTagCompound.setInteger("CB", Block.field_149771_c.func_148757_b(this.cropTypeB));
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.cropTypeA = Block.func_149729_e(par1NBTTagCompound.getInteger("CA"));
                this.cropTypeB = Block.func_149729_e(par1NBTTagCompound.getInteger("CB"));
            }

            private Block func_151560_a(Random p_151560_1_)
            {
                switch (p_151560_1_.nextInt(5))
                {
                    case 0:
                        return Blocks.carrots;
                    case 1:
                        return Blocks.potatoes;
                    default:
                        return Blocks.wheat;
                }
            }

            public static StructureVillagePieces.Field2 func_74902_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 7, 4, 9, par6);
                return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new StructureVillagePieces.Field2(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 4 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 0, 6, 4, 8, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.farmland, Blocks.farmland, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 0, 0, 6, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 5, 0, 0, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 8, 5, 0, 8, Blocks.log, Blocks.log, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.water, Blocks.water, false);
                int i;

                for (i = 1; i <= 7; ++i)
                {
                    this.func_151550_a(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 1, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 2, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 4, 1, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 5, 1, i, par3StructureBoundingBox);
                }

                for (i = 0; i < 9; ++i)
                {
                    for (int j = 0; j < 7; ++j)
                    {
                        this.clearCurrentPositionBlocksUpwards(par1World, j, 4, i, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.dirt, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public abstract static class Road extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000532";

            public Road() {}

            protected Road(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2)
            {
                super(par1ComponentVillageStartPiece, par2);
            }
        }

    public static class Torch extends StructureVillagePieces.Village
        {
            private static final String __OBFID = "CL_00000520";

            public Torch() {}

            public Torch(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
            {
                super(par1ComponentVillageStartPiece, par2);
                this.coordBaseMode = par5;
                this.boundingBox = par4StructureBoundingBox;
            }

            public static StructureBoundingBox func_74904_a(StructureVillagePieces.Start par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 4, 2, par6);
                return StructureComponent.findIntersecting(par1List, structureboundingbox) != null ? null : structureboundingbox;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.field_143015_k < 0)
                {
                    this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

                    if (this.field_143015_k < 0)
                    {
                        return true;
                    }

                    this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 4 - 1, 0);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 2, 3, 1, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 0, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 1, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.fence, 0, 1, 2, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, 15, 1, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 0, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 1, 3, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 2, 3, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.torch, 0, 1, 3, -1, par3StructureBoundingBox);
                return true;
            }
        }

    public static class PieceWeight
        {
            // JAVADOC FIELD $$ field_75090_a
            public Class villagePieceClass;
            public final int villagePieceWeight;
            public int villagePiecesSpawned;
            public int villagePiecesLimit;
            private static final String __OBFID = "CL_00000521";

            public PieceWeight(Class par1Class, int par2, int par3)
            {
                this.villagePieceClass = par1Class;
                this.villagePieceWeight = par2;
                this.villagePiecesLimit = par3;
            }

            public boolean canSpawnMoreVillagePiecesOfType(int par1)
            {
                return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
            }

            public boolean canSpawnMoreVillagePieces()
            {
                return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
            }
        }
}