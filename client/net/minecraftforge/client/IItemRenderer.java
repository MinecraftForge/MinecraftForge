package net.minecraftforge.client;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;

public interface IItemRenderer
{
    public enum ItemRenderType
    {
        /** 
         * Called to render an in-world item, e.g. one that has been thrown or
         * dropped. The appropriate OpenGL transformations and scaling have already
         * been applied, so Tessellator location (0,0,0) is the center of the
         * EntityItem.
         * 
         * Data parameters:
         * RenderBlocks render - The RenderBlocks instance
         * EntityItem entity - The in-world item to be rendered
         */
        ENTITY, 
        
        /** 
         * Called to render an item currently held in-hand by a living entity. If
         * rendering as a 3D block, the item will be rotated to a 45-degree angle.
         * To render a 2D texture with some thickness (like default items), see
         * net.minecraft.src.ItemRenderer. In either case, rendering should be done
         * in local coordinates from (0,0,0)-(1,1,1).
         * 
         * Data parameters:
         * RenderBlocks render - The RenderBlocks instance
         * EntityLiving entity - The entity holding this item
         */
        EQUIPPED, 
        
        /** 
         * Called to render an item currently held in-hand by a living entity in
         * first person. If rendering as a 3D block, the item will be rotated to a
         * 45-degree angle. To render a 2D texture with some thickness, see
         * net.minecraft.src.ItemRenderer. In either case, rendering should be done
         * in local coordinates from (0,0,0)-(1,1,1).
         * 
         * Data parameters:
         * RenderBlocks render - The RenderBlocks instance
         * EntityLiving entity - The entity holding this item
         */
        EQUIPPED_FIRST_PERSON, 
        
        /** 
         * Called to render an item in a GUI inventory slot. If rendering as a 3D
         * block, the appropriate OpenGL translations and scaling have already been
         * applied, and the rendering should be done in local coordinates from
         * (0,0,0)-(1,1,1). If rendering as a 2D texture, the rendering should be in
         * GUI pixel coordinates from (0, 0, 0)-(16, 16, 0).
         * 
         * Data parameters:
         * RenderBlocks render - The RenderBlocks instance
         */
        INVENTORY,
        
        /**
         * The render type used for when a ItemMap is rendered in first person, 
         * All appropriate rotations have been applied, and the player's hands, 
         * and the map BG are already rendered.
         * 
         * Data Parameters:
         * EntityPlayer player - The player holding the map
         * RenderEngine engine - The RenderEngine instance
         * MapData mapData - The map data
         */
        FIRST_PERSON_MAP
    }
    
    public enum ItemRendererHelper
    {
        /** 
         * Determines if a rotation effect should be used when rendering an
         * EntityItem, like most default blocks do.
         */
        ENTITY_ROTATION,
        
        /** 
         * Determines if an up-and-down bobbing effect should be used when
         * rendering an EntityItem, like most default items do.
         */
        ENTITY_BOBBING,

        /** 
         * Determines if the currently equipped item should be rendered as a 3D
         * block or as a 2D texture.
         */
        EQUIPPED_BLOCK,
        
        /**
         * Determines if the item should equate to a block that has 
         * RenderBlocks.renderItemIn3d return true
         */
        BLOCK_3D,

        /** 
         * Determines if the item should be rendered in GUI inventory slots as a 3D
         * block or as a 2D texture.
         */
        INVENTORY_BLOCK
    }
    
    /** 
     * Checks if this renderer should handle a specific item's render type
     * @param item The item we are trying to render
     * @param type A render type to check if this renderer handles
     * @return true if this renderer should handle the given render type,
     * otherwise false
     */
    public boolean handleRenderType(ItemStack item, ItemRenderType type);
    
    /**
     * Checks if certain helper functionality should be executed for this renderer.
     * See ItemRendererHelper for more info
     * 
     * @param type The render type
     * @param item The ItemStack being rendered
     * @param helper The type of helper functionality to be ran
     * @return True to run the helper functionality, false to not.
     */
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper);
    
    /**
     * Called to do the actual rendering, see ItemRenderType for details on when specific 
     * types are run, and what extra data is passed into the data parameter.
     * 
     * @param type The render type
     * @param item The ItemStack being rendered
     * @param data Extra Type specific data
     */
    public void renderItem(ItemRenderType type, ItemStack item, Object... data);
}
