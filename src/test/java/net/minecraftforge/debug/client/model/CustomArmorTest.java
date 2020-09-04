package net.minecraftforge.debug.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.armor.ArmorModelRegistry;
import net.minecraftforge.client.model.armor.HorseArmorModel;
import net.minecraftforge.client.model.armor.IModelProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomArmorTest.MODID)
public class CustomArmorTest
{
    public static final String MODID = "custom_armor_test";

    public CustomArmorTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

    private void clientSetup(final FMLClientSetupEvent event)
    {
        
        ArmorModelRegistry.attachBipedArmorModel(Items.DIAMOND_HELMET, new IModelProvider<BipedModel<?>>()
        {
            final BipedArmorModelTest test = new BipedArmorModelTest(1.0F);
            final ResourceLocation armor = new ResourceLocation(MODID, "textures/models/armor/diamond_layer_1.png");

            @SuppressWarnings("unchecked")
            @Override
            public <A extends BipedModel<?>> A getModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default)
            {
                return (A) test;
            }

            @Override
            public ResourceLocation getTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, String textureTypeIn)
            {
                return armor;
            }
        });
        ArmorModelRegistry.attachBipedArmorModel(Items.DIAMOND_LEGGINGS, new IModelProvider<BipedModel<?>>()
        {
            final BipedArmorModelTest test = new BipedArmorModelTest(0.5F);
            final ResourceLocation armor = new ResourceLocation(MODID, "textures/models/armor/diamond_layer_2.png");

            @SuppressWarnings("unchecked")
            @Override
            public <A extends BipedModel<?>> A getModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default)
            {
                return (A) test;
            }

            @Override
            public ResourceLocation getTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, String textureTypeIn)
            {
                return armor;
            }
        });
        ArmorModelRegistry.attachHorseArmorModel(Items.DIAMOND_HORSE_ARMOR, new IModelProvider<HorseArmorModel<?>>()
        {
            final HorseArmorModelTest test = new HorseArmorModelTest(0.1F);
            final ResourceLocation armor = new ResourceLocation(MODID, "textures/models/armor/horse_armor_diamond.png");

            @SuppressWarnings("unchecked")
            @Override
            public <A extends HorseArmorModel<?>> A getModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default)
            {
                return (A) test;
            }

            @Override
            public ResourceLocation getTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, String textureTypeIn)
            {
                return armor;
            }
        });
    }

    static class BipedArmorModelTest extends BipedModel<LivingEntity>
    {
        private BipedArmorModelTest(float modelSize)
        {
            super(modelSize, 0.0F, 64, 64); //Need to set biped details before registry
            ModelRenderer horn = new ModelRenderer(this);
            horn.setRotationPoint(0.0F, -7.0F, -1.0F);
            bipedHead.addChild(horn);
            horn.rotateAngleX = 0.4363F;
            horn.setTextureOffset(0, 32).addBox(-1.0F, -8.0F, 0.0F, 2.0F, 8.0F, 2.0F, modelSize);
            ModelRenderer backSpineRight = new ModelRenderer(this);
            backSpineRight.setRotationPoint(0.0F, 0.0F, 0.0F);
            bipedRightLeg.addChild(backSpineRight);
            backSpineRight.rotateAngleX = -0.2618F;
            backSpineRight.setTextureOffset(8, 32).addBox(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 6.0F, modelSize);
            ModelRenderer backSpineLeft = new ModelRenderer(this);
            backSpineLeft.setRotationPoint(-4.0F, 0.0F, 0.0F);
            bipedLeftLeg.addChild(backSpineLeft);
            backSpineLeft.rotateAngleX = -0.2618F;
            backSpineLeft.setTextureOffset(8, 32).addBox(3.0F, 1.0F, 1.0F, 2.0F, 2.0F, 6.0F, modelSize);
        }
    }

    static class HorseArmorModelTest extends HorseArmorModel<HorseEntity>
    {
        private HorseArmorModelTest(float modelSize)
        {
            super(modelSize, 128, 64); //Same as above, set the horse details before registry
            ModelRenderer heldObject = new ModelRenderer(this);
            heldObject.setRotationPoint(0.0F, 1.0F, 0.0F);
            rightFrontLeg.addChild(heldObject);
            heldObject.rotateAngleX = -0.6109F;
            heldObject.rotateAngleY = -0.5236F;
            heldObject.setTextureOffset(64, 12).addBox(-1.2321F, 1.7554F, -9.4791F, 2.0F, 2.0F, 11.0F, 0.0F, false);
            ModelRenderer horn = new ModelRenderer(this);
            horn.setRotationPoint(0.0F, 0.0F, 0.0F);
            neck.addChild(horn);
            horn.setTextureOffset(64, 0).addBox(-1.0F, -20.1244F, 0.0718F, 2.0F, 10.0F, 2.0F, 0.0F, false);
        }
    }
}
