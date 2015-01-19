package net.minecraftforge.client.model.obj.original;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public class CustomMeshModel implements ISmartItemModel, ISmartBlockModel, IInitializeBakedModel {
    String variant;
    ResourceLocation model;
    ItemCameraTransforms transforms;

    List<BakedQuad> faceQuads;
    List<BakedQuad> generalQuads;
    MeshModel sourceMesh;

    TextureAtlasSprite iconSprite;

    public CustomMeshModel(String variant) {
        this.variant = variant;
        this.model = new ResourceLocation("modid", "models/obj/" + variant + ".obj");
//        this.model = new ResourceLocation(ElementsOfPower.MODID, "models/obj/" + variant + ".obj");
        this.faceQuads = new ArrayList<BakedQuad>();
        this.generalQuads = new ArrayList<BakedQuad>();

        try {
            generalQuads.clear();
            sourceMesh = new MeshLoader().loadFromResource(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(ItemCameraTransforms cameraTransforms, ResourceLocation icon, ModelManager modelManager) {
        this.transforms = cameraTransforms;
        this.iconSprite = modelManager.getTextureMap().getAtlasSprite(icon.toString());

        generalQuads = sourceMesh.bakeModel(modelManager);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return this;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return this;
    }

    @Override
    public List getFaceQuads(EnumFacing face) {
        return faceQuads;
    }

    @Override
    public List getGeneralQuads() {
        return generalQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return iconSprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return transforms;
    }
}
