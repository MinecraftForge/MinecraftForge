/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.recipe.recipebook;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(RecipeBookExtensionTest.MOD_ID)
public class RecipeBookExtensionTest
{
    public static final boolean ENABLED = true;

    public static final String MOD_ID = "recipe_book_extension_test";
    public static final RecipeBookType TEST_TYPE = RecipeBookType.create("TESTING");

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    public static final RegistryObject<RecipeSerializer<RecipeBookTestRecipe>> RECIPE_BOOK_TEST_RECIPE_SERIALIZER =
            RECIPE_SERIALIZER.register("test_recipe", RecipeBookTestRecipeSerializer::new);

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
    public static final RegistryObject<MenuType<RecipeBookTestMenu>> RECIPE_BOOK_TEST_MENU_TYPE =
            MENU_TYPE.register("test_recipe_menu", () -> IForgeMenuType.create(RecipeBookTestMenu::new));

    public RecipeBookExtensionTest()
    {
        if (!ENABLED)
            return;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        RECIPE_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MENU_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            RecipeType.register(getId("test_recipe").toString());
        });
    }

    private void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getWorld().isClientSide)
            return;
        if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.GRASS_BLOCK)
            NetworkHooks.openGui((ServerPlayer) event.getPlayer(), new SimpleMenuProvider((id, inv, p) -> new RecipeBookTestMenu(id, inv, ContainerLevelAccess.create(event.getWorld(), event.getPos())), new TextComponent("Test")));
    }

    public static ResourceLocation getId(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientHandler
    {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            if (!ENABLED)
                return;
            event.enqueueWork(() ->
            {
                MenuScreens.register(RECIPE_BOOK_TEST_MENU_TYPE.get(), RecipeBookTestScreen::new);
                RecipeBookExtensionClientHelper.init();
            });
        }
    }

    public static class RecipeBookTestContainer extends SimpleContainer
    {
        public RecipeBookTestContainer()
        {
            super(8);
        }
    }
}
