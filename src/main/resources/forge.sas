# Forge Sided Annotation Stripper Config
# Please keep this file organized. And use the forge:checkSAS task to generate/validate inheretance.
# So only add the root function that needs to have the annotation stripped.
# checkSAS will populate all overrides as nessasary. Prefixing added lines with \t
# checkSAS also supports names using . so simplest way to find the correct line to add is to get the AT line from the bot 
#   and remove the access modifier.
#==================================================================================================================================
# Block.getItem called from Block.getPickBlock.
net/minecraft/world/level/block/Block m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/AbstractBannerBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/AttachedStemBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/BambooSaplingBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/BigDripleafStemBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/CandleCakeBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/CaveVinesBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/CaveVinesPlantBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/CropBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/EndGatewayBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/EndPortalBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/FlowerPotBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/FrostedIceBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/GrowingPlantBodyBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/LightBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/NetherPortalBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/NetherWartBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/ShulkerBoxBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/SpawnerBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/StemBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/SweetBerryBushBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/TallSeagrassBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/piston/MovingPistonBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/level/block/piston/PistonHeadBlock m_7397_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;
# Vanilla blocks calling these sided methods in getItem
net/minecraft/world/level/block/BannerBlock m_49014_(Lnet/minecraft/world/item/DyeColor;)Lnet/minecraft/world/level/block/Block;
#=====================================
net/minecraft/world/level/block/CropBlock m_6404_()Lnet/minecraft/world/level/ItemLike; # getSeedsItem
	net/minecraft/world/level/block/BeetrootBlock m_6404_()Lnet/minecraft/world/level/ItemLike;
	net/minecraft/world/level/block/CarrotBlock m_6404_()Lnet/minecraft/world/level/ItemLike;
	net/minecraft/world/level/block/PotatoBlock m_6404_()Lnet/minecraft/world/level/ItemLike;
