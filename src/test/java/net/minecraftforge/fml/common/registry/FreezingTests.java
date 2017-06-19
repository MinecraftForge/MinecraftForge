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
    }

    public static IForgeRegistry<RTest> registry;
    public static ResourceLocation resloc = new ResourceLocation("fmltest:test");

    @BeforeClass
    public static void setup()
    {
        Loader.instance();
        System.setProperty("fml.queryResult", "confirm");
        System.setProperty("forge.disableVanillaGameData", "true");
        registry = new RegistryBuilder<RTest>().setName(resloc).setType(RTest.class).setIDRange(0, 255).create();
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
        registry = new RegistryBuilder<RTest>().setName(resloc).setType(RTest.class).setIDRange(0, 255).create();
        new RegistryBuilder<Block>().setName(GameData.BLOCKS).setType(Block.class).setIDRange(0, 255).create();
        new RegistryBuilder<Item>().setName(GameData.ITEMS).setType(Item.class).setIDRange(0, 255).create();
    }

    @Test
    public void testFreezeCycle()
    {
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r6);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r5);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r4);
        RegistryManager.ACTIVE.getRegistry(RTest.class).register(r3);
        ForgeRegistry<RTest> r = (ForgeRegistry<RTest>)RegistryManager.ACTIVE.getRegistry(r3.getRegistryType());
        int r3id = r.getID(r3);
        GameData.freezeData();
        assertEquals("Frozen object is the same", r3, RegistryManager.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        assertEquals("Active object is the same", r3, RegistryManager.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        GameData.injectSnapshot(ss, false, false);
        assertNotEquals("IDs don't match", r3id, r.getID(r3));
        assertEquals("Frozen object is the same", r3, RegistryManager.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        assertEquals("Active object is the same", r3, RegistryManager.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        GameData.revertToFrozen();
        assertEquals("IDs match", r3id, r.getID(r3));
        assertEquals("Frozen object is the same", r3, RegistryManager.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        assertEquals("Active object is the same", r3, RegistryManager.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        GameData.injectSnapshot(ss, true, true);
        assertNotEquals("IDs don't match", r3id, r.getID(r3));
        assertEquals("Frozen object is the same", r3, RegistryManager.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        assertEquals("Active object is the same", r3, RegistryManager.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        GameData.revertToFrozen();
        assertEquals("IDs match", r3id, r.getID(r3));
        assertEquals("Frozen object is the same", r3, RegistryManager.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
        assertEquals("Active object is the same", r3, RegistryManager.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3")));
    }
}
