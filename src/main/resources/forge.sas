# Forge Sided Annotation Stripper Config
# Please keep this file organized. And use the forge:checkSAS task to generate/validate inheretance.
# So only add the root function that needs to have the annotation stripped.
# checkSAS will populate all overrides as nessasary. Prefixing added lines with \t
# checkSAS also supports names using . so simplest way to find the correct line to add is to get the AT line from the bot 
#   and remove the access modifier.
#==================================================================================================================================
# Block.getItem called from Block.getPickBlock.
net/minecraft/block/Block func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/AbstractBannerBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/AbstractBodyPlantBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/AttachedStemBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/BambooSaplingBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/CropsBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/EndGatewayBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/EndPortalBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/FlowerPotBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/FrostedIceBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/MovingPistonBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/NetherPortalBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/NetherWartBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/PistonHeadBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/ShulkerBoxBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/SpawnerBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/StemBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/SweetBerryBushBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/TallSeaGrassBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
# Vanilla blocks calling these sided methods in getItem
net/minecraft/tileentity/BannerTileEntity func_190615_l(Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
net/minecraft/block/BannerBlock func_196287_a(Lnet/minecraft/item/DyeColor;)Lnet/minecraft/block/Block;
net/minecraft/block/AttachedStemBlock func_196279_O_()Lnet/minecraft/item/Item;
net/minecraft/block/StemBlock func_176481_j()Lnet/minecraft/item/Item;
#=====================================
net/minecraft/block/CropsBlock func_199772_f()Lnet/minecraft/util/IItemProvider; # getSeedsItem
	net/minecraft/block/BeetrootBlock func_199772_f()Lnet/minecraft/util/IItemProvider;
	net/minecraft/block/CarrotBlock func_199772_f()Lnet/minecraft/util/IItemProvider;
	net/minecraft/block/PotatoBlock func_199772_f()Lnet/minecraft/util/IItemProvider;
net/minecraft/block/SoundType func_185845_c()Lnet/minecraft/util/SoundEvent; # getBreakSound
net/minecraft/block/SoundType func_185846_f()Lnet/minecraft/util/SoundEvent; # getHitSound
net/minecraft/block/WoodType func_227046_a_()Ljava/util/stream/Stream; # getValues
net/minecraft/block/WoodType func_227048_b_()Ljava/lang/String; # getName
net/minecraft/item/DyeColor func_218388_g()I # getTextColor
net/minecraft/item/DyeColor func_196058_b(I)Lnet/minecraft/item/DyeColor; # byFireworkColor
net/minecraft/item/crafting/Ingredient func_193365_a()[Lnet/minecraft/item/ItemStack; # getMatchingStacks
net/minecraft/item/crafting/Ingredient func_193369_a([Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/crafting/Ingredient; # fromStacks
net/minecraft/item/crafting/IRecipe func_222128_h()Lnet/minecraft/item/ItemStack; # getIcon
	net/minecraft/item/crafting/BlastingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/CampfireCookingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/FurnaceRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/SmithingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/SmokingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/StonecuttingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
net/minecraft/item/crafting/IRecipe func_194133_a(II)Z # canFit
	net/minecraft/item/crafting/AbstractCookingRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/ArmorDyeRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/BannerDuplicateRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/BookCloningRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/FireworkRocketRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/FireworkStarFadeRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/FireworkStarRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/MapCloningRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/RepairItemRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/ShapedRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/ShapelessRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/ShieldRecipes func_194133_a(II)Z
	net/minecraft/item/crafting/ShulkerBoxColoringRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/SingleItemRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/SmithingRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/SuspiciousStewRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/TippedArrowRecipe func_194133_a(II)Z
net/minecraft/item/crafting/IRecipe func_193358_e()Ljava/lang/String; # getGroup
	net/minecraft/item/crafting/AbstractCookingRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/ShapedRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/ShapelessRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/SingleItemRecipe func_193358_e()Ljava/lang/String;
net/minecraft/nbt/CompressedStreamTools func_74797_a(Ljava/io/File;)Lnet/minecraft/nbt/CompoundNBT; # read
net/minecraft/nbt/CompressedStreamTools func_74795_b(Lnet/minecraft/nbt/CompoundNBT;Ljava/io/File;)V # write
net/minecraft/network/play/server/SCommandListPacket func_197693_a()Lcom/mojang/brigadier/tree/RootCommandNode; # getRoot
net/minecraft/network/play/server/SEntityPropertiesPacket func_149441_d()Ljava/util/List; # getSnapshots
net/minecraft/network/play/server/SEntityPropertiesPacket func_149442_c()I # getEntityId
net/minecraft/potion/Effect func_220303_e()Lnet/minecraft/potion/EffectType; # getEffectType
net/minecraft/potion/Effect func_111186_k()Ljava/util/Map; # getAttributeModifierMap
net/minecraft/potion/Effect func_188408_i()Z # isBeneficial
net/minecraft/potion/EffectType func_220306_a()Lnet/minecraft/util/text/TextFormatting; # getColor
net/minecraft/resources/IResourceManager func_199001_a()Ljava/util/Set; # getResourceNamespaces
	net/minecraft/resources/FallbackResourceManager func_199001_a()Ljava/util/Set;
	net/minecraft/resources/IResourceManager$Instance func_199001_a()Ljava/util/Set;
	net/minecraft/resources/SimpleReloadableResourceManager func_199001_a()Ljava/util/Set;
net/minecraft/resources/IResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z # hasResource
	net/minecraft/resources/FallbackResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z
	net/minecraft/resources/IResourceManager$Instance func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z
	net/minecraft/resources/SimpleReloadableResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z
net/minecraft/tags/ITagCollection func_199913_a(Ljava/lang/Object;)Ljava/util/Collection; # getOwningTags
net/minecraft/util/Direction func_176739_a(Ljava/lang/String;)Lnet/minecraft/util/Direction; # byName
net/minecraft/util/Direction$Axis func_176717_a(Ljava/lang/String;)Lnet/minecraft/util/Direction$Axis; # byName
net/minecraft/util/math/vector/Vector3d func_216371_e()Lnet/minecraft/util/math/vector/Vector3d;
net/minecraft/util/math/vector/Vector3d func_189984_a(Lnet/minecraft/util/math/vector/Vector2f;)Lnet/minecraft/util/math/vector/Vector3d; # fromPitchYaw
net/minecraft/util/math/vector/Vector3d func_189986_a(FF)Lnet/minecraft/util/math/vector/Vector3d; # fromPitchYaw
net/minecraft/util/math/vector/Vector4f # Vector 4f Class
net/minecraft/util/text/Style func_240719_a_(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/text/Style; #setFontId
net/minecraft/util/text/Style func_240723_c_(Lnet/minecraft/util/text/TextFormatting;)Lnet/minecraft/util/text/Style; #forceFormatting
# BiomeAmbiance getters, needed for it to be useful during BiomeLoadingEvent to be useful
net/minecraft/world/biome/BiomeAmbience func_235213_a_()I # getFogColor
net/minecraft/world/biome/BiomeAmbience func_235216_b_()I # getWaterColor
net/minecraft/world/biome/BiomeAmbience func_235218_c_()I # getWaterFogColor
net/minecraft/world/biome/BiomeAmbience func_242527_d()I # getSkyColor
net/minecraft/world/biome/BiomeAmbience func_242528_e()Ljava/util/Optional; # getFoliageColor
net/minecraft/world/biome/BiomeAmbience func_242529_f()Ljava/util/Optional; # getGrassColor
net/minecraft/world/biome/BiomeAmbience func_242531_g()Lnet/minecraft/world/biome/BiomeAmbience$GrassColorModifier; # getGrassColorModifier
net/minecraft/world/biome/BiomeAmbience func_235220_d_()Ljava/util/Optional; # getParticle
net/minecraft/world/biome/BiomeAmbience func_235222_e_()Ljava/util/Optional; # getAmbientSound
net/minecraft/world/biome/BiomeAmbience func_235224_f_()Ljava/util/Optional; # getMoodSound
net/minecraft/world/biome/BiomeAmbience func_235226_g_()Ljava/util/Optional; # getAdditionsSound
net/minecraft/world/biome/BiomeAmbience func_235228_h_()Ljava/util/Optional; # getMusic
net/minecraft/client/renderer/model/ModelResourceLocation # ModelResourceLocation class