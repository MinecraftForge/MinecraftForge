/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.MinecartEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mod("minecart_link_test")
public class MinecartLinkTest
{

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecart_link_test");
    private static final RegistryObject<Item> LINKER = ITEMS.register("linker", () -> new LinkerItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)));

    public MinecartLinkTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterCapability);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::onMinecartPushed);
        MinecraftForge.EVENT_BUS.addListener(this::onMinecartPostMove);
        MinecraftForge.EVENT_BUS.addListener(this::onMinecartPreMove);
        MinecraftForge.EVENT_BUS.addListener(this::onMinecartSteered);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::onAttachCapability);
    }

    private void onMinecartPostMove(MinecartEvent.PostMove event)
    {
        LinkageHandler.adjustCart(event.getMinecart());
    }

    private void onMinecartPreMove(MinecartEvent.PreMove event)
    {
        if (event.getMinecart() instanceof MinecartChest) event.setCanceled(true);
    }

    private void onMinecartPushed(MinecartEvent.Pushed event)
    {
        event.setPushX(0);
        event.setPushZ(0);
    }

    private void onMinecartSteered(MinecartEvent.Steered event)
    {
        event.setX(event.getX() * 30);
        event.setZ(event.getZ() * 30);
    }

    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof AbstractMinecart)
        {
            event.addCapability(CapabilityMinecartLink.KEY, new MinecartLinkProvider());
        }
    }

    public void onRegisterCapability(RegisterCapabilitiesEvent event)
    {
        CapabilityMinecartLink.register(event);
    }

    public void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getItemStack().getItem() instanceof LinkerItem linkerItem)
        {
            if (event.getTarget() instanceof AbstractMinecart cart)
            {
                InteractionResult result = linkerItem.linkMinecart(event.getWorld(), event.getItemStack(), event.getPlayer(), cart);
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }

    public interface IMinecartLink extends INBTSerializable<CompoundTag>
    {

        UUID getLinkA();

        void setLinkA(UUID linkId);

        UUID getLinkB();

        void setLinkB(UUID linkId);

        boolean isLinkedA();

        boolean isLinkedB();

        class MinecartLink implements IMinecartLink
        {

            private UUID linkA;
            private UUID linkB;

            public MinecartLink()
            {
                linkA = new UUID(0, 0);
                linkB = new UUID(0, 0);
            }

            @Override
            public CompoundTag serializeNBT()
            {
                CompoundTag tag = new CompoundTag();
                tag.putUUID("LinkA", linkA);
                tag.putUUID("LinkB", linkB);
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                this.linkA = nbt.getUUID("LinkA");
                this.linkB = nbt.getUUID("LinkB");
            }

            @Override
            public UUID getLinkA()
            {
                return this.linkA;
            }

            @Override
            public void setLinkA(UUID linkId)
            {
                this.linkA = linkId;
            }

            @Override
            public UUID getLinkB()
            {
                return this.linkB;
            }

            @Override
            public void setLinkB(UUID linkId)
            {
                this.linkB = linkId;
            }

            @Override
            public boolean isLinkedA()
            {
                return !(this.linkA.getLeastSignificantBits() == 0 && this.linkA.getMostSignificantBits() == 0);
            }

            @Override
            public boolean isLinkedB()
            {
                return !(this.linkB.getLeastSignificantBits() == 0 && this.linkB.getMostSignificantBits() == 0);
            }

        }

    }

    private static class LinkerItem extends Item
    {

        public LinkerItem(Properties properties)
        {
            super(properties);
        }

        @Override
        public Component getName(ItemStack stack)
        {
            CompoundTag tag = stack.getOrCreateTag();
            boolean isLinking = tag.contains("FirstCartID") && tag.getInt("FirstCartID") != 0;
            return isLinking ? new TranslatableComponent("item.minecart_link_test.linker.linking") : super.getName(stack);
        }

        public InteractionResult linkMinecart(Level world, ItemStack itemStack, Player player, AbstractMinecart cart)
        {
            if (!world.isClientSide())
            {
                CompoundTag tag = itemStack.getOrCreateTag();
                if (tag.contains("FirstCartID"))
                {
                    int firstCartId = tag.getInt("FirstCartID");
                    if (firstCartId != 0)
                    {
                        AbstractMinecart firstCart = (AbstractMinecart) world.getEntity(firstCartId);
                        if (firstCart != null)
                        {
                            String message = switch (LinkageManager.INSTANCE.linkMinecarts(world, firstCart, cart))
                                    {
                                        case LINK_EXISTS -> "These carts are already linked!";
                                        case LINK_CREATED_A, LINK_CREATED_B -> "Linked successfully!";
                                        case LINK_FAILED -> "Failed to create link!";
                                        case LINK_OCCUPIED -> "These carts' links are occupied!";
                                    };
                            player.displayClientMessage(new TextComponent(message), true);
                        } else
                        {
                            player.displayClientMessage(new TextComponent("Failed to create link!"), true);
                        }
                        tag.putInt("FirstCartID", 0);
                        return InteractionResult.CONSUME;
                    }
                }
                tag.putInt("FirstCartID", cart.getId());
            }
            return InteractionResult.CONSUME;
        }

    }

    private static class LinkageManager
    {
        public static final LinkageManager INSTANCE = new LinkageManager();

        public LinkState linkCarts(Level level, AbstractMinecart cartA, IMinecartLink linkA, AbstractMinecart cartB, IMinecartLink linkB)
        {
            LinkState linkResultA = tryLinkTo(cartA, level, linkA, cartB);
            LinkState linkResultB = tryLinkTo(cartB, level, linkB, cartA);
            if (linkResultA.isCreated() && linkResultB.isCreated())
            {
                switch (linkResultA)
                {
                    case LINK_CREATED_A -> linkA.setLinkA(cartB.getUUID());
                    case LINK_CREATED_B -> linkA.setLinkB(cartB.getUUID());
                }
                switch (linkResultB)
                {
                    case LINK_CREATED_A -> linkB.setLinkA(cartA.getUUID());
                    case LINK_CREATED_B -> linkB.setLinkB(cartA.getUUID());
                }
                return LinkState.LINK_CREATED_A;
            }
            if (linkResultA == LinkState.LINK_EXISTS || linkResultB == LinkState.LINK_EXISTS)
            {
                return LinkState.LINK_EXISTS;
            }
            return LinkState.LINK_OCCUPIED;
        }

        private LinkState tryLinkTo(AbstractMinecart thisCart, Level level, IMinecartLink link, AbstractMinecart otherCart)
        {
            boolean alreadyLinked = isLinkedTo(thisCart, level, link, otherCart);
            if (alreadyLinked) return LinkState.LINK_EXISTS;
            if (!link.isLinkedA())
            {
                return LinkState.LINK_CREATED_A;
            }
            if (!link.isLinkedB())
            {
                return LinkState.LINK_CREATED_B;
            }
            return LinkState.LINK_OCCUPIED;
        }

        private boolean isLinkedTo(AbstractMinecart thisCart, Level level, IMinecartLink link, AbstractMinecart otherCart)
        {
            Set<AbstractMinecart> linkedCarts = getLinkedCarts(thisCart, level);
            return linkedCarts.contains(otherCart);
        }

        private Set<AbstractMinecart> getLinkedCarts(AbstractMinecart cart, Level level)
        {
            Set<AbstractMinecart> minecarts = new HashSet<>();
            minecarts.add(cart);
            getLinkedCarts(cart, level, minecarts);
            return minecarts;
        }

        private void getLinkedCarts(AbstractMinecart cart, Level level, Set<AbstractMinecart> minecarts)
        {
            cart.getCapability(CapabilityMinecartLink.MINECART_LINK_CAPABILITY).ifPresent(link -> {
                if (link.isLinkedA())
                {
                    UUID cartIdA = link.getLinkA();
                    AbstractMinecart linkedCartA = (AbstractMinecart) ((ServerLevel) level).getEntity(cartIdA);
                    assert linkedCartA != null;
                    if (!minecarts.contains(linkedCartA))
                    {
                        minecarts.add(linkedCartA);
                        getLinkedCarts(linkedCartA, level, minecarts);
                    }
                }
                if (link.isLinkedB())
                {
                    UUID cartIdB = link.getLinkB();
                    AbstractMinecart linkedCartB = (AbstractMinecart) ((ServerLevel) level).getEntity(cartIdB);
                    assert linkedCartB != null;
                    if (!minecarts.contains(linkedCartB))
                    {
                        minecarts.add(linkedCartB);
                        getLinkedCarts(linkedCartB, level, minecarts);
                    }
                }
            });
        }

        private LinkState linkMinecarts(Level level, AbstractMinecart cartA, AbstractMinecart cartB)
        {
            if (cartA == cartB) return LinkState.LINK_FAILED;
            Optional<IMinecartLink> cartLinkOptA = cartA.getCapability(CapabilityMinecartLink.MINECART_LINK_CAPABILITY).resolve();
            Optional<IMinecartLink> cartLinkOptB = cartB.getCapability(CapabilityMinecartLink.MINECART_LINK_CAPABILITY).resolve();
            if (cartLinkOptA.isPresent() && cartLinkOptB.isPresent())
            {
                return linkCarts(level, cartA, cartLinkOptA.get(), cartB, cartLinkOptB.get());
            }
            return LinkState.LINK_FAILED;
        }

        private enum LinkState
        {
            LINK_EXISTS,
            LINK_CREATED_A,
            LINK_CREATED_B,
            LINK_FAILED,
            LINK_OCCUPIED;

            public boolean isCreated()
            {
                return this == LINK_CREATED_A || this == LINK_CREATED_B;
            }
        }
    }

    public static class CapabilityMinecartLink
    {

        public static final Capability<IMinecartLink> MINECART_LINK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>()
        {
        });
        public static final ResourceLocation KEY = new ResourceLocation("minecart_link_test:minecart_link");

        public static void register(RegisterCapabilitiesEvent event)
        {
            event.register(IMinecartLink.class);
        }

    }

    private static class MinecartLinkProvider extends CapabilityProvider<MinecartLinkProvider> implements ICapabilitySerializable<CompoundTag>
    {
        private final LazyOptional<IMinecartLink> minecartLink;

        protected MinecartLinkProvider()
        {
            super(MinecartLinkProvider.class);
            minecartLink = LazyOptional.of(IMinecartLink.MinecartLink::new);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction dir)
        {
            if (cap == CapabilityMinecartLink.MINECART_LINK_CAPABILITY)
            {
                return minecartLink.cast();
            }

            return super.getCapability(cap, dir);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return minecartLink.map(INBTSerializable::serializeNBT).orElseThrow();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            minecartLink.ifPresent(link -> link.deserializeNBT(nbt));
        }

    }

    public static class LinkageHandler
    {

        public static void adjustCart(AbstractMinecart cart)
        {
            adjustLinkedCart(cart);
        }

        private static void adjustLinkedCart(AbstractMinecart cart)
        {
            Optional<IMinecartLink> linkOpt = cart.getCapability(CapabilityMinecartLink.MINECART_LINK_CAPABILITY).resolve();
            if (linkOpt.isPresent())
            {
                IMinecartLink link = linkOpt.get();
                AbstractMinecart cartA = (AbstractMinecart) ((ServerLevel) cart.level).getEntity(link.getLinkA());
                if (cartA != null)
                {
                    adjustVelocity(cartA, cart);
                }
                AbstractMinecart cartB = (AbstractMinecart) ((ServerLevel) cart.level).getEntity(link.getLinkB());
                if (cartB != null)
                {
                    adjustVelocity(cart, cartB);
                }
            }
        }

        protected static void adjustVelocity(AbstractMinecart cart1, AbstractMinecart cart2)
        {
            double dist = cart1.position().distanceTo(cart2.position());
            if (dist > 8F)
            {
                // BREAK LINK
                return;
            }

            Vec3 cart1Pos = cart1.position();
            Vec3 cart2Pos = cart2.position();

            Vec3 unit = cart2Pos.subtract(cart1Pos).normalize();// Vec2.unit(cart2Pos, cart1Pos);

            float optDist = 0.78f * 2f;
            double stretch = dist - optDist;

            double stiffness = 0.7F;
            double springX = stiffness * stretch * unit.x;
            double springZ = stiffness * stretch * unit.z;

            springX = limitForce(springX);
            springZ = limitForce(springZ);

            Vec3 cartMotion1 = cart1.getDeltaMovement();
            Vec3 cartMotion2 = cart2.getDeltaMovement();

            cartMotion1 = cartMotion1.add(springX, 0, springZ);
            cartMotion2 = cartMotion2.subtract(springX, 0, springZ);
            cart1.setDeltaMovement(cartMotion1);
            cart2.setDeltaMovement(cartMotion2);

            // Damping

            Vec3 cart1Vel = cart1.getDeltaMovement();
            Vec3 cart2Vel = cart2.getDeltaMovement();

            double dot = cart2Vel.subtract(cart1Vel).dot(unit);//Vec3.subtract(cart2Vel, cart1Vel).dotProduct(unit);

            double damping = 0.4F;
            double dampX = damping * dot * unit.x;
            double dampZ = damping * dot * unit.z;

            dampX = limitForce(dampX);
            dampZ = limitForce(dampZ);

            cart1Vel = cart1Vel.add(dampX, 0, dampZ);
            cart2Vel = cart2Vel.subtract(dampX, 0, dampZ);
            cart1.setDeltaMovement(cart1Vel);
            cart2.setDeltaMovement(cart2Vel);
        }

        private static double limitForce(double force)
        {
            return Math.copySign(Math.min(Math.abs(force), /*FORCE_LIMITER*/ 6F), force);
        }

    }
}
