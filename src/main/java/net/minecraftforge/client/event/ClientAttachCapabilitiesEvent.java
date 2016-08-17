package net.minecraftforge.client.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Fired whenever client object with Capabilities support {currently JSON Model Hierarchy)
 * is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the sub-classes to filter your intended objects.
 */
public class ClientAttachCapabilitiesEvent extends AttachCapabilitiesEvent
{

    public ClientAttachCapabilitiesEvent(Object obj)
    {
        super(obj);
    }
    
    /**
     * A version of the parent event which is only fired for VanillaModelWrappers.
     */
    public static class VanillaModelWrapper extends ClientAttachCapabilitiesEvent
    {
        private final net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper modelWrapper;
        public VanillaModelWrapper(net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper modelWrapper)
        {
            super(modelWrapper);
            this.modelWrapper = modelWrapper;
        }
        public net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
    
    /**
     * A version of the parent event which is only fired for ItemLayerModels.
     */
    public static class ItemLayerModel extends ClientAttachCapabilitiesEvent
    {
        private final net.minecraftforge.client.model.ItemLayerModel modelWrapper;
        public ItemLayerModel(net.minecraftforge.client.model.ItemLayerModel modelWrapper)
        {
            super(modelWrapper);
            this.modelWrapper = modelWrapper;
        }
        public net.minecraftforge.client.model.ItemLayerModel getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
    
    /**
     * A version of the parent event which is only fired for ModelBlocks.
     */
    public static class ModelBlock extends ClientAttachCapabilitiesEvent
    {
        private final net.minecraft.client.renderer.block.model.ModelBlock modelWrapper;
        public ModelBlock(net.minecraft.client.renderer.block.model.ModelBlock modelWrapper)
        {
            super(modelWrapper);
            this.modelWrapper = modelWrapper;
        }
        public net.minecraft.client.renderer.block.model.ModelBlock getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
    
    /**
     * A version of the parent event which is only fired for BlockParts.
     */
    public static class BlockPart extends ClientAttachCapabilitiesEvent
    {
        private final net.minecraft.client.renderer.block.model.BlockPart modelWrapper;
        public BlockPart(net.minecraft.client.renderer.block.model.BlockPart modelWrapper)
        {
            super(modelWrapper);
            this.modelWrapper = modelWrapper;
        }
        public net.minecraft.client.renderer.block.model.BlockPart getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
    
    /**
     * A version of the parent event which is only fired for BlockPartFaces.
     */
    public static class BlockPartFace extends ClientAttachCapabilitiesEvent
    {
        private final net.minecraft.client.renderer.block.model.BlockPartFace modelWrapper;
        public BlockPartFace(net.minecraft.client.renderer.block.model.BlockPartFace modelWrapper)
        {
            super(modelWrapper);
            this.modelWrapper = modelWrapper;
        }
        public net.minecraft.client.renderer.block.model.BlockPartFace getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
}
