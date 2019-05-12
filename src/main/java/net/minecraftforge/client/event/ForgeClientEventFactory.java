/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.io.File;
import java.util.Map;

public class ForgeClientEventFactory
{
    public static boolean onDrawBlockHighlight(WorldRenderer context, EntityPlayer player, RayTraceResult target, int subID, float partialTicks)
    {
        return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, player, target, subID, partialTicks));
    }

    public static void dispatchRenderLast(WorldRenderer context, float partialTicks)
    {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, partialTicks));
    }

    public static boolean renderFirstPersonHand(WorldRenderer context, float partialTicks)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(context, partialTicks));
    }

    public static boolean renderSpecificFirstPersonHand(EnumHand hand, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderSpecificHandEvent(hand, partialTicks, interpPitch, swingProgress, equipProgress, stack));
    }

    public static void onTextureStitchedPre(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Pre(map));
//        ModelLoader.White.INSTANCE.register(map); // TODO Custom TAS
        ModelDynBucket.LoaderDynBucket.INSTANCE.register(map);
    }

    public static void onTextureStitchedPost(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Post(map));
    }

    public static void onBlockColorsInit(BlockColors blockColors)
    {
        MinecraftForge.EVENT_BUS.post(new ColorHandlerEvent.Block(blockColors));
    }

    public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors)
    {
        MinecraftForge.EVENT_BUS.post(new ColorHandlerEvent.Item(itemColors, blockColors));
    }

    /* TODO mouse input
    public static boolean postMouseEvent()
    {
        return MinecraftForge.EVENT_BUS.post(new MouseEvent());
    }
    */

    public static float getOffsetFOV(EntityPlayer entity, float fov)
    {
        FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
        return fovUpdateEvent.getNewfov();
    }

    public static double getFOVModifier(GameRenderer renderer, Entity entity, IBlockState blockState, IFluidState fluidState, double renderPartialTicks, double fov)
    {
        EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, entity, blockState, fluidState, renderPartialTicks, fov);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getFOV();
    }

    public static ISound playSound(SoundManager manager, ISound sound)
    {
        PlaySoundEvent e = new PlaySoundEvent(manager, sound);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getResultSound();
    }

    public static void drawScreen(GuiScreen screen, int mouseX, int mouseY, float partialTicks)
    {
        if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mouseX, mouseY, partialTicks)))
            screen.render(mouseX, mouseY, partialTicks);
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mouseX, mouseY, partialTicks));
    }

    public static float getFogDensity(FogRenderer fogRenderer, GameRenderer renderer, Entity entity, IBlockState state, IFluidState fluidState, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(fogRenderer, renderer, entity, state, fluidState, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getDensity();
        return -1;
    }

    public static void onFogRender(FogRenderer fogRenderer, GameRenderer renderer, Entity entity, IBlockState state, IFluidState fluidState, float partial, int mode, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(fogRenderer, renderer, entity, state, fluidState, partial, mode, distance));
    }

    public static void onModelBake(ModelManager modelManager, Map<ModelResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        MinecraftForge.EVENT_BUS.post(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
        modelLoader.onPostBakeEvent(modelRegistry);
    }

    public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(MainWindow res, BossInfoClient bossInfo, int x, int y, int increment)
    {
        RenderGameOverlayEvent.BossInfo evt = new RenderGameOverlayEvent.BossInfo(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res),
                RenderGameOverlayEvent.ElementType.BOSSINFO, bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void bossBarRenderPost(MainWindow res)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res), RenderGameOverlayEvent.ElementType.BOSSINFO));
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onInputUpdate(EntityPlayer player, MovementInput movementInput)
    {
        MinecraftForge.EVENT_BUS.post(new InputUpdateEvent(player, movementInput));
    }

    public static boolean onGuiMouseClickedPre(GuiScreen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseClickedEvent.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseClickedPost(GuiScreen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseClickedEvent.Post(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseReleasedPre(GuiScreen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseReleasedEvent.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseReleasedPost(GuiScreen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseReleasedEvent.Post(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseDragPre(GuiScreen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new GuiScreenEvent.MouseDragEvent.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseDragPost(GuiScreen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new GuiScreenEvent.MouseDragEvent.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseScrollPre(MouseHelper mouseHelper, GuiScreen guiScreen, double scrollDelta)
    {
        MainWindow mainWindow = guiScreen.mc.mainWindow;
        double mouseX = mouseHelper.getMouseX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
        double mouseY = mouseHelper.getMouseY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
        Event event = new GuiScreenEvent.MouseScrollEvent.Pre(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseScrollPost(MouseHelper mouseHelper, GuiScreen guiScreen, double scrollDelta)
    {
        MainWindow mainWindow = guiScreen.mc.mainWindow;
        double mouseX = mouseHelper.getMouseX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
        double mouseY = mouseHelper.getMouseY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
        Event event = new GuiScreenEvent.MouseScrollEvent.Post(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyPressedPre(GuiScreen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyPressedPost(GuiScreen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyReleasedPre(GuiScreen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyReleasedPost(GuiScreen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiCharTypedPre(GuiScreen guiScreen, char codePoint, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Pre(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiCharTypedPost(GuiScreen guiScreen, char codePoint, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Post(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onRecipesUpdated()
    {
        Event event = new RecipesUpdatedEvent();
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void fireMouseInput(int button, int action, int mods)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
    }

    public static void fireKeyInput(int key, int scanCode, int action, int modifiers)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
    }
}
