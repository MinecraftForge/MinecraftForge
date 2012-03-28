package fml.stubs.obf;

public interface ModLoader {
  static void addAchievementDesc(aeb achievement, java.lang.String name, java.lang.String description);

  static int addAllFuel(int id, int metadata);

  static void addAllRenderers(java.util.Map<java.lang.Class<? extends nn>, um> renderers);

  static void addAnimation(tt anim);

  static int addArmor(java.lang.String armor);

  static void addBiome(abn biome);

  static void addLocalization(java.lang.String key, java.lang.String value);

  static void addLocalization(java.lang.String key, java.lang.String lang, java.lang.String value);

  static void addName(java.lang.Object instance, java.lang.String name);

  static void addName(java.lang.Object instance, java.lang.String lang, java.lang.String name);

  static int addOverride(java.lang.String fileToOverride, java.lang.String fileToAdd);

  static void addOverride(java.lang.String path, java.lang.String overlayPath, int index);

  static void addRecipe(aan output, java.lang.Object... params);

  static void addShapelessRecipe(aan output, java.lang.Object... params);

  static void addSmelting(int input, aan output);

  static void addSpawn(java.lang.Class<? extends acq> entityClass, int weightedProb, int min, int max, acf spawnList);

  static void addSpawn(java.lang.Class<? extends acq> entityClass, int weightedProb, int min, int max, acf spawnList, abn... biomes);

  static void addSpawn(java.lang.String entityName, int weightedProb, int min, int max, acf spawnList);

  static void addSpawn(java.lang.String entityName, int weightedProb, int min, int max, acf spawnList, abn... biomes);

  static boolean dispenseEntity(xd world, double x, double y, double z, int xVel, int zVel, aan item);

  static void genericContainerRemoval(xd world, int x, int y, int z);

  static java.util.List<BaseMod> getLoadedMods();

  static java.util.logging.Logger getLogger();

  static net.minecraft.client.Minecraft getMinecraftInstance();

  static <T, E> T getPrivateValue(java.lang.Class<? super E> instanceclass, E instance, int fieldindex);

  static <T, E> T getPrivateValue(java.lang.Class<? super E> instanceclass, E instance, java.lang.String field);

  static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem);

  static int getUniqueEntityId();

  static int getUniqueSpriteIndex(java.lang.String path);

  static boolean isGUIOpen(java.lang.Class<? extends vp> gui);

  static boolean isModLoaded(java.lang.String modname);

  static void loadConfig();

  static java.awt.image.BufferedImage loadImage(aaw texCache, java.lang.String path);

  static void onItemPickup(yw player, aan item);

  static void onTick(float tick, net.minecraft.client.Minecraft game);

  static void openGUI(yw player, vp gui);

  static void populateChunk(ca generator, int chunkX, int chunkZ, xd world);

  static void receivePacket(ee packet);

  static afu[] registerAllKeys(afu[] keys);

  static void registerAllTextureOverrides(aaw cache);

  static void registerBlock(pb block);

  static void registerBlock(pb block, java.lang.Class<? extends vd> itemclass);

  static void registerEntityID(java.lang.Class<? extends nn> entityClass, java.lang.String entityName, int id);

  static void registerEntityID(java.lang.Class<? extends nn> entityClass, java.lang.String entityName, int id, int background, int foreground);

  static void registerKey(BaseMod mod, afu keyHandler, boolean allowRepeat);

  static void registerPacketChannel(BaseMod mod, java.lang.String channel);

  static void registerTileEntity(java.lang.Class<? extends kw> tileEntityClass, java.lang.String id);

  static void registerTileEntity(java.lang.Class<? extends kw> tileEntityClass, java.lang.String id, aar renderer);

  static void removeBiome(abn biome);

  static void removeSpawn(java.lang.Class<? extends acq> entityClass, acf spawnList);

  static void removeSpawn(java.lang.Class<? extends acq> entityClass, acf spawnList, abn... biomes);

  static void removeSpawn(java.lang.String entityName, acf spawnList);

  static void removeSpawn(java.lang.String entityName, acf spawnList, abn... biomes);

  static boolean renderBlockIsItemFull3D(int modelID);

  static void renderInvBlock(vl renderer, pb block, int metadata, int modelID);

  static boolean renderWorldBlock(vl renderer, ali world, int x, int y, int z, pb block, int modelID);

  static void saveConfig();

  static void serverChat(java.lang.String text);

  static void serverLogin(adl handler, aec loginPacket);

  static void setInGameHook(BaseMod mod, boolean enable, boolean useClock);

  static void setInGUIHook(BaseMod mod, boolean enable, boolean useClock);

  static <T, E> void setPrivateValue(java.lang.Class<? super T> instanceclass, T instance, int fieldindex, E value);

  static <T, E> void setPrivateValue(java.lang.Class<? super T> instanceclass, T instance, java.lang.String field, E value);

  static void takenFromCrafting(yw player, aan item, io matrix);

  static void takenFromFurnace(yw player, aan item);

  static void throwException(java.lang.String message, java.lang.Throwable e);
}
