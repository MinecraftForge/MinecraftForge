package net.minecraftforge.resource;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraftforge.common.loot.LootModifierManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface IForgeDataPackRegistries {
   List<IFutureReloadListener> resources = new ArrayList<>();

   default Stream<IFutureReloadListener> getResourcesToReload()
   {
      return resources.stream();
   }

   static void addResource(IFutureReloadListener resource)
   {
      resources.add(resource);
   }
}
