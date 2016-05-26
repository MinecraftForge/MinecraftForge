package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod(modid="CustomTextComponentDebug", name="CustomTextComponentDebug", version="0.0.0", acceptableRemoteVersions="*")
public class CustomTextComponentDebug
{
    // NOTE: Test with both this ON and OFF - ensure none of the test behaviours show when this is off!
    private static final boolean ENABLE = true;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerLogin(EntityJoinWorldEvent ev)
    {
        if(ENABLE && !ev.getWorld().isRemote && (ev.getEntity() instanceof EntityPlayer))
            ev.getEntity().addChatMessage(new TextComponentItemStack(new ItemStack(Items.COOKIE)));
    }
    
    public static class TextComponentItemStack extends TextComponentBase implements IJsonSerializable
    {
        private ItemStack stack;
    
        public TextComponentItemStack()
        {
        }
    
        public TextComponentItemStack(ItemStack stack)
        {
            this.stack = stack.copy();
        }
    
        public ItemStack getItemStack()
        {
            return stack;
        }
    
        @Override
        public String getUnformattedComponentText()
        {
            return "[" + stack.getDisplayName() + "]";
        }
    
        @Override
        public TextComponentItemStack createCopy()
        {
            TextComponentItemStack copy = new TextComponentItemStack(stack);
    
            copy.setStyle(this.getStyle().createShallowCopy());
    
            for (ITextComponent itextcomponent : this.getSiblings())
            {
                copy.appendSibling(itextcomponent.createCopy());
            }
    
            return copy;
        }
    
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }
            else if (!(other instanceof TextComponentItemStack))
            {
                return false;
            }
            else
            {
                return ItemStack.areItemStacksEqual(this.stack, ((TextComponentItemStack) other).getItemStack()) && super.equals(other);
            }
        }
    
        public String toString()
        {
            ResourceLocation item = stack.getItem().getRegistryName();
            int meta = stack.getMetadata();
            int stackSize = stack.stackSize;
            NBTTagCompound tag = stack.getTagCompound();
    
            StringBuilder b = new StringBuilder();
            b.append("ItemStack{item='"); b.append(item.toString()); b.append("'");
            b.append(", stackSize="); b.append(stackSize);
            if (meta != 0)
            {
                b.append(", meta="); b.append(meta);
            }
            if(tag != null && !tag.hasNoTags())
            {
                b.append(", tag="); b.append(tag.toString());
            }
            b.append("}");
    
            return b.toString();
        }

        @Override
        public void fromJson(JsonElement json)
        {
            JsonObject obj = json.getAsJsonObject();

            Item item = Item.REGISTRY.getObject(new ResourceLocation(obj.get("item").getAsString()));
            int meta = obj.has("meta") ? obj.get("meta").getAsInt() : 0;
            int stackSize = obj.get("stackSize").getAsInt();

            stack = new ItemStack(item, stackSize, meta);
            
            if (obj.has("tag"))
            {
                try
                {
                    stack.setTagCompound((NBTTagCompound)JsonToNBT.getTagFromJson(obj.get("tag").getAsString()));
                }
                catch (NBTException e)
                {
                    throw new ReportedException(new CrashReport("Error deserializing ItemStack text component", e));
                }
            }
        }

        @Override
        public JsonElement getSerializableElement()
        {
            JsonObject obj = new JsonObject();

            ResourceLocation item = stack.getItem().getRegistryName();
            int meta = stack.getMetadata();
            int stackSize = stack.stackSize;
            NBTTagCompound tag = stack.getTagCompound();

            obj.add("item", new JsonPrimitive(item.toString()));
            obj.add("stackSize", new JsonPrimitive(stackSize));
            if(meta != 0) obj.add("meta", new JsonPrimitive(meta));
            if(tag != null && !tag.hasNoTags())
            {
                obj.add("tag", new JsonPrimitive(tag.toString()).getAsJsonObject());
            }

            return obj;
        }
    }
}
