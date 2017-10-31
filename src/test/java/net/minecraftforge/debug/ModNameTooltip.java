package net.minecraftforge.debug;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "forgemodnametooltip", name = "ForgeModNameTooltip", version = "1.0", clientSideOnly = true)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ModNameTooltip
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onToolTip(ItemTooltipEvent event)
    {
        ItemStack itemStack = event.getItemStack();
        String modName = getModName(itemStack);
        if (modName != null)
        {
            List<String> toolTip = event.getToolTip();
            toolTip.add(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + modName);
        }
    }

    @Nullable
    private static String getModName(ItemStack itemStack)
    {
        if (!itemStack.isEmpty())
        {
            Item item = itemStack.getItem();
            String modId = item.getCreatorModId(itemStack);
            ModContainer modContainer = Loader.instance().getIndexedModList().get(modId);
            if (modContainer != null)
            {
                return modContainer.getName();
            }
        }
        return null;
    }
}
