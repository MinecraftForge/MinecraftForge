package net.minecraftforge.debug;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Random;

@Mod(modid=PotionRegistryDebug.MODID)
public class PotionRegistryDebug {
  public static final String MODID = "ForgePotionRegistry";

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    new PotionForge(new ResourceLocation("forge", "forge"), false, 0xff00ff); // test automatic id distribution
    new PotionForge(200, new ResourceLocation("forge", "forgy"), true, 0x00ff00); // test that ids above 127 work

    Random rand = new Random();
    TIntSet taken = new TIntHashSet(100);
    int ra = rand.nextInt(100) + 100;
    taken.add(ra);

    // a new potion with a random id so that forge has to remap it
    new PotionForge(ra, new ResourceLocation("forge", "realRandomPotion"), false, 0x0000ff);

    for(int i = 0; i < 20; i++) {
      int r = rand.nextInt(200) + 35;
      while(taken.contains(r))
        r = rand.nextInt(200) + 35;
      //r = 32+i;
      taken.add(r);
      // this potions will most likely not have the same IDs between server and client.
      // The forge handshake on connect should fix this.
      new PotionForge(r, new ResourceLocation("forge", "randomPotion" + r), false, 0xff00ff);
    }
  }

  protected class PotionForge extends Potion {

    public PotionForge(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
      setPotionName("potion." + location.getResourcePath());
    }

    protected PotionForge(ResourceLocation location, boolean badEffect, int potionColor) {
      super(location, badEffect, potionColor);
      setPotionName("potion." + location.getResourcePath());
    }

    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      Potion potion = Potion.potionTypes[effect.getPotionID()];

      mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_0");

      x += 6;
      y += 7;

      int width = 18;
      int height = width;
/*
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.startDrawingQuads();
      worldrenderer.setColorOpaque_I(potion.getLiquidColor());
      worldrenderer.addVertexWithUV((double) x, (double) (y + height), 0.0D, sprite.getMinU(), sprite.getMaxV());
      worldrenderer.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, sprite.getMaxU(), sprite.getMaxV());
      worldrenderer.addVertexWithUV((double)(x + width), (double)y, 0.0D, sprite.getMaxU(), sprite.getMinV());
      worldrenderer.addVertexWithUV((double)x, (double)y, 0.0D, sprite.getMinU(), sprite.getMinV());
      tessellator.draw();*/
    }
  }
}
