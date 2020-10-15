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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CustomTagTypesTest.MODID)
public class CustomTagTypesTest
{
    public static final String MODID = "custom_tag_types_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final ResourceLocation customRegistryName = new ResourceLocation(MODID, "custom_type_registry");
    private static final DeferredRegister<Custom> CUSTOMS = DeferredRegister.create(Custom.class, MODID);
    private static final RegistryObject<Custom> CUSTOM = CUSTOMS.register("custom", Custom::new);
    private static final Supplier<IForgeRegistry<Custom>> CUSTOM_REG = CUSTOMS.makeRegistry(customRegistryName.getPath(),
          () -> new RegistryBuilder<Custom>().tagFolder(MODID + "/custom_types"));
    private static final ITag.INamedTag<Custom> TESTS = ForgeTagHandler.createOptionalTag(customRegistryName, new ResourceLocation(MODID, "tests"), Sets.newHashSet(CUSTOM));
    private static final ITag.INamedTag<Item> OPTIONAL_TEST = ItemTags.createOptional(new ResourceLocation(MODID, "optional_test"), Sets.newHashSet(() -> Items.BONE));
    private static final ITag.INamedTag<Enchantment> FIRE = ForgeTagHandler.createOptionalTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation(MODID, "fire"));
    private static final ITag.INamedTag<Potion> DAMAGE = ForgeTagHandler.createOptionalTag(ForgeRegistries.POTION_TYPES, new ResourceLocation(MODID, "damage"));
    private static final ITag.INamedTag<TileEntityType<?>> STORAGE = ForgeTagHandler.createOptionalTag(ForgeRegistries.TILE_ENTITIES, new ResourceLocation(MODID, "storage"));

    public CustomTagTypesTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CUSTOMS.register(modBus);
        modBus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.addListener(this::leftClick);
    }

    private void gatherData(GatherDataEvent event)
    {
        if (event.includeServer())
        {
            DataGenerator gen = event.getGenerator();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            gen.addProvider(new CustomRegistryTags(gen, existingFileHelper));
            gen.addProvider(new EnchantmentTags(gen, existingFileHelper));
            gen.addProvider(new PotionTags(gen, existingFileHelper));
            gen.addProvider(new TileEntityTypeTags(gen, existingFileHelper));
        }
    }

    private void leftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.isEmpty())
        {
            LOGGER.info("{} {} {}", Items.BONE.getTags(), OPTIONAL_TEST.func_230236_b_().size(), TagCollectionManager.func_242178_a().func_241836_b().get(new ResourceLocation(MODID, "optional_test")));
            EnchantmentHelper.getEnchantments(itemStack).forEach((enchantment, level) -> logTagsIfPresent(enchantment.getTags()));
            if (itemStack.getItem() instanceof PotionItem) logTagsIfPresent(PotionUtils.getPotionFromItem(itemStack).getTags());
            TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
            if (tileEntity != null) logTagsIfPresent(tileEntity.getType().getTags());
        }
    }

    private void logTagsIfPresent(Set<ResourceLocation> tags)
    {
        if (!tags.isEmpty())
        {
            LOGGER.info("Tags: {}", tags);
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
        public String getName()
        {
            return "Custom Registry Tags";
        }
    }

    public static class EnchantmentTags extends ForgeRegistryTagsProvider<Enchantment>
    {
        public EnchantmentTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.ENCHANTMENTS, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            func_240522_a_(FIRE).func_240534_a_(Enchantments.FIRE_ASPECT, Enchantments.FLAME);
        }

        @Override
        public String getName()
        {
            return "Enchantment Tags";
        }
    }

    public static class PotionTags extends ForgeRegistryTagsProvider<Potion>
    {
        public PotionTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.POTION_TYPES, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            func_240522_a_(DAMAGE).func_240534_a_(Potions.HARMING, Potions.STRONG_HARMING);
        }

        @Override
        public String getName()
        {
            return "Potion Tags";
        }
    }

    public static class TileEntityTypeTags extends ForgeRegistryTagsProvider<TileEntityType<?>>
    {
        public TileEntityTypeTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.TILE_ENTITIES, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            func_240522_a_(STORAGE).func_240534_a_(TileEntityType.BARREL, TileEntityType.CHEST, TileEntityType.ENDER_CHEST);
        }

        @Override
        public String getName()
        {
            return "Tile Entity Type Tags";
        }
    }
}
