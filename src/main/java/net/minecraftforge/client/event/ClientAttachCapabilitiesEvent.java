/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Fired whenever client object with Capabilities support (currently JSON Model Hierarchy) is created. Allowing for the attachment of arbitrary capability providers.
 *
 * Please note that as this is fired for ALL object creations efficient code is recommended. And if possible use one of the sub-classes to filter your intended objects.
 */
public class ClientAttachCapabilitiesEvent<T> extends AttachCapabilitiesEvent<T>
{

    public ClientAttachCapabilitiesEvent(Class<T> type, T obj)
    {
        super(type, obj);
    }

    /**
     * A version of the parent event which is only fired for VanillaModelWrappers.
     */
    public static class VanillaModelWrapper extends ClientAttachCapabilitiesEvent<net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper>
    {
        private final net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper modelWrapper;

        public VanillaModelWrapper(net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper modelWrapper)
        {
            super(net.minecraftforge.client.model.ModelLoader.VanillaModelWrapper.class, modelWrapper);
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
    public static class ItemLayerModel extends ClientAttachCapabilitiesEvent<net.minecraftforge.client.model.ItemLayerModel>
    {
        private final net.minecraftforge.client.model.ItemLayerModel modelWrapper;

        public ItemLayerModel(net.minecraftforge.client.model.ItemLayerModel modelWrapper)
        {
            super(net.minecraftforge.client.model.ItemLayerModel.class, modelWrapper);
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
    public static class ModelBlock extends ClientAttachCapabilitiesEvent<net.minecraft.client.renderer.block.model.ModelBlock>
    {
        private final net.minecraft.client.renderer.block.model.ModelBlock modelWrapper;

        public ModelBlock(net.minecraft.client.renderer.block.model.ModelBlock modelWrapper)
        {
            super(net.minecraft.client.renderer.block.model.ModelBlock.class, modelWrapper);
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
    public static class BlockPart extends ClientAttachCapabilitiesEvent<net.minecraft.client.renderer.block.model.BlockPart>
    {
        private final net.minecraft.client.renderer.block.model.BlockPart modelWrapper;

        public BlockPart(net.minecraft.client.renderer.block.model.BlockPart modelWrapper)
        {
            super(net.minecraft.client.renderer.block.model.BlockPart.class, modelWrapper);
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
    public static class BlockPartFace extends ClientAttachCapabilitiesEvent<net.minecraft.client.renderer.block.model.BlockPartFace>
    {
        private final net.minecraft.client.renderer.block.model.BlockPartFace modelWrapper;

        public BlockPartFace(net.minecraft.client.renderer.block.model.BlockPartFace modelWrapper)
        {
            super(net.minecraft.client.renderer.block.model.BlockPartFace.class, modelWrapper);
            this.modelWrapper = modelWrapper;
        }

        public net.minecraft.client.renderer.block.model.BlockPartFace getModelWrapper()
        {
            return this.modelWrapper;
        }
    }
}
