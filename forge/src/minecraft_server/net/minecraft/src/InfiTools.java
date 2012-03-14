package net.minecraft.src;

public class InfiTools
{
    private static final InfiTools instance = new InfiTools();

    public static final InfiTools getInstance()
    {
        return instance;
    }

    private InfiTools()
    {
    }

    public static InfiProps InitProps(InfiProps infiprops)
    {
        infiprops.accessInt("blockMossID", 252);
        infiprops.accessInt("rodID", 13891);
        infiprops.accessInt("paperMaterialsID", 13917);
        infiprops.accessInt("metalChunksID", 13925);
        infiprops.accessInt("mossID", 13929);
        infiprops.accessInt("crystalID", 13933);
        infiprops.accessInt("woodBucketID", 13949);
        infiprops.accessInt("cactusBucketID", 13954);
        infiprops.accessInt("goldBucketID", 13959);
        infiprops.accessInt("iceBucketID", 13965);
        infiprops.accessInt("lavaBucketID", 13970);
        infiprops.accessInt("slimeBucketID", 13974);
        infiprops.accessInt("ironBucketID", 13979);
        infiprops.accessInt("bowlID", 13981);
        infiprops.accessInt("materialShardID", 14001);
        infiprops.accessInt("pumpkinPieID", 14017);
        infiprops.accessInt("pumpkinPulpID", 14334);
        infiprops.accessInt("woodSwordID", 14101);
        infiprops.accessInt("stoneSwordID", 14113);
        infiprops.accessInt("ironSwordID", 14127);
        infiprops.accessInt("diamondSwordID", 14142);
        infiprops.accessInt("goldSwordID", 14156);
        infiprops.accessInt("redstoneSwordID", 14171);
        infiprops.accessInt("obsidianSwordID", 14185);
        infiprops.accessInt("sandstoneSwordID", 14201);
        infiprops.accessInt("boneSwordID", 14215);
        infiprops.accessInt("paperSwordID", 14231);
        infiprops.accessInt("mossySwordID", 14241);
        infiprops.accessInt("netherrackSwordID", 14250);
        infiprops.accessInt("glowstoneSwordID", 14268);
        infiprops.accessInt("iceSwordID", 14285);
        infiprops.accessInt("lavaSwordID", 14303);
        infiprops.accessInt("slimeSwordID", 14313);
        infiprops.accessInt("cactusSwordID", 14336);
        infiprops.accessInt("flintSwordID", 14349);
        infiprops.accessInt("brickSwordID", 14367);
        infiprops.accessInt("blazeSwordID", 14379);
        infiprops.accessInt("woodPickaxeID", 14401);
        infiprops.accessInt("stonePickaxeID", 14413);
        infiprops.accessInt("ironPickaxeID", 14427);
        infiprops.accessInt("diamondPickaxeID", 14442);
        infiprops.accessInt("goldPickaxeID", 14456);
        infiprops.accessInt("redstonePickaxeID", 14471);
        infiprops.accessInt("obsidianPickaxeID", 14485);
        infiprops.accessInt("sandstonePickaxeID", 14501);
        infiprops.accessInt("bonePickaxeID", 14515);
        infiprops.accessInt("paperPickaxeID", 14531);
        infiprops.accessInt("mossyPickaxeID", 14541);
        infiprops.accessInt("netherrackPickaxeID", 14550);
        infiprops.accessInt("glowstonePickaxeID", 14568);
        infiprops.accessInt("icePickaxeID", 14585);
        infiprops.accessInt("lavaPickaxeID", 14603);
        infiprops.accessInt("slimePickaxeID", 14613);
        infiprops.accessInt("cactusPickaxeID", 14636);
        infiprops.accessInt("flintPickaxeID", 14649);
        infiprops.accessInt("brickPickaxeID", 14667);
        infiprops.accessInt("blazePickaxeID", 14679);
        infiprops.accessInt("woodShovelID", 14701);
        infiprops.accessInt("stoneShovelID", 14713);
        infiprops.accessInt("ironShovelID", 14727);
        infiprops.accessInt("diamondShovelID", 14742);
        infiprops.accessInt("goldShovelID", 14756);
        infiprops.accessInt("redstoneShovelID", 14771);
        infiprops.accessInt("obsidianShovelID", 14785);
        infiprops.accessInt("sandstoneShovelID", 14801);
        infiprops.accessInt("boneShovelID", 14815);
        infiprops.accessInt("paperShovelID", 14831);
        infiprops.accessInt("mossyShovelID", 14841);
        infiprops.accessInt("netherrackShovelID", 14850);
        infiprops.accessInt("glowstoneShovelID", 14868);
        infiprops.accessInt("iceShovelID", 14885);
        infiprops.accessInt("lavaShovelID", 14903);
        infiprops.accessInt("slimeShovelID", 14913);
        infiprops.accessInt("cactusShovelID", 14936);
        infiprops.accessInt("flintShovelID", 14949);
        infiprops.accessInt("brickShovelID", 14967);
        infiprops.accessInt("blazeShovelID", 14979);
        infiprops.accessInt("woodAxeID", 15001);
        infiprops.accessInt("stoneAxeID", 15013);
        infiprops.accessInt("ironAxeID", 15027);
        infiprops.accessInt("diamondAxeID", 15042);
        infiprops.accessInt("goldAxeID", 15056);
        infiprops.accessInt("redstoneAxeID", 15071);
        infiprops.accessInt("obsidianAxeID", 15085);
        infiprops.accessInt("sandstoneAxeID", 15101);
        infiprops.accessInt("boneAxeID", 15115);
        infiprops.accessInt("paperAxeID", 15131);
        infiprops.accessInt("mossyAxeID", 15141);
        infiprops.accessInt("netherrackAxeID", 15150);
        infiprops.accessInt("glowstoneAxeID", 15168);
        infiprops.accessInt("iceAxeID", 15185);
        infiprops.accessInt("lavaAxeID", 15203);
        infiprops.accessInt("slimeAxeID", 15213);
        infiprops.accessInt("cactusAxeID", 15236);
        infiprops.accessInt("flintAxeID", 15249);
        infiprops.accessInt("brickAxeID", 15267);
        infiprops.accessInt("blazeAxeID", 15279);
        infiprops.accessInt("woodHoeID", 15301);
        infiprops.accessInt("stoneHoeID", 15313);
        infiprops.accessInt("ironHoeID", 15327);
        infiprops.accessInt("diamondHoeID", 15342);
        infiprops.accessInt("goldHoeID", 15356);
        infiprops.accessInt("redstoneHoeID", 15371);
        infiprops.accessInt("obsidianHoeID", 15385);
        infiprops.accessInt("sandstoneHoeID", 15401);
        infiprops.accessInt("boneHoeID", 15415);
        infiprops.accessInt("paperHoeID", 15431);
        infiprops.accessInt("mossyHoeID", 15441);
        infiprops.accessInt("netherrackHoeID", 15450);
        infiprops.accessInt("glowstoneHoeID", 15468);
        infiprops.accessInt("iceHoeID", 15485);
        infiprops.accessInt("lavaHoeID", 15503);
        infiprops.accessInt("slimeHoeID", 15513);
        infiprops.accessInt("cactusHoeID", 15536);
        infiprops.accessInt("flintHoeID", 15549);
        infiprops.accessInt("brickHoeID", 15567);
        infiprops.accessInt("blazeHoeID", 15579);
        infiprops.accessInt("woodFryingPanID", 15601);
        infiprops.accessInt("stoneFryingPanID", 15614);
        infiprops.accessInt("ironFryingPanID", 15629);
        infiprops.accessInt("diamondFryingPanID", 15645);
        infiprops.accessInt("goldFryingPanID", 15660);
        infiprops.accessInt("redstoneFryingPanID", 15676);
        infiprops.accessInt("obsidianFryingPanID", 15690);
        infiprops.accessInt("sandstoneFryingPanID", 15706);
        infiprops.accessInt("boneFryingPanID", 15720);
        infiprops.accessInt("paperFryingPanID", 15736);
        infiprops.accessInt("mossyFryingPanID", 15746);
        infiprops.accessInt("netherrackFryingPanID", 15755);
        infiprops.accessInt("glowstoneFryingPanID", 15773);
        infiprops.accessInt("iceFryingPanID", 15790);
        infiprops.accessInt("lavaFryingPanID", 15808);
        infiprops.accessInt("slimeFryingPanID", 15818);
        infiprops.accessInt("cactusFryingPanID", 15841);
        infiprops.accessInt("flintFryingPanID", 15854);
        infiprops.accessInt("brickFryingPanID", 15872);
        infiprops.accessInt("blazeFryingPanID", 15884);
        return infiprops;
    }

    public static void AddBlocks(Block ablock[], int i)
    {
        boolean flag = true;
        for (int j = 0; j < ablock.length; j++)
        {
            Block block = ablock[j];
            if (!(block instanceof Block))
            {
                flag = false;
            }
        }

        if (!flag)
        {
            throw new RuntimeException("Invalid block minimum-harvesting-level declared!");
        }
        else
        {
            InfiGather.getInstance();
            InfiGather.addBlocks(ablock, i);
            return;
        }
    }
}
