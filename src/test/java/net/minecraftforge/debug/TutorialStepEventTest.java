/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.TutorialStepChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tutorial_event_test")
@Mod.EventBusSubscriber
public class TutorialStepEventTest
{

    private static Logger LOGGER = LogManager.getLogger(TutorialStepEventTest.class);

    private static TutorialSteps testStep;

    public TutorialStepEventTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event)
    {
        testStep = TutorialSteps.create("test", "test", TestTutorialStepInstance::new);
    }

    @SubscribeEvent
    public static void onTutorialChange(TutorialStepChangeEvent.Pre event)
    {
        if (event.getNext() != TutorialSteps.NONE)
        {
            return;
        }

        event.setNext(testStep);
    }

    public static class TestTutorialStepInstance implements TutorialStepInstance
    {

        private static final Component TITLE = Component.translatable("tutorial.test.title");
        private static final Component DESCRIPTION = Component.translatable("tutorial.test.description");
        private final Tutorial tutorial;
        private TutorialToast toast;
        private int timeWaiting;

        public TestTutorialStepInstance(Tutorial p_120496_)
        {
            this.tutorial = p_120496_;
        }

        public void tick()
        {
            ++this.timeWaiting;
            if (!this.tutorial.isSurvival())
            {
                this.tutorial.setStep(TutorialSteps.NONE);
            }
            else
            {
                if (this.timeWaiting == 1)
                {
                    LocalPlayer localplayer = this.tutorial.getMinecraft().player;
                    if (localplayer != null && (hasCollectedTreeItems(localplayer) || hasPunchedTreesPreviously(localplayer)))
                    {
                        this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                        return;
                    }
                }

                if (this.timeWaiting >= 6000 && this.toast == null)
                {
                    this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
                    this.tutorial.getMinecraft().getToasts().addToast(this.toast);
                }

            }
        }

        public void clear()
        {
            if (this.toast != null)
            {
                this.toast.hide();
                this.toast = null;
            }

        }

        public void onLookAt(ClientLevel level, HitResult hitResult)
        {
            if (hitResult.getType() == HitResult.Type.BLOCK)
            {
                BlockState blockstate = level.getBlockState(((BlockHitResult)hitResult).getBlockPos());
                if (blockstate.is(BlockTags.COMPLETES_FIND_TREE_TUTORIAL))
                {
                    this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
                }
            }

        }

        public void onGetItem(ItemStack itemStack)
        {
            if (itemStack.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL))
            {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            }

        }

        private static boolean hasCollectedTreeItems(LocalPlayer player)
        {
            return player.getInventory().hasAnyMatching((p_235270_) ->
            {
                return p_235270_.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
            });
        }

        public static boolean hasPunchedTreesPreviously(LocalPlayer player)
        {
            for(Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.COMPLETES_FIND_TREE_TUTORIAL))
            {
                Block block = holder.value();
                if (player.getStats().getValue(Stats.BLOCK_MINED.get(block)) > 0)
                {
                    return true;
                }
            }

            return false;
        }
    }
}
