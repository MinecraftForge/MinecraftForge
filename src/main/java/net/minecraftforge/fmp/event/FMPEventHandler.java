package net.minecraftforge.fmp.event;

import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.client.multipart.ModelMultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartRegistry;

public class FMPEventHandler
{

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onDrawGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && event instanceof RenderGameOverlayEvent.Text
                && Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            RenderGameOverlayEvent.Text ev = (RenderGameOverlayEvent.Text) event;
            RayTraceResult hit = Minecraft.getMinecraft().objectMouseOver;
            if (hit != null && hit.partHit != null)
            {
                if (hit.partHit != null)
                {
                    ev.getRight().add("");
                    ev.getRight().add(hit.partHit.getType().toString());

                    IBlockState state = hit.partHit.getExtendedState(MultipartRegistry.getDefaultState(hit.partHit).getBaseState());
                    for (Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                    {
                        String s = entry.getValue().toString();

                        if (entry.getValue() == Boolean.TRUE)
                        {
                            s = TextFormatting.GREEN + s;
                        }
                        else if (entry.getValue() == Boolean.FALSE)
                        {
                            s = TextFormatting.RED + s;
                        }

                        ev.getRight().add(entry.getKey().getName() + ": " + s);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        if (ForgeMultipartModContainer.registered)
        {
            // Link the custom ISmartBlockModel to the multipart block
            event.getModelRegistry().putObject(
                    new ModelResourceLocation(Block.REGISTRY.getNameForObject(ForgeMultipartModContainer.multipart), "ticking=false"),
                    new ModelMultipartContainer(null, null));
            event.getModelRegistry().putObject(
                    new ModelResourceLocation(Block.REGISTRY.getNameForObject(ForgeMultipartModContainer.multipart), "ticking=true"),
                    new ModelMultipartContainer(null, null));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public final void onDrawBlockHighlight(DrawBlockHighlightEvent event)
    {
        RayTraceResult hit = event.getTarget();
        if (hit != null && hit.partHit != null)
        {
            GlStateManager.pushMatrix();

            BlockPos pos = hit.getBlockPos();
            EntityPlayer player = event.getPlayer();
            float partialTicks = event.getPartialTicks();
            double x = pos.getX() - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
            double y = pos.getY() - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
            double z = pos.getZ() - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);
            GlStateManager.translate(x, y, z);

            if (hit.partHit.drawHighlight(hit, player, partialTicks))
            {
                event.setCanceled(true);
            }

            GlStateManager.popMatrix();
        }
    }

}
