package net.minecraftforge.client;

import java.util.Collection;
import java.util.IdentityHashMap;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.FMLLog;

public class WorldRendererRegistry {
    private static final IdentityHashMap<Block, IWorldRenderer> renderMap = new IdentityHashMap<Block, IWorldRenderer>();

    public static void registerRenderer(IWorldRenderer renderer, Block ... blocks)
    {
        for(Block block : blocks)
        {
            if(renderMap.containsKey(block))
            {
                FMLLog.severe("Duplicate renderer for block %s: have %s, registering %s; ignoring", block, renderMap.get(block), renderer);
            }
            else
            {
                renderMap.put(block, renderer);
            }
        }
    }

    public static IWorldRenderer getRenderer(Block block)
    {
        return renderMap.get(block);
    }
    public static Collection<IWorldRenderer> getAllRenderers()
    {
        return renderMap.values();
    }
}
