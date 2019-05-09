|Commit|Needs Revisit|Note|
|------|-------------|----|
|[Class transformer optimizations (#5159)](https://github.com/MinecraftForge/MinecraftForge/commit/10036aa764f9678ef2c86cde40ddcaf72d32dc95)| Yes |  |
|[Change universal bucket support to use fluid names instead of instances](https://github.com/MinecraftForge/MinecraftForge/commit/82262e462e5ea8ff3a368ffce7d0edfd301dfeb0)| Yes | The affected code is nearly entirely absent |
|[Change biome spawn list entries to use factory method where possible (#5075)](https://github.com/MinecraftForge/MinecraftForge/commit/214275babadb9288ecea779245442687dd3ae8cd)| No | EntityType makes this obsolete |
|[Prevent some texture loading errors from crashing the game (#5121)](https://github.com/MinecraftForge/MinecraftForge/commit/633746673523e9375a933fa00badcc180018332e)| Yes | TextureMap has changed drastically, is this still needed? |
|[Add a notification event for handling game rule changes (#5152)](https://github.com/MinecraftForge/MinecraftForge/commit/55a7c6d64b61a40a5d7aa8cef4259f2209e2cd5e) | No | `GameRules$Value` has a change callback, mods can just use this |
|[Allow IModel to express itself as a vanilla parent (#5195)](https://github.com/MinecraftForge/MinecraftForge/commit/f9c2f715fd63bed9d4bd78b332ed348ad7383a30) | Yes | Vanilla has integrated the check we used to do (see ModelBlock#resolveParent) so this will need a new solution. |
|[A different approach to my changes in 8ace535 to fix #5207](https://github.com/MinecraftForge/MinecraftForge/commit/10dbbf9c1915b7f5b2fc879a630b199aedbd192a) | No | 🦀FML IS GONE🦀 |
|[Fix patches from #5160 setting rotation as well as position (#5233)](https://github.com/MinecraftForge/MinecraftForge/commit/ebc4eaddac66e8d9eee6a859b473d3165b1bbfdb) | No | Apparently I included it in [this commit](https://github.com/MinecraftForge/MinecraftForge/commit/958bbf6c9a3c55aa2a1543679f95b4b985a31ed3) accidentally. |
|[Use HTTPS for files website.](https://github.com/MinecraftForge/MinecraftForge/commit/607da1bd9b07ed07d8332530d90bca869503cc6d) | No | Irrelevant, buildscript was completely changed |
|[Prevent RecipeBook from crashing on empty modded ingredients (#5234)](https://github.com/MinecraftForge/MinecraftForge/commit/e406137b19596224997397ebfbd033f21defefce) | No | Fixed in vanilla |
|[Improve reflection helper methods (#4853)](https://github.com/MinecraftForge/MinecraftForge/commit/d16472d0ba38c04d46fbdb59cc035c6796b4798f) | No | ObfuscationReflectionHelper refactored |
|[Clean up logged mod states (#5227)](https://github.com/MinecraftForge/MinecraftForge/commit/0b47ccc015dd1ac2a1d9210b49938cff6f2596d7) | Yes | Currently the mod state table dump is absent from crash reports, should revisit once that is added again |
|[Fix minor issue in getFilledPercentage for Fluid rendering (#5206)](https://github.com/MinecraftForge/MinecraftForge/commit/3cf97f43fec5514033f24b099218c3254753dff8) | No | Fluid block classes are gone |
|[Fix missing comments in configs created with annotations (#5189)](https://github.com/MinecraftForge/MinecraftForge/commit/3dfe47944d80432c31108b8497b6ed9376faf4be) | No | Configs rewritten |
|[Compute ASMDataTable submaps parallel, speeds up contructing mods (#5246)](https://github.com/MinecraftForge/MinecraftForge/commit/7f337cf2309631bccd2d6c573c1a348f48f067f3) | No | ASMData is no more |
|[Add methods to allow loading json constants outside of _constants (#4975)](https://github.com/MinecraftForge/MinecraftForge/commit/5f56b7cd096add31687b21bbb8b6be92a112e7c0) | Yes | CraftingHelper was overhauled quite a bit, is this necessary still? |
|[Fix crash from CraftingHelper due to FileSystem being closed early](https://github.com/MinecraftForge/MinecraftForge/commit/5ef199f6d71f2baba34fd4cc4ac3fa160274d580) | No | Not applicable as it only fixes the above commit |
|[Clean up CraftingHelper constants loading API](https://github.com/MinecraftForge/MinecraftForge/commit/f1d1e8853b278ab2c09df264583ae6cc76d0c752) | No | " |
|[Fix issue with --modListFile. (#5316)](https://github.com/MinecraftForge/MinecraftForge/commit/c5402d50fad5dbb649015d9b6d9bead6b710e035) | No | --modListFile is gone |
|[Fix incorrect indexing in mipmap generation code (#5201)](https://github.com/MinecraftForge/MinecraftForge/commit/3dd8c8ae6f197c75b23008f5533160b2794a1c85) | No | TAS was totally refactored, this was likely fixed by vanilla in the process |
|[Allow config GUI cycling button elements generated from enums to display toString return values, rather than actual values. (#5125)](https://github.com/MinecraftForge/MinecraftForge/commit/d3f27cf6fbb36f5ff7189fba0c985955e3cf45b5) | No | Config GUI is gone |
|[Add an annotation for @config elements which will automatically create a slider control (#5026)](https://github.com/MinecraftForge/MinecraftForge/commit/d1478ca52996894e1d83a9f9a6694bb9c84e3af0) | No | Config annotations are gone |
|[Added an additional constructor to every implementation of IFluidBlock. It is now possible to create a fluid block with a Fluid, Material and MapColor, so that the Material's MapColor isn't used for the blocks MapColor. (#5293)](https://github.com/MinecraftForge/MinecraftForge/commit/21f7c31f6217d91b9d6a0afc012b068e1a59b744) | No | IFluidBlock etc. are defunct for the moment |
|[Add default impl to IConfigElement#getValidValuesDisplay](https://github.com/MinecraftForge/MinecraftForge/commit/d5ff207fabd9d50c8739c6df5dc98d8ede7ea562) | No | IConfigElement etc. are gone |
|[Remove FluidStack amount from hashcode calculation (#5272)](https://github.com/MinecraftForge/MinecraftForge/commit/41a098e2f789a507d8d56f4aa3facfe9c8088164) | No | FluidStack is gone |
|[Fixed incorrect string representation of string list config property default values in their comments.](https://github.com/MinecraftForge/MinecraftForge/commit/965d8728564420983ef15377612c61274c2cd54f) | No | Configuration is gone |
|[Improve tracking of used dimension IDs (#5249)](https://github.com/MinecraftForge/MinecraftForge/commit/bcbf18491542b6eb8ef5516beb71a7ed9f1aa6a9) | No | Dimensions now have a proper registry |
|[Apply access-level changes to inner class attributes (#5468)](https://github.com/MinecraftForge/MinecraftForge/commit/e6fbf39591b920532ea464d36dad24671c6d08fd) | No | AccessTransformer has been rewritten and seems to handle this case already |
|[Generalise EnumRarity to an interface (#5182)](https://github.com/MinecraftForge/MinecraftForge/commit/cc95c40b5ba0b2219d492b6506fa1040fa9e9fb1) | Yes | This involves a lot of patches for a rather niche feature, that would be now be best implemented with `@ExtensibleEnum`. Needs to be re-evaluated later. |
|[Fix block placement not checking for player collision](https://github.com/MinecraftForge/MinecraftForge/commit/3870951a3738389bea521bd13b2b08efba542f77) | No | Bug was not introduced in cherry-picked commit |
|[Fix #5651 Re-add canPlaceBlockOnSide check in World#mayPlace](https://github.com/MinecraftForge/MinecraftForge/commit/e7beed487531d199ca1ddffa7b7cc314024bf8a8) | No | " |
|[Try and make 1.13 mods more obviously wrong in 1.12..](https://github.com/MinecraftForge/MinecraftForge/commit/1f12642a05e4f97ff1d83abb1a7c787422490900) | No |  |
|[Fixed boat not taking care of block-liquid hooks (#5086)](https://github.com/MinecraftForge/MinecraftForge/commit/6162f438f18f5e78f31239cdef1a62aab374f95a) | No | No longer applies to new fluid blocks |
|[Added Wool to OreDictionary (#5414)](https://github.com/MinecraftForge/MinecraftForge/commit/3e7b8809b626e6db9230a9a364bdc9eb2961024b) | No | OreDictionary is gone |
|[Invalidate tile entities that are queued for removal (#5512)](https://github.com/MinecraftForge/MinecraftForge/commit/75788f63eea6c33ccef7e5cbcab27ad9ad2c2a04) | No | Reverted later |
|[Revert "Invalidate tile entities that are queued for removal (#5512)"](https://github.com/MinecraftForge/MinecraftForge/commit/633fd9bd2eea06abd2eb42484439a5250236435b) | No |  |


Currently updated through: 633fd9bd2eea06abd2eb42484439a5250236435b
