/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

/**
 * Vanilla registry tests
 */
@RunWith(ForgeTestRunner.class)
public class VanillaRegistryTests
{
    @BeforeClass
    public static void setupHarness()
    {
        Loader.instance();
        Bootstrap.register();
    }

    @Test
    public void testSetup()
    {
        // All the blocks loaded
        assertEquals("We have all the blocks via GameData", 254, Block.REGISTRY.getKeys().size());

        // All the items loaded
        assertEquals("We have all the items via GameData", 411, Item.REGISTRY.getKeys().size());

        // Our lookups find the same stuff vanilla sees
        final IForgeRegistry<Block> blocks = RegistryManager.ACTIVE.getRegistry(Block.class);
        assertEquals("We have a different block registry then vanilla", blocks, getDelegate(Block.REGISTRY));

        // We can look up stuff through our APIs
        Block bl = blocks.getValue(new ResourceLocation("minecraft:air"));
        assertEquals("We got air when we asked for it", Blocks.AIR, bl);

        // Default values work
        Block blch = blocks.getValue(new ResourceLocation("minecraft:cheese"));
        assertEquals("We got air when we asked for cheese", Blocks.AIR, blch);

        // Our lookups find the same stuff vanilla sees
        final IForgeRegistry<Item> items = RegistryManager.ACTIVE.getRegistry(Item.class);
        assertEquals("We have a different item registry then vanilla", items, getDelegate(Item.REGISTRY));

        // We can look up stuff through our APIs
        Item it = items.getValue(new ResourceLocation("minecraft:bed"));
        assertEquals("We got a bed item when we asked for it", Items.BED, it);

        // We find nothing for a non-defaulted registry
        Item none = items.getValue(new ResourceLocation("minecraft:cheese"));
        assertEquals("We got nothing (items) when we asked for cheese", null, none);
    }

    private Object getDelegate(Object obj)
    {
        try
        {
            Field f = obj.getClass().getDeclaredField("delegate");
            f.setAccessible(true);
            return f.get(obj);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegistration()
    {
        final IForgeRegistry<Block> blocks = RegistryManager.ACTIVE.getRegistry(Block.class);
        Block myBlock = (new Block(Material.CAKE){}).setRegistryName(new ResourceLocation("minecraft:testy"));
        blocks.register(myBlock);
        assertNotNull("Registered my block", myBlock);

        // Our lookups find the same stuff vanilla sees
        assertEquals("We have a different block registry then vanilla", blocks, getDelegate(Block.REGISTRY));

        Block found = blocks.getValue(new ResourceLocation("minecraft:testy"));
        assertEquals("Registry lookup works", myBlock, found);
    }

    @Test
    public void testRegistryStates()
    {
        final ForgeRegistry<Block> blockVanilla = (ForgeRegistry<Block>)RegistryManager.VANILLA.getRegistry(Block.class);
        final ForgeRegistry<Block> blockActive = (ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(Block.class);

        assertNotEquals("Registry states are distinct", blockActive, blockVanilla);

        final Block stoneActive = blockActive.getValue(new ResourceLocation("minecraft:stone"));
        final Block stoneVanilla = blockVanilla.getValue(new ResourceLocation("minecraft:stone"));

        assertEquals("Stone from active and vanilla are the same", stoneActive, stoneVanilla);

        int activeId = blockActive.getID(stoneActive);
        int vanillaId = blockVanilla.getID(stoneVanilla);

        assertEquals("Stone has correct id", 1, activeId);
        assertEquals("Stone has correct id", 1, vanillaId);
    }


}
