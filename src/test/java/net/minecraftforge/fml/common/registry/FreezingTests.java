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
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;

/**
 * Created by cpw on 04/07/16.
 */
@RunWith(ForgeTestRunner.class)
public class FreezingTests
{

    private static RTest r1;
    private static RTest r2;
    private static RTest r3;
    private static RTest r4;
    private static RTest r5;
    private static RTest r6;
    private static Map<ResourceLocation, ForgeRegistry.Snapshot> ss;

    static class RTest extends IForgeRegistryEntry.Impl<RTest>
    {
        public RTest(String name)
        {
            setRegistryName(name);
        }
        @Override
        public String toString()
        {
            return this.getRegistryName().toString();
        }
    }

    public static ResourceLocation resloc = new ResourceLocation("fmltest:test");

    @BeforeClass
    public static void setup()
    {
        Loader.instance();
        System.setProperty("fml.queryResult", "confirm");
        System.setProperty("fml.doNotBackup", "true");
        System.setProperty("forge.disableVanillaGameData", "true");

        new RegistryBuilder<RTest>().setName(resloc).setType(RTest.class).setIDRange(0, 255).create();
        new RegistryBuilder<Block>().setName(GameData.BLOCKS).setType(Block.class).setIDRange(0, 255).create();
        new RegistryBuilder<Item>().setName(GameData.ITEMS).setType(Item.class).setIDRange(0, 255).create();

        r1 = new RTest("test1");
        r2 = new RTest("test2");
        r3 = new RTest("test3");
        r4 = new RTest("test4");
        r5 = new RTest("test5");
        r6 = new RTest("test6");

        ss = Maps.newHashMap();
        ss.put(GameData.BLOCKS, new ForgeRegistry.Snapshot());
        ss.put(GameData.ITEMS, new ForgeRegistry.Snapshot());

        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r1);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r2);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r3);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r4);
        ss.put(resloc, ((ForgeRegistry<?>)RegistryManager.ACTIVE.getRegistry(RTest.class)).makeSnapshot());

        RegistryManager.ACTIVE.clean();
        RegistryManager.FROZEN.clean();
        new RegistryBuilder<RTest>().setName(resloc).setType(RTest.class).setIDRange(0, 255).create();
        new RegistryBuilder<Block>().setName(GameData.BLOCKS).setType(Block.class).setIDRange(0, 255).create();
        new RegistryBuilder<Item>().setName(GameData.ITEMS).setType(Item.class).setIDRange(0, 255).create();
    }

    @Test
    public void testFreezeCycle()
    {
        ResourceLocation name = new ResourceLocation("test3");
        ForgeRegistry<RTest> active = (ForgeRegistry<RTest>)RegistryManager.ACTIVE.getRegistry(RTest.class);
        active.register(r6);
        active.register(r5);
        active.register(r4);
        active.register(r3);

        int r3id = active.getID(r3);
        GameData.freezeData();

        //Frozen data should be the same, as there are no replacement
        ForgeRegistry<RTest> frozen = (ForgeRegistry<RTest>)RegistryManager.ACTIVE.getRegistry(RTest.class);
        assertEquals("Frozen object not the same", r3, frozen.getValue(name));
        assertEquals("Active object not the same", r3, active.getValue(name));

        // r3 is in the snapshot, so the ID SHOULD change to whats in the snapshot.
        GameData.injectSnapshot(ss, false, true); //Unlike the old system we die on missing mappings for custom registries. So we need to tell it to continue loading as if we're local
        assertNotEquals("IDs match", r3id, active.getID(r3));
        assertEquals("Frozen object not the same", r3, frozen.getValue(name));
        assertEquals("Active object not the same", r3, active.getValue(name));

        // Frozen has the original ID
        GameData.revertToFrozen();
        assertEquals("IDs don't match", r3id, active.getID(r3));
        assertEquals("Frozen object not the same", r3, frozen.getValue(name));
        assertEquals("Active object not the same", r3, active.getValue(name));

        // Make sure we have snapshot ID again
        GameData.injectSnapshot(ss, true, true);
        assertNotEquals("IDs match", r3id, active.getID(r3));
        assertEquals("Frozen object not the same", r3, frozen.getValue(name));
        assertEquals("Active object not the same", r3, active.getValue(name));

        //And back to the frozen ID
        GameData.revertToFrozen();
        assertEquals("IDs don't match", r3id, active.getID(r3));
        assertEquals("Frozen object not the same", r3, frozen.getValue(name));
        assertEquals("Active object not the same", r3, active.getValue(name));
    }
}
