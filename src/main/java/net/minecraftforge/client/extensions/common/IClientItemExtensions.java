/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * {@linkplain LogicalSide#CLIENT Client-only} extensions to {@link Item}.
 *
 * @see Item#initializeClient(Consumer)
 */
public interface IClientItemExtensions
{
    IClientItemExtensions DEFAULT = new IClientItemExtensions() { };

    static IClientItemExtensions of(ItemStack stack)
    {
        return of(stack.getItem());
    }

    static IClientItemExtensions of(Item item)
    {
        return item.getRenderPropertiesInternal() instanceof IClientItemExtensions e ? e : DEFAULT;
    }

    /**
     * Returns the font used to render data related to this item as specified in the {@code context}.
     * Return {@code null} to use the default font.
     *
     * @param stack   The item stack
     * @param context The context in which the font will be used
     * @return A {@link Font} or null to use the default
     */
    @Nullable
    default Font getFont(ItemStack stack, FontContext context)
    {
        return null;
    }

    /**
     * Queries the humanoid armor model for this item when it's equipped.
     *
     * @param livingEntity  The entity wearing the armor
     * @param itemStack     The item stack
     * @param equipmentSlot The slot the item is in
     * @param original      The original armor model. Will have attributes set.
     * @return A HumanoidModel to be rendered. Relevant properties are to be copied over by the caller.
     * @see #getGenericArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)
     */
    @NotNull
    default HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original)
    {
        return original;
    }

    /**
     * Queries the armor model for this item when it's equipped. Useful in place of
     * {@link #getHumanoidArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)} for wrapping the original
     * model or returning anything non-standard.
     * <p>
     * If you override this method you are responsible for copying any properties you care about from the original model.
     *
     * @param livingEntity  The entity wearing the armor
     * @param itemStack     The item stack
     * @param equipmentSlot The slot the item is in
     * @param original      The original armor model. Will have attributes set.
     * @return A Model to be rendered. Relevant properties must be copied over manually.
     * @see #getHumanoidArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel)
     */
    @NotNull
    default Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original)
    {
        HumanoidModel<?> replacement = getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
        if (replacement != original)
        {
            ForgeHooksClient.copyModelProperties(original, replacement);
            return replacement;
        }
        return original;
    }

    /**
     * Called when the client starts rendering the HUD, and is wearing this item in the helmet slot.
     * <p>
     * This is where pumpkins would render their overlay.
     *
     * @param stack       The item stack
     * @param player      The player entity
     * @param width       The viewport width
     * @param height      Viewport height
     * @param partialTick Partial tick time, useful for interpolation
     */
    default void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick)
    {
    }

    /**
     * Queries this item's renderer.
     * <p>
     * Only used if {@link BakedModel#isCustomRenderer()} returns {@code true} or {@link BlockState#getRenderShape()}
     * returns {@link net.minecraft.world.level.block.RenderShape#ENTITYBLOCK_ANIMATED}.
     * <p>
     * By default, returns vanilla's block entity renderer.
     */
    default BlockEntityWithoutLevelRenderer getCustomRenderer()
    {
        return Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer();
    }

    enum FontContext
    {
        /**
         * Used to display the amount of items in the {@link ItemStack}.
         */
        ITEM_COUNT,
        /**
         * Used to display text in the {@link net.minecraft.world.inventory.tooltip.TooltipComponent}.
         */
        TOOLTIP,
        /**
         * Used to display the item name above the hotbar when the player selects it.
         */
        SELECTED_ITEM_NAME
    }
}
