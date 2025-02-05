package net.minecraftforge.debug.resource;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.Objects;

@Mod(ResourceLocationTest.MODID)
@GameTestHolder("forge." + ResourceLocationTest.MODID)
public final class ResourceLocationTest {
    static final String MODID = "resource_location";

    @SuppressWarnings("removal") // intentionally using old constructors and methods for testing purposes
    @GameTest(template = "forge:empty3x3x3")
    public static void backported_methods(GameTestHelper helper) {
        var mcLoc = new ResourceLocation("test");
        var forgeLoc = new ResourceLocation("forge", "test");

        assertValueEqual(forgeLoc, ResourceLocation.fromNamespaceAndPath("forge", "test"), "resource location from namespace and path (forge)");
        assertValueEqual(mcLoc, ResourceLocation.withDefaultNamespace("test"), "resource location with default namespace");
        assertValueEqual(mcLoc, ResourceLocation.parse("test"), "resource location parse");
        assertValueEqual(mcLoc, ResourceLocation.parse("minecraft:test"), "resource location parse (with minecraft namespace)");
        assertValueEqual(forgeLoc, ResourceLocation.parse("forge:test"), "resource location parse (with forge namespace)");
        assertValueEqual(mcLoc, ResourceLocation.bySeparator("minecraft/test", '/'), "resource location by separator");
        assertValueEqual(ResourceLocation.of("forge/of_test", '/'), ResourceLocation.bySeparator("forge/of_test", '/'), "resource location by separator (compared to of)");
        assertValueEqual(mcLoc, ResourceLocation.tryBySeparator("minecraft/test", '/'), "resource location by separator");
        assertValueEqual(null, ResourceLocation.tryBySeparator("minecraft:?/awd&", '/'), "resource location by separator (invalid)");

        helper.succeed();
    }

    // I don't want to backport IForgeGameTestHelper stuff right now, so this will do - Jonathing
    private static void assertValueEqual(ResourceLocation expected, ResourceLocation actual, String name) {
        if (!Objects.equals(expected, actual)) {
            throw new GameTestAssertException("Expected " + name + " to be " + expected + ", but got " + actual);
        }
    }
}
