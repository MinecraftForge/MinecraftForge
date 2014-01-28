package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public interface INetHandlerPlayServer extends INetHandler
{
    void func_147350_a(C0APacketAnimation var1);

    void func_147354_a(C01PacketChatMessage var1);

    void func_147341_a(C14PacketTabComplete var1);

    void func_147342_a(C16PacketClientStatus var1);

    void func_147352_a(C15PacketClientSettings var1);

    void func_147339_a(C0FPacketConfirmTransaction var1);

    void func_147338_a(C11PacketEnchantItem var1);

    void func_147351_a(C0EPacketClickWindow var1);

    void func_147356_a(C0DPacketCloseWindow var1);

    void func_147349_a(C17PacketCustomPayload var1);

    void func_147340_a(C02PacketUseEntity var1);

    void func_147353_a(C00PacketKeepAlive var1);

    void func_147347_a(C03PacketPlayer var1);

    void func_147348_a(C13PacketPlayerAbilities var1);

    void func_147345_a(C07PacketPlayerDigging var1);

    void func_147357_a(C0BPacketEntityAction var1);

    void func_147358_a(C0CPacketInput var1);

    void func_147355_a(C09PacketHeldItemChange var1);

    void func_147344_a(C10PacketCreativeInventoryAction var1);

    void func_147343_a(C12PacketUpdateSign var1);

    void func_147346_a(C08PacketPlayerBlockPlacement var1);
}