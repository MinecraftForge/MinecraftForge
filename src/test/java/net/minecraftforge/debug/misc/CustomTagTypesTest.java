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

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
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
    private static final IOptionalNamedTag<Custom> TESTS = ForgeTagHandler.createOptionalTag(customRegistryName, new ResourceLocation(MODID, "tests"), () -> Sets.newHashSet(CUSTOM.get()));
    private static final IOptionalNamedTag<Item> OPTIONAL_TEST = ItemTags.createOptional(new ResourceLocation(MODID, "optional_test"), () -> Sets.newHashSet(Items.BONE));

    public CustomTagTypesTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CUSTOMS.register(modBus);
        modBus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.addListener(this::projectileImpact);
    }

    private void gatherData(GatherDataEvent event)
    {
        if (event.includeServer())
        {
            DataGenerator gen = event.getGenerator();
            gen.addProvider(new CustomRegistryTags(gen, event.getExistingFileHelper()));
        }
    }

    private void projectileImpact(ProjectileImpactEvent.Arrow event)
    {
        //TODO: Either remove this or switch to logger
        System.out.println(event.getArrow().getEntityWorld().getBiome(event.getArrow().func_233580_cy_()).getTags());
        System.out.println(Items.BONE.getTags());
        System.out.println(OPTIONAL_TEST.func_230236_b_().size());
        //TODO: Should we somehow note that at least server side the below is not able to find optional tags
        System.out.println(TagCollectionManager.func_242178_a().func_241836_b().get(new ResourceLocation(MODID, "optional_test")));
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
