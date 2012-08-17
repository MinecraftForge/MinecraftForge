package cpw.mods.fml.client.registry;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraft.src.TileEntitySpecialRenderer;
import cpw.mods.fml.common.registry.GameRegistry;

public class ClientRegistry
{
    /**
     * 
     * Utility method for registering a tile entity and it's renderer at once - generally you should register them separately
     * 
     * @param tileEntityClass
     * @param id
     * @param specialRenderer
     */
    public static void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id, TileEntitySpecialRenderer specialRenderer)
    {
        GameRegistry.registerTileEntity(tileEntityClass, id);
        bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
    }
    
    public static void bindTileEntitySpecialRenderer(Class <? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer specialRenderer)
    {
        TileEntityRenderer.field_76963_a.field_76966_m.put(tileEntityClass, specialRenderer);
        specialRenderer.func_76893_a(TileEntityRenderer.field_76963_a);
    }
}
