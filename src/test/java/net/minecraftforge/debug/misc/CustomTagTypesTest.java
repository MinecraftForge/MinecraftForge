/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
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
    private static final ResourceKey<? extends Registry<Custom>> CUSTOM_KEY = ResourceKey.createRegistryKey(customRegistryName);
    private static final Supplier<IForgeRegistry<Custom>> CUSTOM_REG = CUSTOMS.makeRegistry(customRegistryName.getPath(),
          () -> new RegistryBuilder<Custom>().tagFolder(MODID + "/custom_types"));
    private static final Tag.Named<Custom> TESTS = ForgeTagHandler.createOptionalTag(customRegistryName, new ResourceLocation(MODID, "tests"), Set.of(CUSTOM));
    private static final Tag.Named<Item> OPTIONAL_TEST = ItemTags.createOptional(new ResourceLocation(MODID, "optional_test"), Set.of(() -> Items.BONE));
    private static final Tag.Named<Enchantment> FIRE = ForgeTagHandler.createOptionalTag(ForgeRegistries.ENCHANTMENTS, new ResourceLocation(MODID, "fire"));
    private static final Tag.Named<Potion> DAMAGE = ForgeTagHandler.createOptionalTag(ForgeRegistries.POTIONS, new ResourceLocation(MODID, "damage"));
    private static final Tag.Named<BlockEntityType<?>> STORAGE = ForgeTagHandler.createOptionalTag(ForgeRegistries.BLOCK_ENTITIES, new ResourceLocation(MODID, "storage"));
    private static final Tag.Named<MobEffect> BEACON = ForgeTagHandler.createOptionalTag(ForgeRegistries.MOB_EFFECTS, new ResourceLocation(MODID, "beacon"));

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
            gen.addProvider(new BlockEntityTypeTags(gen, existingFileHelper));
            gen.addProvider(new MobEffectTypeTags(gen, existingFileHelper));
        }
    }

    private void leftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.isEmpty())
        {
            LOGGER.info("{} {} {}", Items.BONE.getTags(), OPTIONAL_TEST.getValues().size(), SerializationTags.getInstance().getOrEmpty(Registry.ITEM_REGISTRY).getTag(new ResourceLocation(MODID, "optional_test")));
            LOGGER.info("{} {}", CUSTOM.get().getTags(), TESTS.getValues().size());
            itemStack.getAllEnchantments().forEach((enchantment, level) -> logTagsIfPresent(enchantment.getTags()));
            if (itemStack.getItem() instanceof PotionItem) logTagsIfPresent(PotionUtils.getPotion(itemStack).getTags());
            BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());
            if (blockEntity != null) logTagsIfPresent(blockEntity.getType().getTags());
            event.getEntityLiving().getActiveEffects().forEach((mobEffectInstance) -> logTagsIfPresent(mobEffectInstance.getEffect().getTags()));
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
        private final ReverseTagWrapper<Custom> reverseTags = new ReverseTagWrapper<>(this, () -> SerializationTags.getInstance().getOrEmpty(CUSTOM_KEY));

        public Set<ResourceLocation> getTags()
        {
            return reverseTags.getTagNames();
        }
    }

    public static class CustomRegistryTags extends ForgeRegistryTagsProvider<Custom>
    {
        public CustomRegistryTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, CUSTOM_REG.get(), MODID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            tag(TESTS).add(CUSTOM.get());
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
        protected void addTags()
        {
            tag(FIRE).add(Enchantments.FIRE_ASPECT, Enchantments.FLAMING_ARROWS);
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
            super(gen, ForgeRegistries.POTIONS, MODID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            tag(DAMAGE).add(Potions.HARMING, Potions.STRONG_HARMING);
        }

        @Override
        public String getName()
        {
            return "Potion Tags";
        }
    }

    public static class BlockEntityTypeTags extends ForgeRegistryTagsProvider<BlockEntityType<?>>
    {
        public BlockEntityTypeTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.BLOCK_ENTITIES, MODID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            tag(STORAGE).add(BlockEntityType.BARREL, BlockEntityType.CHEST, BlockEntityType.ENDER_CHEST, BlockEntityType.TRAPPED_CHEST);
        }

        @Override
        public String getName()
        {
            return "Block Entity Type Tags";
        }
    }

    public static class MobEffectTypeTags extends ForgeRegistryTagsProvider<MobEffect>
    {
        public MobEffectTypeTags(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(gen, ForgeRegistries.MOB_EFFECTS, MODID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            tag(BEACON).add(MobEffects.MOVEMENT_SPEED, MobEffects.JUMP, MobEffects.DIG_SPEED, MobEffects.REGENERATION, MobEffects.DAMAGE_RESISTANCE, MobEffects.DAMAGE_BOOST);
        }

        @Override
        public String getName()
        {
            return "Mob Effect Tags";
        }
    }
}
