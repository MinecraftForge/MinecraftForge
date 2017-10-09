package net.minecraftforge.test;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.unification.UnificationConstants;
import net.minecraftforge.unification.UnificationManager;
import net.minecraftforge.unification.UnifiedForm;
import net.minecraftforge.unification.UnifiedMaterial;
import net.minecraftforge.unification.Unifier;
import net.minecraftforge.unification.Unified;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnifierTest
{
    private static class TestItem
    {
        @Nullable
        private ResourceLocation registryName;

        public TestItem(@Nullable ResourceLocation registryName)
        {
            this.registryName = registryName;
        }

        @Nullable
        public ResourceLocation getRegistryName()
        {
            return registryName;
        }

        public void setRegistryName(@Nullable ResourceLocation registryName)
        {
            this.registryName = registryName;
        }

        public TestItem copy()
        {
            return new TestItem(registryName);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TestItem)
            {
                TestItem other = (TestItem) obj;
                return Objects.equals(registryName, other.registryName);
            }
            return false;
        }

        @Override
        public String toString()
        {
            return String.valueOf(registryName);
        }
    }

    private static final UnifiedMaterial TOPAZ = UnificationManager.getMaterial("topaz");
    @Nullable
    private static Unifier<TestItem> unifier;

    @BeforeEach
    public void beforeEach()
    {
        Consumer<TestItem> validator = testItem -> Preconditions.checkNotNull(testItem.registryName);
        unifier = UnificationManager.createUnifier(validator, TestItem::getRegistryName, TestItem::copy, false);
    }

    @Test
    public void testMaterialsAndForms()
    {
        assertNotNull(TOPAZ);
        assertSame(TOPAZ, UnificationManager.getMaterial("topaz"));

        UnifiedForm ore = UnificationManager.getForm("ore");
        assertSame(UnificationConstants.ORE, ore);
    }

    @Test
    public void testUnification()
    {
        Preconditions.checkNotNull(unifier);

        TestItem topazOreItem1 = new TestItem(new ResourceLocation("TestMod1", "topaz_ore"));
        unifier.add(TOPAZ, UnificationConstants.ORE, topazOreItem1);
        Unified<TestItem> unifiedTopazOre = unifier.get(TOPAZ, UnificationConstants.ORE);
        assertNotNull(unifiedTopazOre);
        assertNotNull(unifiedTopazOre.get());
        assertSame(unifiedTopazOre.getForm(), UnificationConstants.ORE);
        assertSame(unifiedTopazOre.getMaterial(), TOPAZ);
        assertEquals(1, unifiedTopazOre.getVariants().size());

        TestItem topazOreItem2 = new TestItem(new ResourceLocation("TestMod2", "topaz_ore"));
        unifier.add(TOPAZ, UnificationConstants.ORE, topazOreItem2);
        Unified<TestItem> unifiedTopazOre2 = unifier.get(TOPAZ, UnificationConstants.ORE);
        assertSame(unifiedTopazOre, unifiedTopazOre2);

        assertEquals(2, unifiedTopazOre.getVariants().size());
    }

    @Test
    public void testAddingInvalidVariants()
    {
        Preconditions.checkNotNull(unifier);

        TestItem invalidTestItem = new TestItem(null);

        assertThrows(RuntimeException.class, () -> unifier.add(TOPAZ, UnificationConstants.ORE, invalidTestItem));
    }

    @Test
    public void testVariantImmutability()
    {
        Preconditions.checkNotNull(unifier);

        ResourceLocation originalName = new ResourceLocation("TestMod1", "name1");
        ResourceLocation newName = new ResourceLocation("TestMod2", "name2");
        TestItem testItem = new TestItem(originalName);
        unifier.add(TOPAZ, UnificationConstants.ORE, testItem);
        Unified<TestItem> unified = unifier.get(TOPAZ, UnificationConstants.ORE);
        assertNotNull(unified);
        assertEquals(originalName, unified.get().getRegistryName());

        testItem.setRegistryName(newName);
        assertEquals(originalName, unified.get().getRegistryName());

        unified.get().setRegistryName(newName);
        assertEquals(originalName, unified.get().getRegistryName());
    }

    @Test
    public void testAddingExactDuplicates()
    {
        Preconditions.checkNotNull(unifier);

        TestItem topazOreItem = new TestItem(new ResourceLocation("TestMod1", "topaz_ore"));
        unifier.add(TOPAZ, UnificationConstants.ORE, topazOreItem);
        Unified<TestItem> unifiedTopazOre = unifier.get(TOPAZ, UnificationConstants.ORE);
        assertNotNull(unifiedTopazOre);
        assertEquals(1, unifiedTopazOre.getVariants().size());

        // add the same exact thing again
        unifier.add(TOPAZ, UnificationConstants.ORE, topazOreItem);
        Unified<TestItem> unifiedTopazOre2 = unifier.get(TOPAZ, UnificationConstants.ORE);
        assertNotNull(unifiedTopazOre2);
        assertEquals(unifiedTopazOre, unifiedTopazOre2);
        assertEquals(1, unifiedTopazOre2.getVariants().size(), "Adding an exact duplicate should not increase the number of variants");
    }
}
