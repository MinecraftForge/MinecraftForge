/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.resources;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import java.util.Map;

/**
 * Handles Resource registrations. Resources MUST be registered in order to function.
 */
public class ResourceRegistry {

    static BiMap<String, Resource> resources = HashBiMap.create();
    static Map<Resource,ResourceRegistry.ResourceDelegate> delegates = Maps.newHashMap();

    public static final Resource IRON = new Resource("iron");
    public static final Resource GOLD = new Resource("gold");
    public static final Resource EMERALD = new Resource("emerald");
    public static final Resource DIAMOND = new Resource("diamond");

    public static void registerVanilla(){
        IRON.registerProduction("ingot", new ItemStack(Items.IRON_INGOT));
        IRON.registerProduction("block", new ItemStack(Blocks.IRON_BLOCK));
        IRON.registerProduction("ore", new ItemStack(Blocks.IRON_ORE));
        GOLD.registerProduction("ingot", new ItemStack(Items.GOLD_INGOT));
        GOLD.registerProduction("nugget", new ItemStack(Items.GOLD_NUGGET));
        GOLD.registerProduction("block", new ItemStack(Blocks.GOLD_BLOCK));
        GOLD.registerProduction("ore", new ItemStack(Blocks.GOLD_ORE));
        EMERALD.registerProduction("gem", new ItemStack(Items.EMERALD));
        EMERALD.registerProduction("block", new ItemStack(Blocks.EMERALD_BLOCK));
        EMERALD.registerProduction("ore", new ItemStack(Blocks.EMERALD_ORE));
        DIAMOND.registerProduction("gem", new ItemStack(Items.DIAMOND));
        DIAMOND.registerProduction("block", new ItemStack(Blocks.DIAMOND_BLOCK));
        DIAMOND.registerProduction("ore", new ItemStack(Blocks.DIAMOND_ORE));
    }

    /**
     * Register a new Resource.
     *
     * @param resource
     *            The resource to register.
     * @return True if the resource was registered
     */
    public static boolean registerResource(Resource resource)
    {
        if (resources.containsKey(resource.getName()))
        {
            return false;
        }
        resources.put(resource.getName(), resource);

        delegates.put(resource, new ResourceRegistry.ResourceDelegate(resource, resource.getName()));
        MinecraftForge.EVENT_BUS.post(new ResourceRegistry.ResourceRegisterEvent(resource.getName()));
        return true;
    }

    /**
     * Does the supplied resource have an entry for it's name
     * @param resource the resource we're testing
     * @return if the resource's name has a registration entry
     */
    public static boolean isFluidRegistered(Resource resource)
    {
        return resource != null && resources.containsKey(resource.getName());
    }

    public static boolean isFluidRegistered(String resourceName) { return resources.containsKey(resourceName); }

    public static Resource getResource(String resourceName) { return resources.get(resourceName); }

    public static String getResourceName(Resource resource)
    {
        return resources.inverse().get(resource);
    }

    /**
     * Returns a read-only map containing Resource Names and their associated Resources.
     */
    public static Map<String, Resource> getRegisteredResources()
    {
        return ImmutableMap.copyOf(resources);
    }

    public static class ResourceRegisterEvent extends Event
    {
        private final String resourceName;

        public ResourceRegisterEvent(String resourceName)
        {
            this.resourceName = resourceName;
        }

        public String getResourceName()
        {
            return resourceName;
        }
    }

    static RegistryDelegate<Resource> makeDelegate(Resource resource)
    {
        return delegates.get(resource);
    }

    private static class ResourceDelegate implements RegistryDelegate<Resource>
    {
        private String name;
        private Resource resource;

        ResourceDelegate(Resource resource, String name)
        {
            this.resource = resource;
            this.name = name;
        }

        @Override
        public Resource get()
        {
            return resource;
        }

        @Override
        public ResourceLocation name() {
            return new ResourceLocation(name);
        }

        @Override
        public Class<Resource> type()
        {
            return Resource.class;
        }

        void rebind()
        {
            resource = resources.get(name);
        }
    }

}
