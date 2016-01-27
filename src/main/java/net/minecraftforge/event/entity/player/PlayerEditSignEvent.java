package net.minecraftforge.event.entity.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This event is fired when a C12PacketUpdateSign is processed.
 *
 * This event is fired via {@link ForgeHooks#onSignEditEvent(net.minecraft.network.play.client.C12PacketUpdateSign, EntityPlayerMP)},
 * which is executed by the NetHandlerPlayServer#processUpdateSign(net.minecraft.network.play.client.C12PacketUpdateSign)<br>
 *
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the sign will not be updated with the contents of this event.<br>
 * If it has been newly placed. there will be a blank sign at the given coordinates.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/

@Cancelable
public class PlayerEditSignEvent extends PlayerEvent
{
    /**
     * The location of the sign being edited
     */
    public final BlockPos pos;

    private IChatComponent[] text;

    public PlayerEditSignEvent(BlockPos pos, IChatComponent[] text, EntityPlayerMP editor)
    {
        super(editor);
        this.pos = pos;
        this.text = text;
    }

    /**
     * Get the contents of the sign
     * Vanilla signs will have 4 items in the array, modded ones may have more.
     *
     * @return an array of IChatComponents, one for each line of text
     */
    public IChatComponent[] getText()
    {
        return text;
    }

    /**
     * Set the contents of the sign
     * Vanilla signs will have 4 items in the array, modded ones may have more.
     *
     * @param text an array of IChatComponents, one for each line of text
     */
    public void setText(IChatComponent[] text)
    {
        this.text = text;
    }
}