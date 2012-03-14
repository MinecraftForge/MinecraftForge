package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class ComponentVillageStartPiece extends ComponentVillageWell
{
    public WorldChunkManager worldChunkMngr;

    /** World terrain type, 0 for normal, 1 for flap map */
    public int terrainType;
    public StructureVillagePieceWeight structVillagePieceWeight;

    /**
     * 'Contains List of all spawnable Structure Piece Weights. If no more Pieces of a type can be spawned, they are
     * removed from this list'
     */
    public ArrayList structureVillageWeightedPieceList;
    public ArrayList field_35108_e = new ArrayList();
    public ArrayList field_35106_f = new ArrayList();

    public ComponentVillageStartPiece(WorldChunkManager par1WorldChunkManager, int par2, Random par3Random, int par4, int par5, ArrayList par6ArrayList, int par7)
    {
        super(0, par3Random, par4, par5);
        this.worldChunkMngr = par1WorldChunkManager;
        this.structureVillageWeightedPieceList = par6ArrayList;
        this.terrainType = par7;
    }

    public WorldChunkManager getWorldChunkMngr()
    {
        return this.worldChunkMngr;
    }
}
