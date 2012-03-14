package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class StructureNetherBridgePieces
{
    private static final StructureNetherBridgePieceWeight[] primaryComponents = new StructureNetherBridgePieceWeight[] {new StructureNetherBridgePieceWeight(ComponentNetherBridgeStraight.class, 30, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing3.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeStairs.class, 10, 3), new StructureNetherBridgePieceWeight(ComponentNetherBridgeThrone.class, 5, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeEntrance.class, 5, 1)};
    private static final StructureNetherBridgePieceWeight[] secondaryComponents = new StructureNetherBridgePieceWeight[] {new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor5.class, 25, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing2.class, 15, 5), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor2.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor3.class, 10, 3, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor4.class, 7, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeNetherStalkRoom.class, 5, 2)};

    private static ComponentNetherBridgePiece createNextComponentRandom(StructureNetherBridgePieceWeight par0StructureNetherBridgePieceWeight, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        Class var8 = par0StructureNetherBridgePieceWeight.field_40699_a;
        Object var9 = null;

        if (var8 == ComponentNetherBridgeStraight.class)
        {
            var9 = ComponentNetherBridgeStraight.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCrossing3.class)
        {
            var9 = ComponentNetherBridgeCrossing3.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCrossing.class)
        {
            var9 = ComponentNetherBridgeCrossing.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeStairs.class)
        {
            var9 = ComponentNetherBridgeStairs.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeThrone.class)
        {
            var9 = ComponentNetherBridgeThrone.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeEntrance.class)
        {
            var9 = ComponentNetherBridgeEntrance.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCorridor5.class)
        {
            var9 = ComponentNetherBridgeCorridor5.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCorridor2.class)
        {
            var9 = ComponentNetherBridgeCorridor2.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCorridor.class)
        {
            var9 = ComponentNetherBridgeCorridor.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCorridor3.class)
        {
            var9 = ComponentNetherBridgeCorridor3.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCorridor4.class)
        {
            var9 = ComponentNetherBridgeCorridor4.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeCrossing2.class)
        {
            var9 = ComponentNetherBridgeCrossing2.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentNetherBridgeNetherStalkRoom.class)
        {
            var9 = ComponentNetherBridgeNetherStalkRoom.createValidComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }

        return (ComponentNetherBridgePiece)var9;
    }

    static ComponentNetherBridgePiece createNextComponent(StructureNetherBridgePieceWeight par0StructureNetherBridgePieceWeight, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        return createNextComponentRandom(par0StructureNetherBridgePieceWeight, par1List, par2Random, par3, par4, par5, par6, par7);
    }

    static StructureNetherBridgePieceWeight[] getPrimaryComponents()
    {
        return primaryComponents;
    }

    static StructureNetherBridgePieceWeight[] getSecondaryComponents()
    {
        return secondaryComponents;
    }
}
