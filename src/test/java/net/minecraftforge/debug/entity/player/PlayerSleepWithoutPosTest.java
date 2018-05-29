package net.minecraftforge.debug.entity.player;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;

import static net.minecraftforge.debug.entity.player.PlayerSleepWithoutPosTest.*;

/**
 * A test mod that adds a minecart in which the player can sleep. The minecart can only be summoned by commands.
 */
@SuppressWarnings("unused")
@Mod(modid = MOD_ID, name = "Player Sleep Without Position Test", acceptableRemoteVersions = "*")
public final class PlayerSleepWithoutPosTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "player_sleep_without_pos_test";
    static final String CHANNEL = "PSWP";
    private static final ResourceLocation CART_NAME = new ResourceLocation(MOD_ID, "bed_cart");
    FMLEventChannel channel;
    @SideOnly(Side.CLIENT)
    KeyBinding sleepKey;

    public PlayerSleepWithoutPosTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!ENABLED)
        {
            return;
        }
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);
        channel.register(this);
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            ClientRegistry.registerKeyBinding(sleepKey = new KeyBinding(
                    "player_sleep_without_pos_test.key.sleep",
                    Keyboard.KEY_DOWN,
                    MOD_ID + ".keys"
            ));
            MinecraftForge.EVENT_BUS.register(new Object()
            {
                @SubscribeEvent
                public void onKeyInput(InputEvent.KeyInputEvent event)
                {
                    if (sleepKey.isKeyDown())
                    {
                        Entity entity = Minecraft.getMinecraft().player.getRidingEntity();
                        if (entity == null)
                        {
                            return;
                        }
                        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                        buffer.writeInt(entity.getEntityId());
                        channel.sendToServer(new FMLProxyPacket(buffer, CHANNEL));
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event)
    {
        if (!event.getPacket().channel().equals(CHANNEL))
        {
            return;
        }
        int id = event.getPacket().payload().readInt();
        Entity entity = ((NetHandlerPlayServer) event.getHandler()).player.world.getEntityByID(id);
        if (!(entity instanceof EntityCartBed))
        {
            return;
        }
        EntityCartBed cart = (EntityCartBed) entity;
        cart.attemptSleep();
    }

    @SubscribeEvent
    public void registerEntity(RegistryEvent.Register<EntityEntry> event)
    {
        event.getRegistry().register(new EntityEntry(EntityCartBed.class, "bed_cart")
        {
            @Override
            protected void init()
            {
            }

            @Override
            public Entity newInstance(World world)
            {
                return new EntityCartBed(world);
            }
        }.setRegistryName(MOD_ID, "bed_cart"));
        EntityRegistry.registerModEntity(CART_NAME, EntityCartBed.class, "bed_cart", 0, this, 80, 3, true);
    }

    static void startSleeping(EntityPlayer player)
    {
        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, true, 26);
        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, 0, 28);
    }

    final class EntityCartBed extends EntityMinecartEmpty
    {

        private WeakReference<EntityPlayer> lastSleeper = new WeakReference<>(null);
        private boolean wokeUp = false;
        private boolean rideAfterSleep = false;
        private boolean shouldSleep = false;

        EntityCartBed(World world)
        {
            super(world);
            if (!world.isRemote)
                MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        public boolean canBeRidden()
        {
            return true;
        }

        @Override
        public IBlockState getDefaultDisplayTile()
        {
            return Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.GRAY);
        }

        @Override
        public void onUpdate()
        {
            super.onUpdate();
            if (world.isRemote)
            {
                return;
            }

            EntityPlayer sleeper = lastSleeper.get();
            if (sleeper != null)
            {
                if (sleeper.getRidingEntity() != this)
                    sleeper.startRiding(this, true);
                // Riding enforced! Otherwise player dismounts once sleeps
                if (rideAfterSleep)
                    sendPlayerRiding((EntityPlayerMP) sleeper);
                if (!wokeUp)
                {
                    sleeper.bedLocation = getPosition();
                    return;
                }
                wokeUp = false;
                lastSleeper = new WeakReference<>(null);
            }

            Entity rider = getFirstPassenger();
            if (!(rider instanceof EntityPlayer))
                return;
            EntityPlayer player = (EntityPlayer) rider;

            if (shouldSleep && world.provider.isSurfaceWorld() && !player.isPlayerSleeping())
            {
                shouldSleep = false;
                EntityPlayer.SleepResult sleepResult = player.trySleep(getPosition());
                if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE)
                {
                    player.sendMessage(new TextComponentTranslation("tile.bed.notSafe"));
                }
                else if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                {
                    player.sendMessage(new TextComponentTranslation("tile.bed.noSleep"));
                }
            }
        }

        @Override
        protected void addPassenger(Entity passenger)
        {
            super.addPassenger(passenger);
            if (!world.isRemote && passenger instanceof EntityPlayerMP)
            {
                passenger.sendMessage(new TextComponentTranslation("player_sleep_without_pos_test.press",
                        new TextComponentTranslation(PlayerSleepWithoutPosTest.this.sleepKey.getKeyDescription())));
            }
        }

        @Override
        public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
        {
            //No rider removal!
        }

        @SubscribeEvent
        public void onPlayerSleep(PlayerSleepInBedEvent event)
        {
            EntityPlayer player = event.getEntityPlayer();
            // Hmm, player is most likely right, although we cannot really guarantee yet!
            if (event.getPos().equals(getPosition()) && player == getFirstPassenger())
            {
                event.setResult(trySleep(player));
                if (event.getResultStatus() == EntityPlayer.SleepResult.OK)
                {
                    lastSleeper = new WeakReference<>(player);
                    rideAfterSleep = true;
                }
            }
        }

        @SubscribeEvent
        public void onLocationCheck(SleepingLocationCheckEvent event)
        {
            if (event.getEntityPlayer() == lastSleeper.get())
            {
                event.setResult(Event.Result.ALLOW);
            }
        }

        @SubscribeEvent
        public void onWakeUp(PlayerWakeUpEvent event)
        {
            if (event.getEntityPlayer() == lastSleeper.get())
            {
                wokeUp = true;
            }
        }

        private EntityPlayer.SleepResult trySleep(EntityPlayer player)
        {
            if (player.isPlayerSleeping() || !player.isEntityAlive())
            {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (!player.world.provider.isSurfaceWorld())
            {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
            }

            if (player.world.isDaytime())
            {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class,
                    player.getEntityBoundingBox().expand(d0, d1, d0));
            if (!list.isEmpty())
            {
                return EntityPlayer.SleepResult.NOT_SAFE;
            }

            player.setPosition(posX, posY, posZ);

            startSleeping(player);
            player.setPosition(getPosition().getX(), getPosition().getY(), getPosition().getZ());

            if (!player.world.isRemote)
            {
                player.world.updateAllPlayersSleepingFlag();
            }

            return EntityPlayer.SleepResult.OK;
        }

        private void sendPlayerRiding(EntityPlayerMP player)
        {
            player.connection.sendPacket(new SPacketSetPassengers(this));
        }

        public void attemptSleep()
        {
            shouldSleep = true;
        }

        @Override
        public void setDead()
        {
            super.setDead();
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        @Nullable
        protected Entity getFirstPassenger()
        {
            List<Entity> passengers = getPassengers();
            return passengers.isEmpty() ? null : passengers.get(0);
        }
    }
}

