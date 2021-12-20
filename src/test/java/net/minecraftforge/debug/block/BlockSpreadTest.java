package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.block.spreader.SpreadBehaviors;
import net.minecraftforge.common.block.spreader.SpreaderType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("block_spread_test")
public class BlockSpreadTest
{
    private static boolean isEnabled = true;

    public BlockSpreadTest(){
        if(isEnabled)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event){
        SpreadBehaviors.addSpreaderBehavior(Blocks.COAL_BLOCK, SpreaderType.MYCELIUM, ((state, level, pos) -> Blocks.DIAMOND_BLOCK.defaultBlockState()));
    }
}
