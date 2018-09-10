/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;

@Mod(modid = "mapdatatest", name = "mapdatatest", version = "1.0", acceptableRemoteVersions = "*")
public class MapDataTest
{
    @GameRegistry.ObjectHolder("mapdatatest:custom_map")
    public static final Item CUSTOM_MAP = null;

    @GameRegistry.ObjectHolder("mapdatatest:empty_custom_map")
    public static final Item EMPTY_CUSTOM_MAP = null;

    private static SimpleNetworkWrapper packetHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        packetHandler = new SimpleNetworkWrapper("mapdatatest");
        packetHandler.registerMessage(CustomMapPacketHandler.class, CustomMapPacket.class, 0, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent evt)
    {
        ModelLoader.setCustomModelResourceLocation(EMPTY_CUSTOM_MAP, 0, new ModelResourceLocation("map", "inventory"));
        ModelLoader.setCustomMeshDefinition(CUSTOM_MAP, s -> new ModelResourceLocation("filled_map", "inventory"));
        ModelBakery.registerItemVariants(CUSTOM_MAP, new ModelResourceLocation("filled_map", "inventory"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> evt)
    {
        evt.getRegistry().register(new EmptyCustomMap().setUnlocalizedName("emptyCustomMap").setRegistryName("mapdatatest", "empty_custom_map"));
        evt.getRegistry().register(new CustomMap().setUnlocalizedName("customMap").setRegistryName("mapdatatest", "custom_map"));
    }

    public static class EmptyCustomMap extends ItemMapBase
    {
        // copy of super, setting up our own map
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            ItemStack itemstack = CustomMap.setupNewMap(worldIn, playerIn.posX, playerIn.posZ, (byte) 0, true, false);
            ItemStack itemstack1 = playerIn.getHeldItem(handIn);
            itemstack1.shrink(1);

            if (itemstack1.isEmpty())
            {
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            } else
            {
                if (!playerIn.inventory.addItemStackToInventory(itemstack.copy()))
                {
                    playerIn.dropItem(itemstack, false);
                }

                playerIn.addStat(StatList.getObjectUseStats(this));
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
            }
        }
    }

    public static class CustomMap extends ItemMap
    {
        private static final String PREFIX = "custommap";

        // copy of super with own map prefix
        public static ItemStack setupNewMap(World worldIn, double worldX, double worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking)
        {
            ItemStack itemstack = new ItemStack(CUSTOM_MAP, 1, worldIn.getUniqueDataId(PREFIX));
            String s = PREFIX + "_" + itemstack.getMetadata();
            MapData mapdata = new CustomMapData(s);
            worldIn.setData(s, mapdata);
            mapdata.scale = scale;
            mapdata.calculateMapCenter(worldX, worldZ, mapdata.scale);
            mapdata.dimension = worldIn.provider.getDimension();
            mapdata.trackingPosition = trackingPosition;
            mapdata.unlimitedTracking = unlimitedTracking;
            mapdata.markDirty();
            return itemstack;
        }

        // copy of super with own map prefix and type
        @Nullable
        @SideOnly(Side.CLIENT)
        public static CustomMapData loadMapData(int mapId, World worldIn)
        {
            String s = PREFIX + "_" + mapId;
            return (CustomMapData) worldIn.loadData(CustomMapData.class, s);
        }

        // copy of super with own map prefix and type
        @Nullable
        @Override
        public CustomMapData getMapData(ItemStack stack, World worldIn)
        {
            String s = PREFIX + "_" + stack.getMetadata();
            CustomMapData mapdata = (CustomMapData) worldIn.loadData(CustomMapData.class, s);

            if (mapdata == null && !worldIn.isRemote)
            {
                stack.setItemDamage(worldIn.getUniqueDataId(PREFIX));
                s = PREFIX + "_" + stack.getMetadata();
                mapdata = new CustomMapData(s);
                mapdata.scale = 3;
                mapdata.calculateMapCenter((double) worldIn.getWorldInfo().getSpawnX(), (double) worldIn.getWorldInfo().getSpawnZ(), mapdata.scale);
                mapdata.dimension = worldIn.provider.getDimension();
                mapdata.markDirty();
                worldIn.setData(s, mapdata);
            }

            return mapdata;
        }

        @Override
        public void updateMapData(World worldIn, Entity viewer, MapData data)
        {
            // Solid red
            Arrays.fill(data.colors, (byte) (MapColor.RED.colorIndex * 4 + 1));
        }

        // rewrap vanilla packet with own, to sync CustomMapData's extra data
        @Override
        public Packet<?> createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player)
        {
            SPacketMaps vanillaPacket = (SPacketMaps) super.createMapDataPacket(stack, worldIn, player);
            if (vanillaPacket == null)
            {
                return null;
            }

            return packetHandler.getPacketFrom(new CustomMapPacket(vanillaPacket));
        }
    }

    // A custom subclass to distinguish from vanilla's type in the WorldSavedData
    public static class CustomMapData extends MapData
    {
        public CustomMapData(String mapname)
        {
            super(mapname);
        }
    }

    // Custom map packet wrapping vanilla's because the handler needs to use our custom type
    public static class CustomMapPacket implements IMessage
    {
        public SPacketMaps vanillaPacket;

        public CustomMapPacket() {}

        public CustomMapPacket(SPacketMaps vanillaPacket)
        {
            this.vanillaPacket = vanillaPacket;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            vanillaPacket = new SPacketMaps();
            try {
                vanillaPacket.readPacketData(new PacketBuffer(buf));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            try {
                vanillaPacket.writePacketData(new PacketBuffer(buf));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class CustomMapPacketHandler implements IMessageHandler<CustomMapPacket, IMessage>
    {
        @Nullable
        @Override
        public IMessage onMessage(CustomMapPacket message, MessageContext ctx)
        {
            // Like NetHandlerPlayClient.handleMaps but using our custom type
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run()
                {
                    MapItemRenderer mapitemrenderer = Minecraft.getMinecraft().entityRenderer.getMapItemRenderer();
                    MapData mapdata = CustomMap.loadMapData(message.vanillaPacket.getMapId(), Minecraft.getMinecraft().world);

                    if (mapdata == null) {
                        String s = CustomMap.PREFIX + "_" + message.vanillaPacket.getMapId();
                        mapdata = new CustomMapData(s);

                        if (mapitemrenderer.getMapInstanceIfExists(s) != null) {
                            MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));

                            if (mapdata1 != null) {
                                mapdata = mapdata1;
                            }
                        }

                        Minecraft.getMinecraft().world.setData(s, mapdata);
                    }

                    message.vanillaPacket.setMapdataTo(mapdata);
                    mapitemrenderer.updateMapTexture(mapdata);
                }
            });

            return null;
        }
    }

}
