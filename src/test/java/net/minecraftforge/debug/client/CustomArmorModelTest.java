/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@Mod(CustomArmorModelTest.MOD_ID)
public class CustomArmorModelTest
{
    static final String MOD_ID = "custom_armor_model_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> CUSTOM_ARMOR_MODEL = ITEMS.register("test_custom_armor_model", () -> new CustomArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

    public CustomArmorModelTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
    }

    private static class CustomArmorItem extends ArmorItem
    {
        public CustomArmorItem(ArmorMaterial material, EquipmentSlot slot, Properties props)
        {
            super(material, slot, props);
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
        {
            return "textures/models/armor/diamond_layer_1.png";
        }

        @Override
        public void initializeClient(Consumer<IItemRenderProperties> consumer)
        {
            consumer.accept(new IItemRenderProperties()
            {
                @Override
                public Model getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
                {
                    OverallsModel.INSTANCE.base = _default;
                    return OverallsModel.INSTANCE;
                }
            });
        }
    }

    private static class OverallsModel extends Model
    {
        private static final OverallsModel INSTANCE = new OverallsModel(RenderType::entityCutoutNoCull);

        private HumanoidModel<?> base;
        private OverallsModel(Function<ResourceLocation,RenderType> renderTypeFunction)
        {
            super(renderTypeFunction);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha)
        {
            if (base != null)
            {
                base.setAllVisible(true);
                base.renderToBuffer(poseStack, consumer, light, overlay, red, green, blue, alpha);
            }
        }
    }
}
