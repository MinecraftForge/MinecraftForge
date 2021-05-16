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

package net.minecraftforge.client;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.function.Function;

public class ItemClientProperties
{
    private ItemClientProperties()
    {
        // Not instantiable.
    }

    private static final Map<Item, Function<ItemStack, FontRenderer>> fontProviders = Maps.newHashMap();
    private static final Map<Item, IArmorModelProvider> armorModelProviders = Maps.newHashMap();
    private static final Map<Item, IHelmetOverlayRenderer> helmetOverlayRenderers = Maps.newHashMap();
    private static final Map<Item, ItemStackTileEntityRenderer> itemStackTileEntityRenderers = Maps.newHashMap();

    public static synchronized void registerFontProvider(Item item, Function<ItemStack, FontRenderer> fontProvider)
    {
        if (fontProviders.containsKey(item))
            throw new IllegalStateException("A font provider has already been registered for this item.");

        fontProviders.put(item, fontProvider);
    }

    public static synchronized void registerArmorModelProvider(Item item, IArmorModelProvider armorModelProvider)
    {
        if (armorModelProviders.containsKey(item))
            throw new IllegalStateException("An armor model provider has already been registered for this item.");

        armorModelProviders.put(item, armorModelProvider);
    }

    public static synchronized void registerFontProvider(Item item, IHelmetOverlayRenderer helmetOverlayRenderer)
    {
        if (helmetOverlayRenderers.containsKey(item))
            throw new IllegalStateException("An armor model provider has already been registered for this item.");

        helmetOverlayRenderers.put(item, helmetOverlayRenderer);
    }

    public static synchronized void registerFontProvider(Item item, ItemStackTileEntityRenderer itemStackTileEntityRenderer)
    {
        if (itemStackTileEntityRenderers.containsKey(item))
            throw new IllegalStateException("An ItemStackTileEntityRenderer has already been registered for this item.");

        itemStackTileEntityRenderers.put(item, itemStackTileEntityRenderer);
    }

    public static FontRenderer getFont(ItemStack stack)
    {
        Function<ItemStack, FontRenderer> provider = fontProviders.get(stack.getItem());
        if (provider == null)
            return null;
        return provider.apply(stack);
    }

    public static <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A defaultModel)
    {
        IArmorModelProvider armorModelProvider = armorModelProviders.get(itemStack.getItem());
        if (armorModelProvider == null)
            return null;
        return armorModelProvider.getArmorModel(entityLiving, itemStack, armorSlot, defaultModel);
    }

    public static void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)
    {
        IHelmetOverlayRenderer helmetOverlayRenderer = helmetOverlayRenderers.get(stack.getItem());
        if (helmetOverlayRenderer != null)
            helmetOverlayRenderer.renderHelmetOverlay(stack, player, width, height, partialTicks);
    }

    public static ItemStackTileEntityRenderer getItemStackTileEntityRenderer(ItemStack stack)
    {
        return itemStackTileEntityRenderers.getOrDefault(stack.getItem(), ItemStackTileEntityRenderer.instance);
    }

    public interface IArmorModelProvider
    {
        <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A defaultModel);
    }

    public interface IHelmetOverlayRenderer
    {
        void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks);
    }
}
