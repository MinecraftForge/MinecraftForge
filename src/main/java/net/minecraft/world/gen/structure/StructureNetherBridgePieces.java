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
import net.minecraft.world.World;

public class StructureNetherBridgePieces
{
    private static final StructureNetherBridgePieces.PieceWeight[] primaryComponents = new StructureNetherBridgePieces.PieceWeight[] {new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Straight.class, 30, 0, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing.class, 10, 4), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)};
    private static final StructureNetherBridgePieces.PieceWeight[] secondaryComponents = new StructureNetherBridgePieces.PieceWeight[] {new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor2.class, 5, 10), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor.class, 5, 10), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor3.class, 10, 3, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor4.class, 7, 2), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)};
    private static final String __OBFID = "CL_00000453";

    public static void func_143049_a()
    {
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.End.class, "NeBEF");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Straight.class, "NeBS");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor3.class, "NeCCS");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor4.class, "NeCTB");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Entrance.class, "NeCE");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor.class, "NeSCLT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor5.class, "NeSC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor2.class, "NeSCRT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Throne.class, "NeMT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing.class, "NeRC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Stairs.class, "NeSR");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Start.class, "NeStart");
    }

    private static StructureNetherBridgePieces.Piece createNextComponentRandom(StructureNetherBridgePieces.PieceWeight par0StructureNetherBridgePieceWeight, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        Class oclass = par0StructureNetherBridgePieceWeight.weightClass;
        Object object = null;

        if (oclass == StructureNetherBridgePieces.Straight.class)
        {
            object = StructureNetherBridgePieces.Straight.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Crossing3.class)
        {
            object = StructureNetherBridgePieces.Crossing3.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Crossing.class)
        {
            object = StructureNetherBridgePieces.Crossing.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Stairs.class)
        {
            object = StructureNetherBridgePieces.Stairs.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Throne.class)
        {
            object = StructureNetherBridgePieces.Throne.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Entrance.class)
        {
            object = StructureNetherBridgePieces.Entrance.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Corridor5.class)
        {
            object = StructureNetherBridgePieces.Corridor5.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Corridor2.class)
        {
            object = StructureNetherBridgePieces.Corridor2.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Corridor.class)
        {
            object = StructureNetherBridgePieces.Corridor.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Corridor3.class)
        {
            object = StructureNetherBridgePieces.Corridor3.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Corridor4.class)
        {
            object = StructureNetherBridgePieces.Corridor4.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.Crossing2.class)
        {
            object = StructureNetherBridgePieces.Crossing2.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (oclass == StructureNetherBridgePieces.NetherStalkRoom.class)
        {
            object = StructureNetherBridgePieces.NetherStalkRoom.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }

        return (StructureNetherBridgePieces.Piece)object;
    }

    public static class Crossing3 extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000454";

            public Crossing3() {}

            public Crossing3(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            protected Crossing3(Random par1Random, int par2, int par3)
            {
                super(0);
                this.coordBaseMode = par1Random.nextInt(4);

                switch (this.coordBaseMode)
                {
                    case 0:
                    case 2:
                        this.boundingBox = new StructureBoundingBox(par2, 64, par3, par2 + 19 - 1, 73, par3 + 19 - 1);
                        break;
                    default:
                        this.boundingBox = new StructureBoundingBox(par2, 64, par3, par2 + 19 - 1, 73, par3 + 19 - 1);
                }
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 8, 3, false);
                this.getNextComponentX((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 3, 8, false);
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 3, 8, false);
            }

            // JAVADOC METHOD $$ func_74966_a
            public static StructureNetherBridgePieces.Crossing3 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -8, -3, 0, 19, 10, 19, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Crossing3(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 3, 0, 11, 4, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 7, 18, 4, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 0, 10, 7, 18, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 8, 18, 7, 10, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 5, 0, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 5, 11, 7, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 0, 11, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 11, 11, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 7, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 7, 18, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 11, 7, 5, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 11, 18, 5, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 2, 0, 11, 2, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 2, 13, 11, 2, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 0, 0, 11, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 0, 15, 11, 1, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                int i;
                int j;

                for (i = 7; i <= 11; ++i)
                {
                    for (j = 0; j <= 2; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, 18 - j, par3StructureBoundingBox);
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 7, 5, 2, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 13, 2, 7, 18, 2, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 7, 3, 1, 11, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 15, 0, 7, 18, 1, 11, Blocks.nether_brick, Blocks.nether_brick, false);

                for (i = 0; i <= 2; ++i)
                {
                    for (j = 7; j <= 11; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, 18 - i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Straight extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000456";

            public Straight() {}

            public Straight(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 1, 3, false);
            }

            // JAVADOC METHOD $$ func_74983_a
            public static StructureNetherBridgePieces.Straight createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -3, 0, 5, 10, 19, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Straight(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 0, 4, 4, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 0, 3, 7, 18, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 0, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 0, 4, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 2, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 13, 4, 2, 18, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 15, 4, 1, 18, Blocks.nether_brick, Blocks.nether_brick, false);

                for (int i = 0; i <= 4; ++i)
                {
                    for (int j = 0; j <= 2; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, 18 - j, par3StructureBoundingBox);
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 4, 0, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 14, 0, 4, 14, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 1, 17, 0, 4, 17, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 4, 4, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 14, 4, 4, 14, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 17, 4, 4, 17, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                return true;
            }
        }

    public static class Crossing2 extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000460";

            public Crossing2() {}

            public Crossing2(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 1, 0, true);
                this.getNextComponentX((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 1, true);
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 1, true);
            }

            // JAVADOC METHOD $$ func_74979_a
            public static StructureNetherBridgePieces.Crossing2 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, 0, 0, 5, 7, 5, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Crossing2(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 4, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 4, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

                for (int i = 0; i <= 4; ++i)
                {
                    for (int j = 0; j <= 4; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Entrance extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000459";

            public Entrance() {}

            public Entrance(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 5, 3, true);
            }

            // JAVADOC METHOD $$ func_74984_a
            public static StructureNetherBridgePieces.Entrance createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -5, -3, 0, 13, 14, 13, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Entrance(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 8, 0, 7, 8, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                int i;

                for (i = 1; i <= 11; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 10, 0, i, 11, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 10, 12, i, 11, 12, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, 10, i, 0, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 12, 10, i, 12, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, i, 13, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, i, 13, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, 0, 13, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, 12, 13, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, i + 1, 13, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, i + 1, 13, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, i + 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 12, 13, i + 1, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 12, 13, 0, par3StructureBoundingBox);

                for (i = 3; i <= 9; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 7, i, 1, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 11, 7, i, 11, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                int j;

                for (i = 4; i <= 8; ++i)
                {
                    for (j = 0; j <= 2; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, 12 - j, par3StructureBoundingBox);
                    }
                }

                for (i = 0; i <= 2; ++i)
                {
                    for (j = 4; j <= 8; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, 12 - i, -1, j, par3StructureBoundingBox);
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 5, 5, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 1, 6, 6, 4, 6, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.nether_brick, 0, 6, 0, 6, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.flowing_lava, 0, 6, 5, 6, par3StructureBoundingBox);
                i = this.getXWithOffset(6, 6);
                j = this.getYWithOffset(5);
                int k = this.getZWithOffset(6, 6);

                if (par3StructureBoundingBox.isVecInside(i, j, k))
                {
                    par1World.scheduledUpdatesAreImmediate = true;
                    Blocks.flowing_lava.func_149674_a(par1World, i, j, k, par2Random);
                    par1World.scheduledUpdatesAreImmediate = false;
                }

                return true;
            }
        }

    static class PieceWeight
        {
            // JAVADOC FIELD $$ field_78828_a
            public Class weightClass;
            public final int field_78826_b;
            public int field_78827_c;
            public int field_78824_d;
            public boolean field_78825_e;
            private static final String __OBFID = "CL_00000467";

            public PieceWeight(Class par1Class, int par2, int par3, boolean par4)
            {
                this.weightClass = par1Class;
                this.field_78826_b = par2;
                this.field_78824_d = par3;
                this.field_78825_e = par4;
            }

            public PieceWeight(Class par1Class, int par2, int par3)
            {
                this(par1Class, par2, par3, false);
            }

            public boolean func_78822_a(int par1)
            {
                return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
            }

            public boolean func_78823_a()
            {
                return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
            }
        }

    public static class Throne extends StructureNetherBridgePieces.Piece
        {
            private boolean hasSpawner;
            private static final String __OBFID = "CL_00000465";

            public Throne() {}

            public Throne(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.hasSpawner = par1NBTTagCompound.getBoolean("Mob");
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Mob", this.hasSpawner);
            }

            // JAVADOC METHOD $$ func_74975_a
            public static StructureNetherBridgePieces.Throne createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -2, 0, 0, 7, 8, 9, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Throne(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 6, 7, 7, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 0, 5, 1, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 1, 5, 2, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 2, 5, 3, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 3, 5, 4, 7, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 0, 1, 4, 2, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 2, 0, 5, 4, 2, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 2, 1, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 5, 2, 5, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 3, 0, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 5, 3, 6, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 5, 8, 5, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 1, 6, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 5, 6, 3, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 3, 0, 6, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 6, 3, 6, 6, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 6, 8, 5, 7, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 8, 8, 4, 8, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                int i;
                int j;

                if (!this.hasSpawner)
                {
                    i = this.getYWithOffset(5);
                    j = this.getXWithOffset(3, 5);
                    int k = this.getZWithOffset(3, 5);

                    if (par3StructureBoundingBox.isVecInside(j, i, k))
                    {
                        this.hasSpawner = true;
                        par1World.func_147465_d(j, i, k, Blocks.mob_spawner, 0, 2);
                        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)par1World.func_147438_o(j, i, k);

                        if (tileentitymobspawner != null)
                        {
                            tileentitymobspawner.func_145881_a().setMobID("Blaze");
                        }
                    }
                }

                for (i = 0; i <= 6; ++i)
                {
                    for (j = 0; j <= 6; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Corridor5 extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000462";

            public Corridor5() {}

            public Corridor5(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 1, 0, true);
            }

            // JAVADOC METHOD $$ func_74981_a
            public static StructureNetherBridgePieces.Corridor5 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, 0, 0, 5, 7, 5, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Corridor5(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

                for (int i = 0; i <= 4; ++i)
                {
                    for (int j = 0; j <= 4; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    abstract static class Piece extends StructureComponent
        {
            protected static final WeightedRandomChestContent[] field_111019_a = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15), new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5), new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5), new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5), new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3)};
            private static final String __OBFID = "CL_00000466";

            public Piece() {}

            protected Piece(int par1)
            {
                super(par1);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {}

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {}

            private int getTotalWeight(List par1List)
            {
                boolean flag = false;
                int i = 0;
                StructureNetherBridgePieces.PieceWeight pieceweight;

                for (Iterator iterator = par1List.iterator(); iterator.hasNext(); i += pieceweight.field_78826_b)
                {
                    pieceweight = (StructureNetherBridgePieces.PieceWeight)iterator.next();

                    if (pieceweight.field_78824_d > 0 && pieceweight.field_78827_c < pieceweight.field_78824_d)
                    {
                        flag = true;
                    }
                }

                return flag ? i : -1;
            }

            private StructureNetherBridgePieces.Piece getNextComponent(StructureNetherBridgePieces.Start par1ComponentNetherBridgeStartPiece, List par2List, List par3List, Random par4Random, int par5, int par6, int par7, int par8, int par9)
            {
                int j1 = this.getTotalWeight(par2List);
                boolean flag = j1 > 0 && par9 <= 30;
                int k1 = 0;

                while (k1 < 5 && flag)
                {
                    ++k1;
                    int l1 = par4Random.nextInt(j1);
                    Iterator iterator = par2List.iterator();

                    while (iterator.hasNext())
                    {
                        StructureNetherBridgePieces.PieceWeight pieceweight = (StructureNetherBridgePieces.PieceWeight)iterator.next();
                        l1 -= pieceweight.field_78826_b;

                        if (l1 < 0)
                        {
                            if (!pieceweight.func_78822_a(par9) || pieceweight == par1ComponentNetherBridgeStartPiece.theNetherBridgePieceWeight && !pieceweight.field_78825_e)
                            {
                                break;
                            }

                            StructureNetherBridgePieces.Piece piece = StructureNetherBridgePieces.createNextComponentRandom(pieceweight, par3List, par4Random, par5, par6, par7, par8, par9);

                            if (piece != null)
                            {
                                ++pieceweight.field_78827_c;
                                par1ComponentNetherBridgeStartPiece.theNetherBridgePieceWeight = pieceweight;

                                if (!pieceweight.func_78823_a())
                                {
                                    par2List.remove(pieceweight);
                                }

                                return piece;
                            }
                        }
                    }
                }

                return StructureNetherBridgePieces.End.func_74971_a(par3List, par4Random, par5, par6, par7, par8, par9);
            }

            // JAVADOC METHOD $$ func_74962_a
            private StructureComponent getNextComponent(StructureNetherBridgePieces.Start par1ComponentNetherBridgeStartPiece, List par2List, Random par3Random, int par4, int par5, int par6, int par7, int par8, boolean par9)
            {
                if (Math.abs(par4 - par1ComponentNetherBridgeStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par6 - par1ComponentNetherBridgeStartPiece.getBoundingBox().minZ) <= 112)
                {
                    List list1 = par1ComponentNetherBridgeStartPiece.primaryWeights;

                    if (par9)
                    {
                        list1 = par1ComponentNetherBridgeStartPiece.secondaryWeights;
                    }

                    StructureNetherBridgePieces.Piece piece = this.getNextComponent(par1ComponentNetherBridgeStartPiece, list1, par2List, par3Random, par4, par5, par6, par7, par8 + 1);

                    if (piece != null)
                    {
                        par2List.add(piece);
                        par1ComponentNetherBridgeStartPiece.field_74967_d.add(piece);
                    }

                    return piece;
                }
                else
                {
                    return StructureNetherBridgePieces.End.func_74971_a(par2List, par3Random, par4, par5, par6, par7, par8);
                }
            }

            // JAVADOC METHOD $$ func_74963_a
            protected StructureComponent getNextComponentNormal(StructureNetherBridgePieces.Start par1ComponentNetherBridgeStartPiece, List par2List, Random par3Random, int par4, int par5, boolean par6)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.maxZ + 1, this.coordBaseMode, this.getComponentType(), par6);
                    case 1:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType(), par6);
                    case 2:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.minZ - 1, this.coordBaseMode, this.getComponentType(), par6);
                    case 3:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType(), par6);
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74961_b
            protected StructureComponent getNextComponentX(StructureNetherBridgePieces.Start par1ComponentNetherBridgeStartPiece, List par2List, Random par3Random, int par4, int par5, boolean par6)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType(), par6);
                    case 1:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType(), par6);
                    case 2:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType(), par6);
                    case 3:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType(), par6);
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74965_c
            protected StructureComponent getNextComponentZ(StructureNetherBridgePieces.Start par1ComponentNetherBridgeStartPiece, List par2List, Random par3Random, int par4, int par5, boolean par6)
            {
                switch (this.coordBaseMode)
                {
                    case 0:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType(), par6);
                    case 1:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType(), par6);
                    case 2:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType(), par6);
                    case 3:
                        return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType(), par6);
                    default:
                        return null;
                }
            }

            // JAVADOC METHOD $$ func_74964_a
            protected static boolean isAboveGround(StructureBoundingBox par0StructureBoundingBox)
            {
                return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
            }
        }

    public static class Start extends StructureNetherBridgePieces.Crossing3
        {
            // JAVADOC FIELD $$ field_74970_a
            public StructureNetherBridgePieces.PieceWeight theNetherBridgePieceWeight;
            // JAVADOC FIELD $$ field_74968_b
            public List primaryWeights;
            // JAVADOC FIELD $$ field_74969_c
            public List secondaryWeights;
            public ArrayList field_74967_d = new ArrayList();
            private static final String __OBFID = "CL_00000470";

            public Start() {}

            public Start(Random par1Random, int par2, int par3)
            {
                super(par1Random, par2, par3);
                this.primaryWeights = new ArrayList();
                StructureNetherBridgePieces.PieceWeight[] apieceweight = StructureNetherBridgePieces.primaryComponents;
                int k = apieceweight.length;
                int l;
                StructureNetherBridgePieces.PieceWeight pieceweight;

                for (l = 0; l < k; ++l)
                {
                    pieceweight = apieceweight[l];
                    pieceweight.field_78827_c = 0;
                    this.primaryWeights.add(pieceweight);
                }

                this.secondaryWeights = new ArrayList();
                apieceweight = StructureNetherBridgePieces.secondaryComponents;
                k = apieceweight.length;

                for (l = 0; l < k; ++l)
                {
                    pieceweight = apieceweight[l];
                    pieceweight.field_78827_c = 0;
                    this.secondaryWeights.add(pieceweight);
                }
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
            }
        }

    public static class Stairs extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000469";

            public Stairs() {}

            public Stairs(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 6, 2, false);
            }

            // JAVADOC METHOD $$ func_74973_a
            public static StructureNetherBridgePieces.Stairs createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -2, 0, 0, 7, 11, 7, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Stairs(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 6, 10, 6, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 1, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 2, 0, 6, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 1, 0, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 2, 1, 6, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 6, 5, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 2, 0, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 3, 2, 6, 5, 2, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 3, 4, 6, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151550_a(par1World, Blocks.nether_brick, 0, 5, 2, 5, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 5, 4, 3, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 2, 5, 3, 4, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 2, 5, 2, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 5, 1, 6, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 7, 1, 5, 7, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 8, 2, 6, 8, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 6, 0, 4, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

                for (int i = 0; i <= 6; ++i)
                {
                    for (int j = 0; j <= 6; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Corridor2 extends StructureNetherBridgePieces.Piece
        {
            private boolean field_111020_b;
            private static final String __OBFID = "CL_00000463";

            public Corridor2() {}

            public Corridor2(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
                this.field_111020_b = par2Random.nextInt(3) == 0;
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_111020_b = par1NBTTagCompound.getBoolean("Chest");
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Chest", this.field_111020_b);
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 1, true);
            }

            // JAVADOC METHOD $$ func_74980_a
            public static StructureNetherBridgePieces.Corridor2 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, 0, 0, 5, 7, 5, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Corridor2(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 2, 4, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
                int i;
                int j;

                if (this.field_111020_b)
                {
                    i = this.getYWithOffset(2);
                    j = this.getXWithOffset(1, 3);
                    int k = this.getZWithOffset(1, 3);

                    if (par3StructureBoundingBox.isVecInside(j, i, k))
                    {
                        this.field_111020_b = false;
                        this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 1, 2, 3, field_111019_a, 2 + par2Random.nextInt(4));
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

                for (i = 0; i <= 4; ++i)
                {
                    for (j = 0; j <= 4; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Corridor3 extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000457";

            public Corridor3() {}

            public Corridor3(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 1, 0, true);
            }

            // JAVADOC METHOD $$ func_74982_a
            public static StructureNetherBridgePieces.Corridor3 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 14, 10, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Corridor3(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                int i = this.func_151555_a(Blocks.nether_brick_stairs, 2);

                for (int j = 0; j <= 9; ++j)
                {
                    int k = Math.max(1, 7 - j);
                    int l = Math.min(Math.max(k + 5, 14 - j), 13);
                    int i1 = j;
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, j, 4, k, j, Blocks.nether_brick, Blocks.nether_brick, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, k + 1, j, 3, l - 1, j, Blocks.air, Blocks.air, false);

                    if (j <= 6)
                    {
                        this.func_151550_a(par1World, Blocks.nether_brick_stairs, i, 1, k + 1, j, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.nether_brick_stairs, i, 2, k + 1, j, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.nether_brick_stairs, i, 3, k + 1, j, par3StructureBoundingBox);
                    }

                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, l, j, 4, l, j, Blocks.nether_brick, Blocks.nether_brick, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, k + 1, j, 0, l - 1, j, Blocks.nether_brick, Blocks.nether_brick, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 4, k + 1, j, 4, l - 1, j, Blocks.nether_brick, Blocks.nether_brick, false);

                    if ((j & 1) == 0)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 0, k + 2, j, 0, k + 3, j, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                        this.func_151549_a(par1World, par3StructureBoundingBox, 4, k + 2, j, 4, k + 3, j, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    }

                    for (int j1 = 0; j1 <= 4; ++j1)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, j1, -1, i1, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Corridor4 extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000458";

            public Corridor4() {}

            public Corridor4(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                byte b0 = 1;

                if (this.coordBaseMode == 1 || this.coordBaseMode == 2)
                {
                    b0 = 5;
                }

                this.getNextComponentX((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, b0, par3Random.nextInt(8) > 0);
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, b0, par3Random.nextInt(8) > 0);
            }

            // JAVADOC METHOD $$ func_74985_a
            public static StructureNetherBridgePieces.Corridor4 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -3, 0, 0, 9, 7, 9, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Corridor4(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 8, 5, 8, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 0, 8, 6, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 2, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 2, 0, 8, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 0, 1, 4, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 3, 0, 7, 4, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 4, 8, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 4, 2, 2, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 1, 4, 7, 2, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 8, 8, 3, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 6, 0, 3, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 3, 6, 8, 3, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 4, 0, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 3, 4, 8, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 5, 2, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 3, 5, 7, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 4, 5, 1, 5, 5, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 4, 5, 7, 5, 5, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

                for (int i = 0; i <= 5; ++i)
                {
                    for (int j = 0; j <= 8; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, j, -1, i, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class End extends StructureNetherBridgePieces.Piece
        {
            private int fillSeed;
            private static final String __OBFID = "CL_00000455";

            public End() {}

            public End(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
                this.fillSeed = par2Random.nextInt();
            }

            public static StructureNetherBridgePieces.End func_74971_a(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -3, 0, 5, 10, 8, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.End(par6, par1Random, structureboundingbox, par5) : null;
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.fillSeed = par1NBTTagCompound.getInteger("Seed");
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setInteger("Seed", this.fillSeed);
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                Random random1 = new Random((long)this.fillSeed);
                int i;
                int j;
                int k;

                for (i = 0; i <= 4; ++i)
                {
                    for (j = 3; j <= 4; ++j)
                    {
                        k = random1.nextInt(8);
                        this.func_151549_a(par1World, par3StructureBoundingBox, i, j, 0, i, j, k, Blocks.nether_brick, Blocks.nether_brick, false);
                    }
                }

                i = random1.nextInt(8);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 0, 5, i, Blocks.nether_brick, Blocks.nether_brick, false);
                i = random1.nextInt(8);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 5, 0, 4, 5, i, Blocks.nether_brick, Blocks.nether_brick, false);

                for (i = 0; i <= 4; ++i)
                {
                    j = random1.nextInt(5);
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 2, 0, i, 2, j, Blocks.nether_brick, Blocks.nether_brick, false);
                }

                for (i = 0; i <= 4; ++i)
                {
                    for (j = 0; j <= 1; ++j)
                    {
                        k = random1.nextInt(3);
                        this.func_151549_a(par1World, par3StructureBoundingBox, i, j, 0, i, j, k, Blocks.nether_brick, Blocks.nether_brick, false);
                    }
                }

                return true;
            }
        }

    public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000464";

            public NetherStalkRoom() {}

            public NetherStalkRoom(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 5, 3, true);
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 5, 11, true);
            }

            // JAVADOC METHOD $$ func_74977_a
            public static StructureNetherBridgePieces.NetherStalkRoom createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -5, -3, 0, 13, 14, 13, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.NetherStalkRoom(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.nether_brick, Blocks.nether_brick, false);
                int i;

                for (i = 1; i <= 11; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 10, 0, i, 11, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, 10, 12, i, 11, 12, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 0, 10, i, 0, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 12, 10, i, 12, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, i, 13, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, i, 13, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, 0, 13, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick, 0, 12, 13, i, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, i + 1, 13, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, i + 1, 13, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, i + 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 12, 13, i + 1, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 0, 13, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_fence, 0, 12, 13, 0, par3StructureBoundingBox);

                for (i = 3; i <= 9; i += 2)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 7, i, 1, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 11, 7, i, 11, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                }

                i = this.func_151555_a(Blocks.nether_brick_stairs, 3);
                int j;
                int k;
                int l;

                for (j = 0; j <= 6; ++j)
                {
                    k = j + 4;

                    for (l = 5; l <= 7; ++l)
                    {
                        this.func_151550_a(par1World, Blocks.nether_brick_stairs, i, l, 5 + j, k, par3StructureBoundingBox);
                    }

                    if (k >= 5 && k <= 8)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 5, 5, k, 7, j + 4, k, Blocks.nether_brick, Blocks.nether_brick, false);
                    }
                    else if (k >= 9 && k <= 10)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 5, 8, k, 7, j + 4, k, Blocks.nether_brick, Blocks.nether_brick, false);
                    }

                    if (j >= 1)
                    {
                        this.func_151549_a(par1World, par3StructureBoundingBox, 5, 6 + j, k, 7, 9 + j, k, Blocks.air, Blocks.air, false);
                    }
                }

                for (j = 5; j <= 7; ++j)
                {
                    this.func_151550_a(par1World, Blocks.nether_brick_stairs, i, j, 12, 11, par3StructureBoundingBox);
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 6, 7, 5, 7, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 7, 6, 7, 7, 7, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 13, 12, 7, 13, 12, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 2, 3, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 9, 3, 5, 10, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 4, 2, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 5, 2, 10, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 5, 9, 10, 5, 10, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 10, 5, 4, 10, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                j = this.func_151555_a(Blocks.nether_brick_stairs, 0);
                k = this.func_151555_a(Blocks.nether_brick_stairs, 1);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, k, 4, 5, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, k, 4, 5, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, k, 4, 5, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, k, 4, 5, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, j, 8, 5, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, j, 8, 5, 3, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, j, 8, 5, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.nether_brick_stairs, j, 8, 5, 10, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 4, 4, 4, 4, 8, Blocks.soul_sand, Blocks.soul_sand, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 4, 4, 9, 4, 8, Blocks.soul_sand, Blocks.soul_sand, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 5, 4, 4, 5, 8, Blocks.nether_wart, Blocks.nether_wart, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 5, 4, 9, 5, 8, Blocks.nether_wart, Blocks.nether_wart, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
                int i1;

                for (l = 4; l <= 8; ++l)
                {
                    for (i1 = 0; i1 <= 2; ++i1)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, l, -1, i1, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, l, -1, 12 - i1, par3StructureBoundingBox);
                    }
                }

                for (l = 0; l <= 2; ++l)
                {
                    for (i1 = 4; i1 <= 8; ++i1)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, l, -1, i1, par3StructureBoundingBox);
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, 12 - l, -1, i1, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Crossing extends StructureNetherBridgePieces.Piece
        {
            private static final String __OBFID = "CL_00000468";

            public Crossing() {}

            public Crossing(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentNormal((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 2, 0, false);
                this.getNextComponentX((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 2, false);
                this.getNextComponentZ((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 2, false);
            }

            // JAVADOC METHOD $$ func_74974_a
            public static StructureNetherBridgePieces.Crossing createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -2, 0, 0, 7, 9, 7, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Crossing(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 6, 7, 6, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 1, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 6, 1, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 2, 0, 6, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 2, 6, 6, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 6, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 5, 0, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 2, 0, 6, 6, 1, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 2, 5, 6, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 6, 0, 4, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 6, 6, 4, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 5, 6, 4, 5, 6, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 2, 0, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 5, 2, 0, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 6, 2, 6, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 5, 2, 6, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

                for (int i = 0; i <= 6; ++i)
                {
                    for (int j = 0; j <= 6; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }

    public static class Corridor extends StructureNetherBridgePieces.Piece
        {
            private boolean field_111021_b;
            private static final String __OBFID = "CL_00000461";

            public Corridor() {}

            public Corridor(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
            {
                super(par1);
                this.coordBaseMode = par4;
                this.boundingBox = par3StructureBoundingBox;
                this.field_111021_b = par2Random.nextInt(3) == 0;
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_111021_b = par1NBTTagCompound.getBoolean("Chest");
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Chest", this.field_111021_b);
            }

            // JAVADOC METHOD $$ func_74861_a
            public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
            {
                this.getNextComponentX((StructureNetherBridgePieces.Start)par1StructureComponent, par2List, par3Random, 0, 1, true);
            }

            // JAVADOC METHOD $$ func_74978_a
            public static StructureNetherBridgePieces.Corridor createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
            {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, 0, 0, 5, 7, 5, par5);
                // JAVADOC METHOD $$ func_74964_a
                return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new StructureNetherBridgePieces.Corridor(par6, par1Random, structureboundingbox, par5) : null;
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 2, 4, 3, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
                int i;
                int j;

                if (this.field_111021_b)
                {
                    i = this.getYWithOffset(2);
                    j = this.getXWithOffset(3, 3);
                    int k = this.getZWithOffset(3, 3);

                    if (par3StructureBoundingBox.isVecInside(j, i, k))
                    {
                        this.field_111021_b = false;
                        this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 2, 3, field_111019_a, 2 + par2Random.nextInt(4));
                    }
                }

                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

                for (i = 0; i <= 4; ++i)
                {
                    for (j = 0; j <= 4; ++j)
                    {
                        this.func_151554_b(par1World, Blocks.nether_brick, 0, i, -1, j, par3StructureBoundingBox);
                    }
                }

                return true;
            }
        }
}