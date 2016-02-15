package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="forge.testcapmod",version="1.0")
public class TestCapabilityMod
{
    // A Holder/Marker for if this capability is installed.
    // MUST be Static final doesn't matter but is suggested to prevent
    // you from overriding it elsewhere.
    // As Annotations and generic's are erased/unload at runtime this
    // does NOT create a hard dependency on the class.
    @CapabilityInject(IExampleCapability.class)
    private static final Capability<IExampleCapability> TEST_CAP = null;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        // If you are a API, provide register your capability ASAP.
        // You MUST supply a default save handler and a default implementation
        // If you are a CONSUMER of the capability DO NOT register it. Only APIs should.
        CapabilityManager.INSTANCE.register(IExampleCapability.class, new Storage(),    DefaultImpl.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if (event.entityPlayer.getHeldItem() == null) return;
        if (event.entityPlayer.getHeldItem().getItem() != Items.stick) return;

        // This is just a example of how to interact with the TE, note the strong type binding that getCapability has
        TileEntity te = event.world.getTileEntity(event.pos);
        if (te != null && te.hasCapability(TEST_CAP, event.face))
        {
            event.setCanceled(true);
            IExampleCapability inv = te.getCapability(TEST_CAP, event.face);
            System.out.println("Hi I'm a " + inv.getOwnerType());
        }
        if (event.world.getBlockState(event.pos).getBlock() == Blocks.dirt)
        {
            event.entityPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "" + EnumChatFormatting.ITALIC + "TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST"));
            event.setCanceled(true);
        }
    }


    // Example of having this annotation on a method, this will be called when the capability is present.
    // You could do something like register event handlers to attach these capabilities to objects, or
    // setup your factory, who knows. Just figured i'd give you the power.
    @CapabilityInject(IExampleCapability.class)
    private static void capRegistered(Capability<IExampleCapability> cap)
    {
        System.out.println("IExampleCapability was registered wheeeeee!");
    }

    // An example of how to attach a capability to an arbitrary Tile entity.
    // Note: Doing this IS NOT recommended for normal implementations.
    // If you control the TE it is HIGHLY recommend that you implement a fast
    // version of the has/getCapability functions yourself. So you have control
    // over everything yours being called first.
    @SubscribeEvent
    public void onTELoad(AttachCapabilitiesEvent.TileEntity event)
    {
        // Having the Provider implement the cap is not recomended as this creates a hard dep on the cap interface.
        // And doesnt allow for sidedness.
        // But as this is a example and we dont care about that here we go.
        class Provider implements ICapabilityProvider, IExampleCapability
        {
            private TileEntity te;

            Provider(TileEntity te)
            {
                this.te = te;
            }
            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing)
            {
                return TEST_CAP != null && capability == TEST_CAP;
            }
            @SuppressWarnings("unchecked") //There isnt anything sane we can do about this.
            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing)
            {
                if (TEST_CAP != null && capability == TEST_CAP) return (T)this;
                return null;
            }
            @Override
            public String getOwnerType() {
                return te.getClass().getName();
            }
        }

        //Attach it! The resource location MUST be unique it's recomneded that you tag it with your modid and what the cap is.
        event.addCapability(new ResourceLocation("TestCapabilityMod:DummyCap"), new Provider(event.getTileEntity()));
    }

    // Capabilities SHOULD be interfaces, NOT concrete classes, this allows for
    // the most flexibility for the implementors.
    public static interface IExampleCapability
    {
        String getOwnerType();
    }

    // Storage implementations are required, tho there is some flexibility here.
    // If you are the API provider you can also say that in order to use the default storage
    // the consumer MUST use the default impl, to allow you to access innards.
    // This is just a contract you will have to stipulate in the documentation of your cap.
    public static class Storage implements IStorage<IExampleCapability>
    {
        @Override
        public NBTBase writeNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side, NBTBase nbt) {
        }
    }

    // You MUST also supply a default implementation.
    // This is to make life easier on consumers.
    public static class DefaultImpl implements IExampleCapability {
        @Override
        public String getOwnerType(){
            return "Default Implementation!";
        }
    }
}