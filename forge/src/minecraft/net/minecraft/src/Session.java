package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class Session
{
    public static List registeredBlocksList = new ArrayList();
    public String username;
    public String sessionId;
    public String mpPassParameter;

    public Session(String par1Str, String par2Str)
    {
        this.username = par1Str;
        this.sessionId = par2Str;
    }

    static
    {
        registeredBlocksList.add(Block.stone);
        registeredBlocksList.add(Block.cobblestone);
        registeredBlocksList.add(Block.brick);
        registeredBlocksList.add(Block.dirt);
        registeredBlocksList.add(Block.planks);
        registeredBlocksList.add(Block.wood);
        registeredBlocksList.add(Block.leaves);
        registeredBlocksList.add(Block.torchWood);
        registeredBlocksList.add(Block.stairSingle);
        registeredBlocksList.add(Block.glass);
        registeredBlocksList.add(Block.cobblestoneMossy);
        registeredBlocksList.add(Block.sapling);
        registeredBlocksList.add(Block.plantYellow);
        registeredBlocksList.add(Block.plantRed);
        registeredBlocksList.add(Block.mushroomBrown);
        registeredBlocksList.add(Block.mushroomRed);
        registeredBlocksList.add(Block.sand);
        registeredBlocksList.add(Block.gravel);
        registeredBlocksList.add(Block.sponge);
        registeredBlocksList.add(Block.cloth);
        registeredBlocksList.add(Block.oreCoal);
        registeredBlocksList.add(Block.oreIron);
        registeredBlocksList.add(Block.oreGold);
        registeredBlocksList.add(Block.blockSteel);
        registeredBlocksList.add(Block.blockGold);
        registeredBlocksList.add(Block.bookShelf);
        registeredBlocksList.add(Block.tnt);
        registeredBlocksList.add(Block.obsidian);
    }
}
