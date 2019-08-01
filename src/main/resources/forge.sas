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
	net/minecraft/block/AttachedStemBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/BambooSaplingBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/CropsBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/EndGatewayBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/EndPortalBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/FlowerPotBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/FrostedIceBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/KelpBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/MovingPistonBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/NetherPortalBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/NetherWartBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/PistonHeadBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/ShulkerBoxBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/SpawnerBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/StemBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/SweetBerryBushBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
	net/minecraft/block/TallSeaGrassBlock func_185473_a(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;
net/minecraft/block/SoundType func_185845_c()Lnet/minecraft/util/SoundEvent; # getBreakSound
net/minecraft/block/SoundType func_185846_f()Lnet/minecraft/util/SoundEvent; # getHitSound
net/minecraft/item/crafting/Ingredient func_193365_a()[Lnet/minecraft/item/ItemStack; # getMatchingStacks
net/minecraft/item/crafting/Ingredient func_193369_a([Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/crafting/Ingredient; # fromStacks
net/minecraft/item/crafting/IRecipe func_222128_h()Lnet/minecraft/item/ItemStack; # getIcon
	net/minecraft/item/crafting/BlastingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/CampfireCookingRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
	net/minecraft/item/crafting/FurnaceRecipe func_222128_h()Lnet/minecraft/item/ItemStack;
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
	net/minecraft/item/crafting/SuspiciousStewRecipe func_194133_a(II)Z
	net/minecraft/item/crafting/TippedArrowRecipe func_194133_a(II)Z
net/minecraft/item/crafting/IRecipe func_193358_e()Ljava/lang/String; # getGroup
	net/minecraft/item/crafting/AbstractCookingRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/ShapedRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/ShapelessRecipe func_193358_e()Ljava/lang/String;
	net/minecraft/item/crafting/SingleItemRecipe func_193358_e()Ljava/lang/String;
net/minecraft/nbt/CompressedStreamTools func_74797_a(Ljava/io/File;)Lnet/minecraft/nbt/CompoundNBT; # read
net/minecraft/nbt/CompressedStreamTools func_74795_b(Lnet/minecraft/nbt/CompoundNBT;Ljava/io/File;)V # write
net/minecraft/nbt/CompressedStreamTools func_74793_a(Lnet/minecraft/nbt/CompoundNBT;Ljava/io/File;)V # safeWrite
net/minecraft/potion/Effect func_220303_e()Lnet/minecraft/potion/EffectType; # getEffectType
net/minecraft/potion/Effect func_111186_k()Ljava/util/Map; # getAttributeModifierMap
net/minecraft/potion/Effect func_188408_i()Z # isBeneficial
net/minecraft/potion/EffectType func_220306_a()Lnet/minecraft/util/text/TextFormatting; # getColor
net/minecraft/resources/IResourceManager func_199001_a()Ljava/util/Set; # getResourceNamespaces
	net/minecraft/resources/FallbackResourceManager func_199001_a()Ljava/util/Set;
	net/minecraft/resources/SimpleReloadableResourceManager func_199001_a()Ljava/util/Set;
net/minecraft/resources/IResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z # hasResource
	net/minecraft/resources/FallbackResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z
	net/minecraft/resources/SimpleReloadableResourceManager func_219533_b(Lnet/minecraft/util/ResourceLocation;)Z
net/minecraft/resources/IResourceManager func_199021_a(Lnet/minecraft/resources/IResourcePack;)V # addResourcePack
	net/minecraft/resources/FallbackResourceManager func_199021_a(Lnet/minecraft/resources/IResourcePack;)V
	net/minecraft/resources/SimpleReloadableResourceManager func_199021_a(Lnet/minecraft/resources/IResourcePack;)V
net/minecraft/resources/IReloadableResourceManager func_219535_a(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resources/IAsyncReloader; # initialReload
	net/minecraft/resources/SimpleReloadableResourceManager func_219535_a(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resources/IAsyncReloader;
net/minecraft/tags/TagCollection func_199913_a(Ljava/lang/Object;)Ljava/util/Collection; # getOwningTags
net/minecraft/tileentity/TileEntity func_145835_a(DDD)D # getDistanceSq
net/minecraft/util/Direction func_176732_a(Lnet/minecraft/util/Direction$Axis;)Lnet/minecraft/util/Direction; # rotateAround
net/minecraft/util/Direction func_176744_n()Lnet/minecraft/util/Direction; # rotateX
net/minecraft/util/Direction func_176738_p()Lnet/minecraft/util/Direction; # rotateZ
net/minecraft/util/Direction func_176739_a(Ljava/lang/String;)Lnet/minecraft/util/Direction; # byName
net/minecraft/util/Direction$Axis func_176717_a(Ljava/lang/String;)Lnet/minecraft/util/Direction$Axis; # byName
net/minecraft/util/math/Vec3d func_216371_e()Lnet/minecraft/util/math/Vec3d;
net/minecraft/util/math/Vec3d func_189984_a(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/util/math/Vec3d; # fromPitchYaw
net/minecraft/util/math/Vec3d func_189986_a(FF)Lnet/minecraft/util/math/Vec3d; # fromPitchYaw
net/minecraft/world/World func_72971_b(F)F # getSunBrightness
net/minecraft/world/World func_72919_O()D # getHorizon