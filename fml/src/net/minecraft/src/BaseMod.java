package net.minecraft.src;

import java.util.Random;

import fml.Mod;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

public abstract class BaseMod {
  int addFuel(int id, int metadata) {
    return 0;
  }

  public boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
    return false;
  }

  public void generateNether(World world, Random random, int chunkX, int chunkZ) {
  }

  public void generateSurface(World world, Random random, int chunkX, int chunkZ) {
  }

  public String getName() { return ""; }

  public String getPriorities() {
    return null;
  }

  public abstract String getVersion();

  public abstract void load();

  public void modsLoaded() {
  }

  public void onItemPickup(EntityPlayer player, ItemStack item) {
  }

  // boolean onTickInGame(float tick, Minecraft game);
  public boolean onTickInGame(float tick) {
    return false;
  }

  public void receiveChatPacket(String text) {
  }

  public void receiveCustomPacket(Packet250CustomPayload packet) {
  }

  public void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix) {
  }

  public void takenFromFurnace(EntityPlayer player, ItemStack item) {

  }

  // void addRenderer(Map<Class<? extends Entity>, Render> renderers);
  // void registerAnimation(Minecraft game);
  // void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelID);
  // boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID);
  // boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui);
  // void keyboardEvent(KeyBinding event);
}
