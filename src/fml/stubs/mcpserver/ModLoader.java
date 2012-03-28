package fml.stubs.mcpserver;

import java.util.List;
import java.util.logging.Logger;

import net.minecraft.src.Achievement;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ModLoader {
	static void addAchievementDesc(Achievement achievement, String name, String description) {
	}

	static int addAllFuel(int id, int metadata) {
		return 0;
	}

//	static void addAllRenderers(Map<Class<? extends Entity>, Render> renderers) {
//	}

//	static void addAnimation(TextureFX anim) {
//	}

	static int addArmor(String armor) {
		return 0;
	}

	static void addBiome(BiomeGenBase biome) {
	}

	static void addLocalization(String key, String value) {
	}

	static void addLocalization(String key, String lang, String value) {
	}

	static void addName(Object instance, String name) {
	}

	static void addName(Object instance, String lang, String name) {
	}

	static int addOverride(String fileToOverride, String fileToAdd) {
		return 0;
	}

	static void addOverride(String path, String overlayPath, int index) {
	}

	static void addRecipe(ItemStack output, Object... params) {
	}

	static void addShapelessRecipe(ItemStack output, Object... params) {
	}

	static void addSmelting(int input, ItemStack output) {
	}

	static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList) {
	}

	static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
	}

	static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList) {
	}

	static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
	}

	static boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
		return false;
	}

	static void genericContainerRemoval(World world, int x, int y, int z) {
	}

	static List<BaseMod> getLoadedMods() {
		return null;
	}

	static Logger getLogger() {
		return null;
	}

//	static Minecraft getMinecraftInstance() {
//		return null;
//	}

	static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex) {
		return null;
	}

	static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) {
		return null;
	}

	static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem) {
		return 0;
	}

	static int getUniqueEntityId() {
		return 0;
	}

	static int getUniqueSpriteIndex(String path) {
		return 0;
	}

//	static boolean isGUIOpen(Class<? extends GuiScreen> gui) {
//		return false;
//	}

	static boolean isModLoaded(String modname) {
		return false;
	}

	static void loadConfig() {
	}

//	static BufferedImage loadImage(RenderEngine texCache, String path) {
//		return null;
//	}

	static void onItemPickup(EntityPlayer player, ItemStack item) {
	}

//	static void onTick(float tick, net.minecraft.client.Minecraft game) {
//	}

//	static void openGUI(EntityPlayer player, GuiScreen gui) {
//	}

	static void populateChunk(IChunkProvider generator, int chunkX, int chunkZ, World world) {
	}

	static void receivePacket(Packet250CustomPayload packet) {
	}

//	static KeyBinding[] registerAllKeys(KeyBinding[] keys) {
//		return keys;
//	}

//	static void registerAllTextureOverrides(RenderEngine cache) {
//	}

	static void registerBlock(Block block) {
	}

	static void registerBlock(Block block, Class<? extends ItemBlock> itemclass) {
	}

	static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
	}

	static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int background, int foreground) {
	}

//	static void registerKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat) {
//	}

	static void registerPacketChannel(BaseMod mod, String channel) {
	}

	static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
	}

//	static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id, TileEntitySpecialRenderer renderer) {
//	}

	static void removeBiome(BiomeGenBase biome) {
	}

	static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList) {
	}

	static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, BiomeGenBase... biomes) {
	}

	static void removeSpawn(String entityName, EnumCreatureType spawnList) {
	}

	static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes) {
	}

	static boolean renderBlockIsItemFull3D(int modelID) {
		return false;
	}

//	static void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID) {
//	}

//	static boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
//		return false;
//	}

	static void saveConfig() {
	}

	static void serverChat(String text) {
	}

//	static void serverLogin(NetClientHandler handler, Packet1Login loginPacket) {
//	}

	static void setInGameHook(BaseMod mod, boolean enable, boolean useClock) {
	}

	static void setInGUIHook(BaseMod mod, boolean enable, boolean useClock) {
	}

	static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) {
	}

	static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
	}

	static void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix) {
	}

	static void takenFromFurnace(EntityPlayer player, ItemStack item) {
	}

	static void throwException(String message, Throwable e) {
	}
}
