package net.minecraftforge.resource;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class DataTagCollectionManager {
   private final TagCollection<Block> blocks = new TagCollection<>(resloc -> Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(resloc)), "tags/blocks", "block");
   private final TagCollection<Item> items = new TagCollection<>(resloc -> Optional.ofNullable(ForgeRegistries.ITEMS.getValue(resloc)), "tags/items", "item");
   private final TagCollection<Fluid> fluids = new TagCollection<>(resloc -> Optional.ofNullable(ForgeRegistries.FLUIDS.getValue(resloc)), "tags/fluids", "fluid");
   private final TagCollection<EntityType<?>> entityTypes = new TagCollection<>(resloc -> Optional.ofNullable(ForgeRegistries.ENTITIES.getValue(resloc)), "tags/entity_types", "entity_type");

   public void loadTags(IResourceManager resourceManager, Executor game, Executor background) {
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> cfBlocks = this.blocks.reload(resourceManager, background);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> cfItems = this.items.reload(resourceManager, background);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> cfFluids = this.fluids.reload(resourceManager, background);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> cfEntities = this.entityTypes.reload(resourceManager, background);
      CompletableFuture.allOf(cfBlocks, cfItems, cfFluids, cfEntities).thenAcceptAsync(v -> {
         this.blocks.registerAll(cfBlocks.join());
         this.items.registerAll(cfItems.join());
         this.fluids.registerAll(cfFluids.join());
         this.entityTypes.registerAll(cfEntities.join());
         TagCollectionManager.func_232924_a_(this.blocks, this.items, this.fluids, this.entityTypes);
         Multimap<String, ResourceLocation> multimap = HashMultimap.create();
         multimap.putAll("blocks", BlockTags.func_232892_b_(this.blocks));
         multimap.putAll("items", ItemTags.func_232917_b_(this.items));
         multimap.putAll("fluids", FluidTags.func_232901_b_(this.fluids));
         multimap.putAll("entity_types", EntityTypeTags.func_232897_b_(this.entityTypes));
         if (!multimap.isEmpty()) {
            throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map((entry) -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
         }
         setupTags();
      }, game);
   }

   public void setupTags() {
      BlockTags.setCollection(this.blocks);
      ItemTags.setCollection(this.items);
      FluidTags.setCollection(this.fluids);
      EntityTypeTags.setCollection(this.entityTypes);
   }
}
