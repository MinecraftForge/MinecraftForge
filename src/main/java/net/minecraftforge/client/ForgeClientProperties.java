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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Map;

public class ForgeClientProperties
{
    private ForgeClientProperties()
    {
        // Not instantiable.
    }

    private static final Map<Item, IFontProvider> fontProviders = Maps.newHashMap();
    private static final Map<Item, IArmorModelProvider> armorModelProviders = Maps.newHashMap();
    private static final Map<Item, IHelmetOverlayRenderer> helmetOverlayRenderers = Maps.newHashMap();
    private static final Map<Item, ItemStackTileEntityRenderer> itemStackTileEntityRenderers = Maps.newHashMap();

    private static final Map<Block, IHitEffectProvider> hitEffectProviders = Maps.newHashMap();
    private static final Map<Block, IDestroyEffectProvider> destroyEffectProviders = Maps.newHashMap();
    private static final Map<Block, IFogColorProvider> fogColorProviders = Maps.newHashMap();

    private static final Map<Effect, EffectRenderer> effectRenderers = Maps.newHashMap();

    public static synchronized void registerFontProvider(Item item, IFontProvider fontProvider)
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

    public static synchronized void registerHitEffectProvider(Block block, IHitEffectProvider hitEffectProvider)
    {
        if (hitEffectProviders.containsKey(block))
            throw new IllegalStateException("A hit effect provider has already been registered for this block.");

        hitEffectProviders.put(block, hitEffectProvider);
    }

    public static synchronized void registerDestroyEffectProvider(Block block, IDestroyEffectProvider destroyEffectProvider)
    {
        if (destroyEffectProviders.containsKey(block))
            throw new IllegalStateException("A destroy effect provider has already been registered for this block.");

        destroyEffectProviders.put(block, destroyEffectProvider);
    }

    public static synchronized void registerFogColorProvider(Block block, IFogColorProvider fogColorProvider)
    {
        if (fogColorProviders.containsKey(block))
            throw new IllegalStateException("A fog color provider has already been registered for this block.");

        fogColorProviders.put(block, fogColorProvider);
    }

    public static synchronized void registerEffectRenderer(Effect effect, EffectRenderer effectRenderer)
    {
        if (effectRenderers.containsKey(effect))
            throw new IllegalStateException("An effect renderer has already been registered for this effect.");

        effectRenderers.put(effect, effectRenderer);
    }

    public static FontRenderer getFont(ItemStack stack)
    {
        IFontProvider provider = fontProviders.get(stack.getItem());
        if (provider == null)
            return null;
        return provider.getFont(stack);
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

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param state The current state
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla digging particles form spawning.
     */
    public static boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
    {
        IHitEffectProvider provider = hitEffectProviders.get(state.getBlock());
        if (provider == null)
            return false;
        return provider.addHitEffects(state, world, target, manager);
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature
     * of how this is invoked, the x/y/z locations are not always guaranteed
     * to host your block. So be sure to do proper sanity checks before assuming
     * that the location is this block.
     *
     * @param world The current world
     * @param pos Position to spawn the particle
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla break particles from spawning.
     */
    public static boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
    {
        IDestroyEffectProvider provider = destroyEffectProviders.get(state.getBlock());
        if (provider == null)
            return false;
        return provider.addDestroyEffects(state, world, pos, manager);
    }

    /**
     * NOT CURRENTLY IMPLEMENTED
     *
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param world         The world.
     * @param pos           The position at the entity viewport.
     * @param state         The state at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
    public static Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks)
    {
        IFogColorProvider provider = fogColorProviders.get(state.getBlock());
        if (provider != null)
            return provider.getFogColor(state, world, pos, entity, originalColor, partialTicks);

        if (state.getMaterial() == Material.WATER)
        {
            float f12 = 0.0F;

            if (entity instanceof LivingEntity)
            {
                LivingEntity ent = (LivingEntity)entity;
                f12 = (float) EnchantmentHelper.getRespiration(ent) * 0.2F;

                if (ent.hasEffect(Effects.WATER_BREATHING))
                {
                    f12 = f12 * 0.3F + 0.6F;
                }
            }
            return new Vector3d(0.02F + f12, 0.02F + f12, 0.2F + f12);
        }
        else if (state.getMaterial() == Material.LAVA)
        {
            return new Vector3d(0.6F, 0.1F, 0.0F);
        }
        return originalColor;
    }

    public static EffectRenderer getEffectRenderer(EffectInstance effectInstance)
    {
        EffectRenderer renderer = effectRenderers.get(effectInstance.getEffect());
        if (renderer == null)
            return EffectRenderer.DUMMY;
        return renderer;
    }

    public static boolean shouldRender(EffectInstance effectInstance)
    {
        return getEffectRenderer(effectInstance).shouldRender(effectInstance);
    }

    @FunctionalInterface
    public interface IFontProvider
    {
        FontRenderer getFont(ItemStack stack);
    }

    @FunctionalInterface
    public interface IArmorModelProvider
    {
        <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A defaultModel);
    }

    @FunctionalInterface
    public interface IHelmetOverlayRenderer
    {
        void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks);
    }

    @FunctionalInterface
    public interface IHitEffectProvider
    {
        boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager);
    }

    @FunctionalInterface
    public interface IDestroyEffectProvider
    {
        boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager);
    }

    @FunctionalInterface
    public interface IFogColorProvider
    {
        Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks);
    }
}