net/minecraft/world/level/block/SoundType m_56775_()Lnet/minecraft/sounds/SoundEvent; # getBreakSound
net/minecraft/world/level/block/SoundType m_56778_()Lnet/minecraft/sounds/SoundEvent; # getHitSound
net/minecraft/world/level/block/state/properties/WoodType m_61843_()Ljava/util/stream/Stream; # getValues
net/minecraft/world/level/block/state/properties/WoodType m_61846_()Ljava/lang/String; # getName
net/minecraft/world/item/DyeColor m_41071_()I # getTextColor
net/minecraft/world/item/DyeColor m_41061_(I)Lnet/minecraft/world/item/DyeColor; # byFireworkColor
net/minecraft/world/item/crafting/Ingredient m_43908_()[Lnet/minecraft/world/item/ItemStack; # getMatchingStacks
net/minecraft/world/item/crafting/Ingredient m_43927_([Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/crafting/Ingredient; # fromStacks
net/minecraft/world/item/crafting/Recipe m_8042_()Lnet/minecraft/world/item/ItemStack; # getIcon
	net/minecraft/world/item/crafting/BlastingRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/item/crafting/CampfireCookingRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/item/crafting/SmeltingRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/item/crafting/SmokingRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/item/crafting/StonecutterRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
	net/minecraft/world/item/crafting/UpgradeRecipe m_8042_()Lnet/minecraft/world/item/ItemStack;
net/minecraft/world/item/crafting/Recipe m_8004_(II)Z # canFit
	net/minecraft/world/item/crafting/AbstractCookingRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/ArmorDyeRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/BannerDuplicateRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/BookCloningRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/FireworkRocketRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/FireworkStarFadeRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/FireworkStarRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/MapCloningRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/RepairItemRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/ShapedRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/ShapelessRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/ShieldDecorationRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/ShulkerBoxColoring m_8004_(II)Z
	net/minecraft/world/item/crafting/SingleItemRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/SuspiciousStewRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/TippedArrowRecipe m_8004_(II)Z
	net/minecraft/world/item/crafting/UpgradeRecipe m_8004_(II)Z
net/minecraft/world/item/crafting/Recipe m_6076_()Ljava/lang/String; # getGroup
	net/minecraft/world/item/crafting/AbstractCookingRecipe m_6076_()Ljava/lang/String;
	net/minecraft/world/item/crafting/ShapedRecipe m_6076_()Ljava/lang/String;
	net/minecraft/world/item/crafting/ShapelessRecipe m_6076_()Ljava/lang/String;
	net/minecraft/world/item/crafting/SingleItemRecipe m_6076_()Ljava/lang/String;
net/minecraft/nbt/NbtIo m_128953_(Ljava/io/File;)Lnet/minecraft/nbt/CompoundTag; # read
net/minecraft/nbt/NbtIo m_128955_(Lnet/minecraft/nbt/CompoundTag;Ljava/io/File;)V # write
net/minecraft/network/protocol/game/ClientboundCommandsPacket m_131884_()Lcom/mojang/brigadier/tree/RootCommandNode; # getRoot
net/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket m_133591_()Ljava/util/List; # getSnapshots
net/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket m_133588_()I # getEntityId
net/minecraft/world/effect/MobEffect m_19483_()Lnet/minecraft/world/effect/MobEffectCategory; # getEffectType
net/minecraft/world/effect/MobEffect m_19485_()Ljava/util/Map; # getAttributeModifierMap
net/minecraft/world/effect/MobEffect m_19486_()Z # isBeneficial
net/minecraft/world/effect/MobEffectCategory m_19497_()Lnet/minecraft/ChatFormatting; # getColor
net/minecraft/server/packs/resources/ResourceManager m_7187_()Ljava/util/Set; # getResourceNamespaces
	net/minecraft/server/packs/resources/FallbackResourceManager m_7187_()Ljava/util/Set;
	net/minecraft/server/packs/resources/ResourceManager$Empty m_7187_()Ljava/util/Set;
	net/minecraft/server/packs/resources/SimpleReloadableResourceManager m_7187_()Ljava/util/Set;
net/minecraft/server/packs/resources/ResourceManager m_7165_(Lnet/minecraft/resources/ResourceLocation;)Z # hasResource
	net/minecraft/server/packs/resources/FallbackResourceManager m_7165_(Lnet/minecraft/resources/ResourceLocation;)Z
	net/minecraft/server/packs/resources/ResourceManager$Empty m_7165_(Lnet/minecraft/resources/ResourceLocation;)Z
	net/minecraft/server/packs/resources/SimpleReloadableResourceManager m_7165_(Lnet/minecraft/resources/ResourceLocation;)Z
net/minecraft/core/Direction m_122402_(Ljava/lang/String;)Lnet/minecraft/core/Direction; # byName
net/minecraft/core/Direction$Axis m_122473_(Ljava/lang/String;)Lnet/minecraft/core/Direction$Axis; # byName
net/minecraft/world/phys/Vec3 m_82548_()Lnet/minecraft/world/phys/Vec3;
net/minecraft/world/phys/Vec3 m_82503_(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec3; # fromPitchYaw
net/minecraft/world/phys/Vec3 m_82498_(FF)Lnet/minecraft/world/phys/Vec3; # fromPitchYaw
com/mojang/math/Vector4f # Vector 4f Class
net/minecraft/network/chat/Style m_131150_(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/network/chat/Style; #setFontId
net/minecraft/network/chat/Style m_131164_(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/Style; #forceFormatting
# BiomeSpecialEffects getters, needed for it to be useful during BiomeLoadingEvent to be useful
net/minecraft/world/level/biome/BiomeSpecialEffects m_47967_()I # getFogColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47972_()I # getWaterColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47975_()I # getWaterFogColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47978_()I # getSkyColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47981_()Ljava/util/Optional; # getFoliageColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47984_()Ljava/util/Optional; # getGrassColor
net/minecraft/world/level/biome/BiomeSpecialEffects m_47987_()Lnet/minecraft/world/level/biome/BiomeSpecialEffects$GrassColorModifier; # getGrassColorModifier
net/minecraft/world/level/biome/BiomeSpecialEffects m_47990_()Ljava/util/Optional; # getParticle
net/minecraft/world/level/biome/BiomeSpecialEffects m_47993_()Ljava/util/Optional; # getAmbientSound
net/minecraft/world/level/biome/BiomeSpecialEffects m_47996_()Ljava/util/Optional; # getMoodSound
net/minecraft/world/level/biome/BiomeSpecialEffects m_47999_()Ljava/util/Optional; # getAdditionsSound
net/minecraft/world/level/biome/BiomeSpecialEffects m_48002_()Ljava/util/Optional; # getMusic
net/minecraft/client/resources/model/ModelResourceLocation # ModelResourceLocation class