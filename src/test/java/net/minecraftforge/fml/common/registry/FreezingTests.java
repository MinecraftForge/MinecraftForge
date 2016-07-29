package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

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
    private static PersistentRegistryManager.GameDataSnapshot ss;

    static class RTest extends IForgeRegistryEntry.Impl<RTest> {
        public RTest(String name) {
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
        registry = PersistentRegistryManager.createRegistry(resloc, RTest.class, null, 0, 255, false, null, null, null, null);
        PersistentRegistryManager.createRegistry(PersistentRegistryManager.BLOCKS, Block.class, null, 0, 255, false, null, null, null, null);
        PersistentRegistryManager.createRegistry(PersistentRegistryManager.ITEMS, Item.class, null, 0, 255, false, null, null, null, null);
        r1 = new RTest("test1");
        r2 = new RTest("test2");
        r3 = new RTest("test3");
        r4 = new RTest("test4");
        r5 = new RTest("test5");
        r6 = new RTest("test6");
        ss = new PersistentRegistryManager.GameDataSnapshot();
        ss.entries.put(PersistentRegistryManager.BLOCKS, new PersistentRegistryManager.GameDataSnapshot.Entry());
        ss.entries.put(PersistentRegistryManager.ITEMS, new PersistentRegistryManager.GameDataSnapshot.Entry());
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r1);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r2);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r3);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r4);
        ss.entries.put(resloc, new PersistentRegistryManager.GameDataSnapshot.Entry(PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class)));
        PersistentRegistryManager.PersistentRegistry.ACTIVE.clean();
        PersistentRegistryManager.PersistentRegistry.FROZEN.clean();
        registry = PersistentRegistryManager.createRegistry(resloc, RTest.class, null, 0, 255, false, null, null, null, null);
        PersistentRegistryManager.createRegistry(PersistentRegistryManager.BLOCKS, Block.class, null, 0, 255, false, null, null, null, null);
        PersistentRegistryManager.createRegistry(PersistentRegistryManager.ITEMS, Item.class, null, 0, 255, false, null, null, null, null);
    }

    @Test
    public void testFreezeCycle()
    {
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r6);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r5);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r4);
        PersistentRegistryManager.findRegistryByType(RTest.class).register(r3);
        FMLControlledNamespacedRegistry<RTest> r = (FMLControlledNamespacedRegistry)PersistentRegistryManager.findRegistry(r3);
        int r3id = r.getId(r3);
        PersistentRegistryManager.freezeData();
        RTest q1 = PersistentRegistryManager.PersistentRegistry.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Frozen object is the same", r3, q1);
        q1 = PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Active object is the same", r3, q1);
        PersistentRegistryManager.injectSnapshot(ss, false, false);
        assertNotEquals("IDs don't match", r3id, r.getId(r3));
        q1 = PersistentRegistryManager.PersistentRegistry.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Frozen object is the same", r3, q1);
        q1 = PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Active object is the same", r3, q1);
        PersistentRegistryManager.revertToFrozen();
        assertEquals("IDs match", r3id, r.getId(r3));
        q1 = PersistentRegistryManager.PersistentRegistry.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Frozen object is the same", r3, q1);
        q1 = PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Active object is the same", r3, q1);
        PersistentRegistryManager.injectSnapshot(ss, true, true);
        assertNotEquals("IDs don't match", r3id, r.getId(r3));
        q1 = PersistentRegistryManager.PersistentRegistry.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Frozen object is the same", r3, q1);
        q1 = PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Active object is the same", r3, q1);
        PersistentRegistryManager.revertToFrozen();
        assertEquals("IDs match", r3id, r.getId(r3));
        q1 = PersistentRegistryManager.PersistentRegistry.FROZEN.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Frozen object is the same", r3, q1);
        q1 = PersistentRegistryManager.PersistentRegistry.ACTIVE.getRegistry(RTest.class).getValue(new ResourceLocation("test3"));
        assertEquals("Active object is the same", r3, q1);
    }
}
