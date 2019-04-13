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

Currently updated through: c5402d50fad5dbb649015d9b6d9bead6b710e035
