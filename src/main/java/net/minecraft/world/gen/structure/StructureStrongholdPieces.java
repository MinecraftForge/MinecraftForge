package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import net.minecraftforge.common.ChestGenHooks;
import static net.minecraftforge.common.ChestGenHooks.*;

public class StructureStrongholdPieces
{
    private static final StructureStrongholdPieces.PieceWeight[] pieceWeightArray = new StructureStrongholdPieces.PieceWeight[] {new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Straight.class, 40, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Prison.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.LeftTurn.class, 20, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RightTurn.class, 20, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RoomCrossing.class, 10, 6), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.StairsStraight.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Stairs.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Crossing.class, 5, 4), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.ChestCorridor.class, 5, 4), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Library.class, 10, 2)
    {
        private static final String __OBFID = "CL_00000484";
        public boolean canSpawnMoreStructuresOfType(int par1)
        {
            return super.canSpawnMoreStructuresOfType(par1) && par1 > 4;
        }
    }, new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1)
    {
        private static final String __OBFID = "CL_00000485";
        public boolean canSpawnMoreStructuresOfType(int par1)
        {
            return super.canSpawnMoreStructuresOfType(par1) && par1 > 5;
        }
    }
                                                                                                                             };
    private static List structurePieceList;
    private static Class strongComponentType;
    static int totalWeight;
    private static final StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones(null);
    private static final String __OBFID = "CL_00000483";

    public static void func_143046_a()
    {
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.ChestCorridor.class, "SHCC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Corridor.class, "SHFC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Crossing.class, "SH5C");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.LeftTurn.class, "SHLT");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Library.class, "SHLi");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.PortalRoom.class, "SHPR");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Prison.class, "SHPH");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.RightTurn.class, "SHRT");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.RoomCrossing.class, "SHRC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Stairs.class, "SHSD");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Stairs2.class, "SHStart");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Straight.class, "SHS");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces.StairsStraight.class, "SHSSD");
    }

    // JAVADOC METHOD $$ func_75198_a
    public static void prepareStructurePieces()
    {
        structurePieceList = new ArrayList();
        StructureStrongholdPieces.PieceWeight[] apieceweight = pieceWeightArray;
        int i = apieceweight.length;

        for (int j = 0; j < i; ++j)
        {
            StructureStrongholdPieces.PieceWeight pieceweight = apieceweight[j];
            pieceweight.instancesSpawned = 0;
            structurePieceList.add(pieceweight);
        }

        strongComponentType = null;
    }

    private static boolean canAddStructurePieces()
    {
        boolean flag = false;
        totalWeight = 0;
        StructureStrongholdPieces.PieceWeight pieceweight;

        for (Iterator iterator = structurePieceList.iterator(); iterator.hasNext(); totalWeight += pieceweight.pieceWeight)
        {
            pieceweight = (StructureStrongholdPieces.PieceWeight)iterator.next();

            if (pieceweight.instancesLimit > 0 && pieceweight.instancesSpawned < pieceweight.instancesLimit)
            {
                flag = true;
            }
        }

        return flag;
    }

    // JAVADOC METHOD $$ func_75200_a
    private static StructureStrongholdPieces.Stronghold getStrongholdComponentFromWeightedPiece(Class par0Class, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        Object object = null;

        if (par0Class == StructureStrongholdPieces.Straight.class)
        {
            object = StructureStrongholdPieces.Straight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.Prison.class)
        {
            object = StructureStrongholdPieces.Prison.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.LeftTurn.class)
        {
            object = StructureStrongholdPieces.LeftTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.RightTurn.class)
        {
            object = StructureStrongholdPieces.RightTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.RoomCrossing.class)
        {
            object = StructureStrongholdPieces.RoomCrossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.StairsStraight.class)
        {
            object = StructureStrongholdPieces.StairsStraight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.Stairs.class)
        {
            object = StructureStrongholdPieces.Stairs.getStrongholdStairsComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.Crossing.class)
        {
            object = StructureStrongholdPieces.Crossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.ChestCorridor.class)
        {
            object = StructureStrongholdPieces.ChestCorridor.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.Library.class)
        {
            object = StructureStrongholdPieces.Library.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == StructureStrongholdPieces.PortalRoom.class)
        {
            object = StructureStrongholdPieces.PortalRoom.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }

        return (StructureStrongholdPieces.Stronghold)object;
    }

    private static StructureStrongholdPieces.Stronghold getNextComponent(StructureStrongholdPieces.Stairs2 par0ComponentStrongholdStairs2, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (!canAddStructurePieces())
        {
            return null;
        }
        else
        {
            if (strongComponentType != null)
            {
                StructureStrongholdPieces.Stronghold stronghold = getStrongholdComponentFromWeightedPiece(strongComponentType, par1List, par2Random, par3, par4, par5, par6, par7);
                strongComponentType = null;

                if (stronghold != null)
                {
                    return stronghold;
                }
            }

            int k1 = 0;

            while (k1 < 5)
            {
                ++k1;
                int j1 = par2Random.nextInt(totalWeight);
                Iterator iterator = structurePieceList.iterator();

                while (iterator.hasNext())
                {
                    StructureStrongholdPieces.PieceWeight pieceweight = (StructureStrongholdPieces.PieceWeight)iterator.next();
                    j1 -= pieceweight.pieceWeight;

                    if (j1 < 0)
                    {
                        if (!pieceweight.canSpawnMoreStructuresOfType(par7) || pieceweight == par0ComponentStrongholdStairs2.strongholdPieceWeight)
                        {
                            break;
                        }

                        StructureStrongholdPieces.Stronghold stronghold1 = getStrongholdComponentFromWeightedPiece(pieceweight.pieceClass, par1List, par2Random, par3, par4, par5, par6, par7);

                        if (stronghold1 != null)
                        {
                            ++pieceweight.instancesSpawned;
                            par0ComponentStrongholdStairs2.strongholdPieceWeight = pieceweight;

                            if (!pieceweight.canSpawnMoreStructures())
                            {
                                structurePieceList.remove(pieceweight);
                            }

                            return stronghold1;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = StructureStrongholdPieces.Corridor.func_74992_a(par1List, par2Random, par3, par4, par5, par6);

            if (structureboundingbox != null && structureboundingbox.minY > 1)
            {
                return new StructureStrongholdPieces.Corridor(par7, par2Random, structureboundingbox, par6);
            }
            else
            {
                return null;
            }
        }
    }

    private static StructureComponent getNextValidComponent(StructureStrongholdPieces.Stairs2 par0ComponentStrongholdStairs2, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 50)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentStrongholdStairs2.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentStrongholdStairs2.getBoundingBox().minZ) <= 112)
        {
            StructureStrongholdPieces.Stronghold stronghold = getNextComponent(par0ComponentStrongholdStairs2, par1List, par2Random, par3, par4, par5, par6, par7 + 1);

            if (stronghold != null)
            {
                par1List.add(stronghold);
                par0ComponentStrongholdStairs2.field_75026_c.add(stronghold);
            }

            return stronghold;
        }
        else
        {
            return null;
        }
    }

    public static class Stairs extends StructureStrongholdPieces.Stronghold
        {
            private boolean field_75024_a;
            private static final String __OBFID = "CL_00000498";

            public Stairs() {}

            public Stairs(int par1, Random par2Random, int par3, int par4)
            {
                super(par1);
                this.field_75024_a = true;
                this.coordBaseMode = par2Random.nextInt(4);
                this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;

                switch (this.coordBaseMode)
                {
                    case 0:
                    case 2:
                        this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
                        break;
                    default:
                        this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
                }
            }

            public Stairs(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.field_75024_a = false;
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Source", this.field_75024_a);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_75024_a = par1NBTTagCompound.getBoolean("Source");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                if (this.field_75024_a)
                {
                    StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
                }

                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
            }

            // JAVADOC METHOD $$ func_75022_a
            public static StructureStrongholdPieces.Stairs getStrongholdStairsComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 11, 5, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.Stairs(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 4, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 7, 0);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 6, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 5, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 1, 6, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 5, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 4, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 1, 5, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 4, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 3, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 3, 4, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 2, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 3, 3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 2, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 1, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 1, 2, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 1, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 0, 1, 1, 3, par3StructureBoundingBox);
                    return true;
                }
            }
        }

    public static class Straight extends StructureStrongholdPieces.Stronghold
        {
            private boolean expandsX;
            private boolean expandsZ;
            private static final String __OBFID = "CL_00000500";

            public Straight() {}

            public Straight(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
                this.expandsX = par2Random.nextInt(2) == 0;
                this.expandsZ = par2Random.nextInt(2) == 0;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Left", this.expandsX);
                par1NBTTagCompound.setBoolean("Right", this.expandsZ);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.expandsX = par1NBTTagCompound.getBoolean("Left");
                this.expandsZ = par1NBTTagCompound.getBoolean("Right");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);

                if (this.expandsX)
                {
                    this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 2);
                }

                if (this.expandsZ)
                {
                    this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 2);
                }
            }

            public static StructureStrongholdPieces.Straight findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.Straight(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
                    this.func_151552_a(par1World, par3StructureBoundingBox, par2Random, 0.1F, 1, 2, 1, Blocks.torch, 0);
                    this.func_151552_a(par1World, par3StructureBoundingBox, par2Random, 0.1F, 3, 2, 1, Blocks.torch, 0);
                    this.func_151552_a(par1World, par3StructureBoundingBox, par2Random, 0.1F, 1, 2, 5, Blocks.torch, 0);
                    this.func_151552_a(par1World, par3StructureBoundingBox, par2Random, 0.1F, 3, 2, 5, Blocks.torch, 0);

                    if (this.expandsX)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 2, 0, 3, 4, Blocks.air, Blocks.air, false);
                    }

                    if (this.expandsZ)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 2, 4, 3, 4, Blocks.air, Blocks.air, false);
                    }

                    return true;
                }
            }
        }

    public static class Library extends StructureStrongholdPieces.Stronghold
        {
            // JAVADOC FIELD $$ field_75007_b
            public static final WeightedRandomChestContent[] strongholdLibraryChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.book, 0, 1, 3, 20), new WeightedRandomChestContent(Items.paper, 0, 2, 7, 20), new WeightedRandomChestContent(Items.map, 0, 1, 1, 1), new WeightedRandomChestContent(Items.compass, 0, 1, 1, 1)};
            private boolean isLargeRoom;
            private static final String __OBFID = "CL_00000491";

            public Library() {}

            public Library(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
                this.isLargeRoom = par3StructureBoundingBox.getYSize() > 6;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Tall", this.isLargeRoom);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.isLargeRoom = par1NBTTagCompound.getBoolean("Tall");
            }

            public static StructureStrongholdPieces.Library findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 11, 15, par5);

                if (!canStrongholdGoDeeper(structureboundingbox) || StructureComponent.findIntersecting(par0List, structureboundingbox) != null)
                {
                    structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 6, 15, par5);

                    if (!canStrongholdGoDeeper(structureboundingbox) || StructureComponent.findIntersecting(par0List, structureboundingbox) != null)
                    {
                        return null;
                    }
                }

                return new StructureStrongholdPieces.Library(par6, par1Random, structureboundingbox, par5);
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    byte b0 = 11;

                    if (!this.isLargeRoom)
                    {
                        b0 = 6;
                    }

                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 13, b0 - 1, 14, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 4, 1, 0);
                    this.func_151551_a(par1World, par3StructureBoundingBox, par2Random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web, Blocks.web, false);
                    boolean flag = true;
                    boolean flag1 = true;
                    int i;

                    for (i = 1; i <= 13; ++i)
                    {
                        if ((i - 1) % 4 == 0)
                        {
                            this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, i, 1, 4, i, Blocks.planks, Blocks.planks, false);
                            this.func_151549_a(par1World, par3StructureBoundingBox, 12, 1, i, 12, 4, i, Blocks.planks, Blocks.planks, false);
                            this.func_151550_a(par1World, Blocks.torch, 0, 2, 3, i, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.torch, 0, 11, 3, i, par3StructureBoundingBox);

                            if (this.isLargeRoom)
                            {
                                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 6, i, 1, 9, i, Blocks.planks, Blocks.planks, false);
                                this.func_151549_a(par1World, par3StructureBoundingBox, 12, 6, i, 12, 9, i, Blocks.planks, Blocks.planks, false);
                            }
                        }
                        else
                        {
                            this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, i, 1, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);
                            this.func_151549_a(par1World, par3StructureBoundingBox, 12, 1, i, 12, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);

                            if (this.isLargeRoom)
                            {
                                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 6, i, 1, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
                                this.func_151549_a(par1World, par3StructureBoundingBox, 12, 6, i, 12, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
                            }
                        }
                    }

                    for (i = 3; i < 12; i += 2)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, i, 4, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 6, 1, i, 7, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 9, 1, i, 10, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
                    }

                    if (this.isLargeRoom)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.planks, Blocks.planks, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.planks, Blocks.planks, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.planks, Blocks.planks, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.planks, Blocks.planks, false);
                        this.func_151550_a(par1World, Blocks.planks, 0, 9, 5, 11, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.planks, 0, 8, 5, 11, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.planks, 0, 9, 5, 10, par3StructureBoundingBox);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 3, 6, 2, 3, 6, 12, Blocks.fence, Blocks.fence, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 10, 6, 2, 10, 6, 10, Blocks.fence, Blocks.fence, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 6, 2, 9, 6, 2, Blocks.fence, Blocks.fence, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 6, 12, 8, 6, 12, Blocks.fence, Blocks.fence, false);
                        this.func_151550_a(par1World, Blocks.fence, 0, 9, 6, 11, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, 8, 6, 11, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, 9, 6, 10, par3StructureBoundingBox);
                        i = this.func_151555_a(Blocks.ladder, 3);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 1, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 2, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 3, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 4, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 5, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 6, 13, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.ladder, i, 10, 7, 13, par3StructureBoundingBox);
                        byte b1 = 7;
                        byte b2 = 7;
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 1, 9, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1, 9, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 1, 8, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1, 8, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 1, 7, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1, 7, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 2, 7, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 + 1, 7, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 1, 7, b2 - 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1 - 1, 7, b2 + 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1, 7, b2 - 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.fence, 0, b1, 7, b2 + 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1 - 2, 8, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1 + 1, 8, b2, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1 - 1, 8, b2 - 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1 - 1, 8, b2 + 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1, 8, b2 - 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.torch, 0, b1, 8, b2 + 1, par3StructureBoundingBox);
                    }

                    ChestGenHooks info = ChestGenHooks.getInfo(STRONGHOLD_LIBRARY);

                    this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 3, 5, info.getItems(par2Random), info.getCount(par2Random));

                    if (this.isLargeRoom)
                    {
                        this.func_151550_a(par1World, Blocks.air, 0, 12, 9, 1, par3StructureBoundingBox);
                        this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 12, 8, 1, info.getItems(par2Random), info.getCount(par2Random));
                    }

                    return true;
                }
            }
        }

    public static class PortalRoom extends StructureStrongholdPieces.Stronghold
        {
            private boolean hasSpawner;
            private static final String __OBFID = "CL_00000493";

            public PortalRoom() {}

            public PortalRoom(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Mob", this.hasSpawner);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.hasSpawner = par1NBTTagCompound.getBoolean("Mob");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                if (par1StructureComponent != null)
                {
                    ((StructureStrongholdPieces.Stairs2)par1StructureComponent).strongholdPortalRoom = this;
                }
            }

            public static StructureStrongholdPieces.PortalRoom findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 11, 8, 16, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.PortalRoom(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 7, 15, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
                byte b0 = 6;
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, b0, 1, 1, b0, 14, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, b0, 1, 9, b0, 14, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, b0, 1, 8, b0, 2, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, b0, 14, 8, b0, 14, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 2, 1, 4, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, 1, 1, 9, 1, 4, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 3, 1, 8, 7, 1, 12, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava, Blocks.flowing_lava, false);
                int i;

                for (i = 3; i < 14; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, i, 0, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 10, 3, i, 10, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
                }

                for (i = 2; i < 9; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 3, 15, i, 4, 15, Blocks.iron_bars, Blocks.iron_bars, false);
                }

                i = this.func_151555_a(Blocks.stone_brick_stairs, 3);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 5, 6, 1, 7, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 2, 6, 6, 2, 7, false, par2Random, StructureStrongholdPieces.strongholdStones);
                this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 3, 7, 6, 3, 7, false, par2Random, StructureStrongholdPieces.strongholdStones);

                for (int j = 4; j <= 6; ++j)
                {
                    this.func_151550_a(par1World, Blocks.stone_brick_stairs, i, j, 1, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_brick_stairs, i, j, 2, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_brick_stairs, i, j, 3, 6, par3StructureBoundingBox);
                }

                byte b4 = 2;
                byte b1 = 0;
                byte b2 = 3;
                byte b3 = 1;

                switch (this.coordBaseMode)
                {
                    case 0:
                        b4 = 0;
                        b1 = 2;
                        break;
                    case 1:
                        b4 = 1;
                        b1 = 3;
                        b2 = 0;
                        b3 = 2;
                    case 2:
                    default:
                        break;
                    case 3:
                        b4 = 3;
                        b1 = 1;
                        b2 = 0;
                        b3 = 2;
                }

                this.func_151550_a(par1World, Blocks.end_portal_frame, b4 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b4 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b4 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b1 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b1 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b1 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b2 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b2 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b2 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 11, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b3 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b3 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.end_portal_frame, b3 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 11, par3StructureBoundingBox);

                if (!this.hasSpawner)
                {
                    int i1 = this.getYWithOffset(3);
                    int k = this.getXWithOffset(5, 6);
                    int l = this.getZWithOffset(5, 6);

                    if (par3StructureBoundingBox.isVecInside(k, i1, l))
                    {
                        this.hasSpawner = true;
                        par1World.func_147465_d(k, i1, l, Blocks.mob_spawner, 0, 2);
                        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)par1World.func_147438_o(k, i1, l);

                        if (tileentitymobspawner != null)
                        {
                            tileentitymobspawner.func_145881_a().setMobID("Silverfish");
                        }
                    }
                }

                return true;
            }
        }

    public static class ChestCorridor extends StructureStrongholdPieces.Stronghold
        {
            // JAVADOC FIELD $$ field_75003_a
            public static final WeightedRandomChestContent[] strongholdChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.ender_pearl, 0, 1, 1, 10), new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5), new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 1), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
            private boolean hasMadeChest;
            private static final String __OBFID = "CL_00000487";

            public ChestCorridor() {}

            public ChestCorridor(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
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

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
            }

            public static StructureStrongholdPieces.ChestCorridor findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.ChestCorridor(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
                    this.func_151550_a(par1World, Blocks.stone_slab, 5, 3, 1, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 5, 3, 1, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 5, 3, 2, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_slab, 5, 3, 2, 4, par3StructureBoundingBox);
                    int i;

                    for (i = 2; i <= 4; ++i)
                    {
                        this.func_151550_a(par1World, Blocks.stone_slab, 5, 2, 1, i, par3StructureBoundingBox);
                    }

                    if (!this.hasMadeChest)
                    {
                        i = this.getYWithOffset(2);
                        int j = this.getXWithOffset(3, 3);
                        int k = this.getZWithOffset(3, 3);

                        if (par3StructureBoundingBox.isVecInside(j, i, k))
                        {
                            this.hasMadeChest = true;
                            this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 2, 3, ChestGenHooks.getItems(STRONGHOLD_CORRIDOR, par2Random), ChestGenHooks.getCount(STRONGHOLD_CORRIDOR, par2Random));
                        }
                    }

                    return true;
                }
            }
        }

    public static class RoomCrossing extends StructureStrongholdPieces.Stronghold
        {
            // JAVADOC FIELD $$ field_75014_c
            public static final WeightedRandomChestContent[] strongholdRoomCrossingChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1)};
            protected int roomType;
            private static final String __OBFID = "CL_00000496";

            public RoomCrossing() {}

            public RoomCrossing(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
                this.roomType = par2Random.nextInt(5);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("Type", this.roomType);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.roomType = par1NBTTagCompound.getInteger("Type");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 4, 1);
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 4);
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 4);
            }

            public static StructureStrongholdPieces.RoomCrossing findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 11, 7, 11, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.RoomCrossing(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 6, 10, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 4, 1, 0);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 10, 6, 3, 10, Blocks.air, Blocks.air, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 4, 0, 3, 6, Blocks.air, Blocks.air, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 10, 1, 4, 10, 3, 6, Blocks.air, Blocks.air, false);
                    int i;

                    switch (this.roomType)
                    {
                        case 0:
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 2, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 3, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.torch, 0, 4, 3, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.torch, 0, 6, 3, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.torch, 0, 5, 3, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.torch, 0, 5, 3, 6, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 4, 1, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 4, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 4, 1, 6, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 6, 1, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 6, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 6, 1, 6, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 5, 1, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stone_slab, 0, 5, 1, 6, par3StructureBoundingBox);
                            break;
                        case 1:
                            for (i = 0; i < 5; ++i)
                            {
                                this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 1, 3 + i, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.stonebrick, 0, 7, 1, 3 + i, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.stonebrick, 0, 3 + i, 1, 3, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.stonebrick, 0, 3 + i, 1, 7, par3StructureBoundingBox);
                            }

                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 2, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 5, 3, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.flowing_water, 0, 5, 4, 5, par3StructureBoundingBox);
                            break;
                        case 2:
                            for (i = 1; i <= 9; ++i)
                            {
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 1, 3, i, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 9, 3, i, par3StructureBoundingBox);
                            }

                            for (i = 1; i <= 9; ++i)
                            {
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, i, 3, 1, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, i, 3, 9, par3StructureBoundingBox);
                            }

                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 5, 1, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 5, 1, 6, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 5, 3, 4, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 5, 3, 6, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, 1, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, 3, 5, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, 3, 5, par3StructureBoundingBox);

                            for (i = 1; i <= 3; ++i)
                            {
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, i, 4, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, i, 4, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 4, i, 6, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.cobblestone, 0, 6, i, 6, par3StructureBoundingBox);
                            }

                            this.func_151550_a(par1World, Blocks.torch, 0, 5, 3, 5, par3StructureBoundingBox);

                            for (i = 2; i <= 8; ++i)
                            {
                                this.func_151550_a(par1World, Blocks.planks, 0, 2, 3, i, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.planks, 0, 3, 3, i, par3StructureBoundingBox);

                                if (i <= 3 || i >= 7)
                                {
                                    this.func_151550_a(par1World, Blocks.planks, 0, 4, 3, i, par3StructureBoundingBox);
                                    this.func_151550_a(par1World, Blocks.planks, 0, 5, 3, i, par3StructureBoundingBox);
                                    this.func_151550_a(par1World, Blocks.planks, 0, 6, 3, i, par3StructureBoundingBox);
                                }

                                this.func_151550_a(par1World, Blocks.planks, 0, 7, 3, i, par3StructureBoundingBox);
                                this.func_151550_a(par1World, Blocks.planks, 0, 8, 3, i, par3StructureBoundingBox);
                            }

                            this.func_151550_a(par1World, Blocks.ladder, this.func_151555_a(Blocks.ladder, 4), 9, 1, 3, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.ladder, this.func_151555_a(Blocks.ladder, 4), 9, 2, 3, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.ladder, this.func_151555_a(Blocks.ladder, 4), 9, 3, 3, par3StructureBoundingBox);
                            this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 4, 8, ChestGenHooks.getItems(STRONGHOLD_CROSSING, par2Random), ChestGenHooks.getCount(STRONGHOLD_CROSSING, par2Random));
                    }

                    return true;
                }
            }
        }

    public static class StairsStraight extends StructureStrongholdPieces.Stronghold
        {
            private static final String __OBFID = "CL_00000501";

            public StairsStraight() {}

            public StairsStraight(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
            }

            public static StructureStrongholdPieces.StairsStraight findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 11, 8, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.StairsStraight(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 7, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 7, 0);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
                    int i = this.func_151555_a(Blocks.stone_stairs, 2);

                    for (int j = 0; j < 6; ++j)
                    {
                        this.func_151550_a(par1World, Blocks.stone_stairs, i, 1, 6 - j, 1 + j, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stone_stairs, i, 2, 6 - j, 1 + j, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stone_stairs, i, 3, 6 - j, 1 + j, par3StructureBoundingBox);

                        if (j < 5)
                        {
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 5 - j, 1 + j, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 5 - j, 1 + j, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 5 - j, 1 + j, par3StructureBoundingBox);
                        }
                    }

                    return true;
                }
            }
        }

    public static class Stairs2 extends StructureStrongholdPieces.Stairs
        {
            public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
            public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
            public List field_75026_c = new ArrayList();
            private static final String __OBFID = "CL_00000499";

            public Stairs2() {}

            public Stairs2(int par1, Random par2Random, int par3, int par4)
            {
                super(0, par2Random, par3, par4);
            }

            public ChunkPosition func_151553_a()
            {
                return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.func_151553_a() : super.func_151553_a();
            }
        }

    public static class Prison extends StructureStrongholdPieces.Stronghold
        {
            private static final String __OBFID = "CL_00000494";

            public Prison() {}

            public Prison(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
            }

            public static StructureStrongholdPieces.Prison findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 9, 5, 11, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.Prison(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 4, 10, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 10, 3, 3, 10, Blocks.air, Blocks.air, false);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 1, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 3, 4, 3, 3, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 7, 4, 3, 7, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 9, 4, 3, 9, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 4, 4, 3, 6, Blocks.iron_bars, Blocks.iron_bars, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 1, 5, 7, 3, 5, Blocks.iron_bars, Blocks.iron_bars, false);
                    this.func_151550_a(par1World, Blocks.iron_bars, 0, 4, 3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.iron_bars, 0, 4, 3, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.iron_door, this.func_151555_a(Blocks.iron_door, 3), 4, 1, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.iron_door, this.func_151555_a(Blocks.iron_door, 3) + 8, 4, 2, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.iron_door, this.func_151555_a(Blocks.iron_door, 3), 4, 1, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.iron_door, this.func_151555_a(Blocks.iron_door, 3) + 8, 4, 2, 8, par3StructureBoundingBox);
                    return true;
                }
            }
        }

    public static class LeftTurn extends StructureStrongholdPieces.Stronghold
        {
            private static final String __OBFID = "CL_00000490";

            public LeftTurn() {}

            public LeftTurn(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
                {
                    this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
                }
                else
                {
                    this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
                }
            }

            public static StructureStrongholdPieces.LeftTurn findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 5, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.LeftTurn(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 4, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);

                    if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
                    }
                    else
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
                    }

                    return true;
                }
            }
        }

    public static class RightTurn extends StructureStrongholdPieces.LeftTurn
        {
            private static final String __OBFID = "CL_00000495";

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
                {
                    this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
                }
                else
                {
                    this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
                }
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 4, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);

                    if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
                    }
                    else
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
                    }

                    return true;
                }
            }
        }

    static class Stones extends StructureComponent.BlockSelector
        {
            private static final String __OBFID = "CL_00000497";

            private Stones() {}

            // JAVADOC METHOD $$ func_75062_a
            public void selectBlocks(Random par1Random, int par2, int par3, int par4, boolean par5)
            {
                if (par5)
                {
                    this.field_151562_a = Blocks.stonebrick;
                    float f = par1Random.nextFloat();

                    if (f < 0.2F)
                    {
                        this.selectedBlockMetaData = 2;
                    }
                    else if (f < 0.5F)
                    {
                        this.selectedBlockMetaData = 1;
                    }
                    else if (f < 0.55F)
                    {
                        this.field_151562_a = Blocks.monster_egg;
                        this.selectedBlockMetaData = 2;
                    }
                    else
                    {
                        this.selectedBlockMetaData = 0;
                    }
                }
                else
                {
                    this.field_151562_a = Blocks.air;
                    this.selectedBlockMetaData = 0;
                }
            }

            Stones(Object par1StructureStrongholdPieceWeight2)
            {
                this();
            }
        }

    public abstract static class Stronghold extends StructureComponent
        {
            protected StructureStrongholdPieces.Stronghold.Door field_143013_d;
            private static final String __OBFID = "CL_00000503";

            public Stronghold()
            {
                this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
            }

            protected Stronghold(int par1)
            {
                super(par1);
                this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                par1NBTTagCompound.setString("EntryDoor", this.field_143013_d.name());
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.valueOf(par1NBTTagCompound.getString("EntryDoor"));
            }

            // JAVADOC METHOD $$ func_74990_a
            protected void placeDoor(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox, StructureStrongholdPieces.Stronghold.Door par4EnumDoor, int par5, int par6, int par7)
            {
                switch (StructureStrongholdPieces.SwitchDoor.doorEnum[par4EnumDoor.ordinal()])
                {
                    case 1:
                    default:
                        this.func_151549_a(par1World, par3StructureBoundingBox, par5, par6, par7, par5 + 3 - 1, par6 + 3 - 1, par7, Blocks.air, Blocks.air, false);
                        break;
                    case 2:
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.wooden_door, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.wooden_door, 8, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                        break;
                    case 3:
                        this.func_151550_a(par1World, Blocks.air, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.air, 0, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_bars, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                        break;
                    case 4:
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_door, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.iron_door, 8, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stone_button, this.func_151555_a(Blocks.stone_button, 4), par5 + 2, par6 + 1, par7 + 1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stone_button, this.func_151555_a(Blocks.stone_button, 3), par5 + 2, par6 + 1, par7 - 1, par3StructureBoundingBox);
                }
            }

            protected StructureStrongholdPieces.Stronghold.Door getRandomDoor(Random par1Random)
            {
                int i = par1Random.nextInt(5);

                switch (i)
                {
                    case 0:
                    case 1:
                    default:
                        return StructureStrongholdPieces.Stronghold.Door.OPENING;
                    case 2:
                        return StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR;
                    case 3:
                        return StructureStrongholdPieces.Stronghold.Door.GRATES;
                    case 4:
                        return StructureStrongholdPieces.Stronghold.Door.IRON_DOOR;
                }
            }

            // JAVADOC METHOD $$ func_74986_a
            protected StructureComponent getNextComponentNormal(StructureStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.maxZ + 1, this.coordBaseMode, this.getComponentType());
                    case 1:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType());
                    case 2:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.minZ - 1, this.coordBaseMode, this.getComponentType());
                    case 3:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType());
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74989_b
            protected StructureComponent getNextComponentX(StructureStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());
                    case 1:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    case 2:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());
                    case 3:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74987_c
            protected StructureComponent getNextComponentZ(StructureStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());
                    case 1:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    case 2:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());
                    case 3:
                        return StructureStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74991_a
            protected static boolean canStrongholdGoDeeper(StructureBoundingBox par0StructureBoundingBox)
            {
                return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
            }

            public static enum Door
            {
                OPENING,
                WOOD_DOOR,
                GRATES,
                IRON_DOOR;

                private static final String __OBFID = "CL_00000504";
            }
        }

    public static class Crossing extends StructureStrongholdPieces.Stronghold
        {
            private boolean field_74996_b;
            private boolean field_74997_c;
            private boolean field_74995_d;
            private boolean field_74999_h;
            private static final String __OBFID = "CL_00000489";

            public Crossing() {}

            public Crossing(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.field_143013_d = this.getRandomDoor(par2Random);
                this.boundingBox = par3StructureBoundingBox;
                this.field_74996_b = par2Random.nextBoolean();
                this.field_74997_c = par2Random.nextBoolean();
                this.field_74995_d = par2Random.nextBoolean();
                this.field_74999_h = par2Random.nextInt(3) > 0;
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("leftLow", this.field_74996_b);
                par1NBTTagCompound.setBoolean("leftHigh", this.field_74997_c);
                par1NBTTagCompound.setBoolean("rightLow", this.field_74995_d);
                par1NBTTagCompound.setBoolean("rightHigh", this.field_74999_h);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_74996_b = par1NBTTagCompound.getBoolean("leftLow");
                this.field_74997_c = par1NBTTagCompound.getBoolean("leftHigh");
                this.field_74995_d = par1NBTTagCompound.getBoolean("rightLow");
                this.field_74999_h = par1NBTTagCompound.getBoolean("rightHigh");
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                int i = 3;
                int j = 5;

                if (this.coordBaseMode == 1 || this.coordBaseMode == 2)
                {
                    i = 8 - i;
                    j = 8 - j;
                }

                this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 5, 1);

                if (this.field_74996_b)
                {
                    this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, i, 1);
                }

                if (this.field_74997_c)
                {
                    this.getNextComponentX((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, j, 7);
                }

                if (this.field_74995_d)
                {
                    this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, i, 1);
                }

                if (this.field_74999_h)
                {
                    this.getNextComponentZ((StructureStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, j, 7);
                }
            }

            public static StructureStrongholdPieces.Crossing findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -3, 0, 10, 9, 11, par5);
                // JAVADOC METHOD $$ func_74991_a
                return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureStrongholdPieces.Crossing(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 9, 8, 10, true, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 4, 3, 0);

                    if (this.field_74996_b)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 1, 0, 5, 3, Blocks.air, Blocks.air, false);
                    }

                    if (this.field_74995_d)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 9, 3, 1, 9, 5, 3, Blocks.air, Blocks.air, false);
                    }

                    if (this.field_74997_c)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 7, 0, 7, 9, Blocks.air, Blocks.air, false);
                    }

                    if (this.field_74999_h)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 9, 5, 7, 9, 7, 9, Blocks.air, Blocks.air, false);
                    }

                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 1, 10, 7, 3, 10, Blocks.air, Blocks.air, false);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 2, 1, 8, 2, 6, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 5, 4, 4, 9, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, 1, 5, 8, 4, 9, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 4, 7, 3, 4, 9, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 5, 3, 3, 6, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 1, 7, 7, 1, 8, false, par2Random, StructureStrongholdPieces.strongholdStones);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
                    this.func_151550_a(par1World, Blocks.torch, 0, 6, 5, 6, par3StructureBoundingBox);
                    return true;
                }
            }
        }

    public static class Corridor extends StructureStrongholdPieces.Stronghold
        {
            private int field_74993_a;
            private static final String __OBFID = "CL_00000488";

            public Corridor() {}

            public Corridor(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
                this.field_74993_a = par4 != 2 && par4 != 0 ? par3StructureBoundingBox.getXSize() : par3StructureBoundingBox.getZSize();
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("Steps", this.field_74993_a);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_74993_a = par1NBTTagCompound.getInteger("Steps");
            }

            public static StructureBoundingBox func_74992_a(List par0List, Random par1Random, int par2, int par3, int par4, int par5)
            {
                boolean flag = true;
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 4, par5);
                StructureComponent structurecomponent = StructureComponent.findIntersecting(par0List, structureboundingbox);

                if (structurecomponent == null)
                {
                    return null;
                }
                else
                {
                    if (structurecomponent.getBoundingBox().minY == structureboundingbox.minY)
                    {
                        for (int i1 = 3; i1 >= 1; --i1)
                        {
                            structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, i1 - 1, par5);

                            if (!structurecomponent.getBoundingBox().intersectsWith(structureboundingbox))
                            {
                                return StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, i1, par5);
                            }
                        }
                    }

                    return null;
                }
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
                {
                    return false;
                }
                else
                {
                    for (int i = 0; i < this.field_74993_a; ++i)
                    {
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 0, 0, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 0, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 0, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 0, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 4, 0, i, par3StructureBoundingBox);

                        for (int j = 1; j <= 3; ++j)
                        {
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 0, j, i, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.air, 0, 1, j, i, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.air, 0, 2, j, i, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.air, 0, 3, j, i, par3StructureBoundingBox);
                            this.func_151550_a(par1World, Blocks.stonebrick, 0, 4, j, i, par3StructureBoundingBox);
                        }

                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 0, 4, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 1, 4, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 2, 4, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 3, 4, i, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stonebrick, 0, 4, 4, i, par3StructureBoundingBox);
                    }

                    return true;
                }
            }
        }

    static final class SwitchDoor
        {
            static final int[] doorEnum = new int[StructureStrongholdPieces.Stronghold.Door.values().length];
            private static final String __OBFID = "CL_00000486";

            static
            {
                try
                {
                    doorEnum[StructureStrongholdPieces.Stronghold.Door.OPENING.ordinal()] = 1;
                }
                catch (NoSuchFieldError var4)
                {
                    ;
                }

                try
                {
                    doorEnum[StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR.ordinal()] = 2;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    doorEnum[StructureStrongholdPieces.Stronghold.Door.GRATES.ordinal()] = 3;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    doorEnum[StructureStrongholdPieces.Stronghold.Door.IRON_DOOR.ordinal()] = 4;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }

    static class PieceWeight
        {
            public Class pieceClass;
            // JAVADOC FIELD $$ field_75192_b
            public final int pieceWeight;
            public int instancesSpawned;
            // JAVADOC FIELD $$ field_75191_d
            public int instancesLimit;
            private static final String __OBFID = "CL_00000492";

            public PieceWeight(Class par1Class, int par2, int par3)
            {
                this.pieceClass = par1Class;
                this.pieceWeight = par2;
                this.instancesLimit = par3;
            }

            public boolean canSpawnMoreStructuresOfType(int par1)
            {
                return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
            }

            public boolean canSpawnMoreStructures()
            {
                return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
            }
        }
}