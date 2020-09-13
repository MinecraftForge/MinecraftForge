/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.misc;

import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(CustomTagTypesTest.MODID)
public class CustomTagTypesTest
{
    public static final String MODID = "custom_tag_types_test";
    private static final ResourceLocation customRegistryName = new ResourceLocation(MODID, "custom_type_registry");
    private static final DeferredRegister<Custom> CUSTOMS = DeferredRegister.create(Custom.class, MODID);
    private static final RegistryObject<Custom> CUSTOM = CUSTOMS.register("custom", Custom::new);
    private static final Supplier<IForgeRegistry<Custom>> CUSTOM_REG = CUSTOMS.makeRegistry(customRegistryName.getPath(),
          () -> new RegistryBuilder<Custom>().tagFolder(MODID + "/custom_types"));
    private static final ITag.INamedTag<Biome> OCEANS = ForgeTagHandler.makeWrapperTag(ForgeRegistries.BIOMES, new ResourceLocation(MODID, "oceans"));
    private static final ITag.INamedTag<Custom> TESTS = ForgeTagHandler.makeWrapperTag(customRegistryName, new ResourceLocation(MODID, "tests"));

    public CustomTagTypesTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CUSTOMS.register(modBus);
        modBus.addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent event)
    {
        if (event.includeServer())
        {
            DataGenerator gen = event.getGenerator();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            gen.addProvider(new BiomeTags(gen, existingFileHelper));
            gen.addProvider(new CustomRegistryTags(gen, existingFileHelper));
        }
    }

    public static class Custom extends ForgeRegistryEntry<Custom>
    {
        private final ReverseTagWrapper<Custom> reverseTags = new ReverseTagWrapper<>(this, () -> TagCollectionManager.func_242178_a().getCustomTypeCollection(CUSTOM_REG.get()));

        public Set<ResourceLocation> getTags()
        {
            return reverseTags.getTagNames();
        }

        public boolean isIn(ITag<Custom> tag)
        {
            return tag.func_230235_a_(this);
        }
    }

    public static class BiomeTags extends ForgeRegistryTagsProvider<Biome>
    {
        public BiomeTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.BIOMES, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            func_240522_a_(OCEANS).add(Biomes.OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN,
                  Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        }

        @Override
        public String getName() {
            return "Biome Tags";
        }
    }

    public static class CustomRegistryTags extends ForgeRegistryTagsProvider<Custom>
    {
        public CustomRegistryTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, CUSTOM_REG.get(), MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            func_240522_a_(TESTS).func_240532_a_(CUSTOM.get());
        }

        @Override
        public String getName() {
            return "Custom Registry Tags";
        }
    }
}
