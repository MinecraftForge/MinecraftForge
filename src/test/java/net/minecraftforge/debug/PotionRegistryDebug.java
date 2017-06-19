package net.minecraftforge.debug;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.GameData;

import java.util.Random;

@Mod(modid = PotionRegistryDebug.MODID, name = "ForgePotionRegistry", version = "1.0", acceptableRemoteVersions = "*")
public class PotionRegistryDebug
{
    public static final String MODID = "forgepotionregistry";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Potion forge = new PotionForge(new ResourceLocation(ForgeVersion.MOD_ID, "forge"), false, 0xff00ff).setRegistryName(new ResourceLocation(ForgeVersion.MOD_ID, "forge")); // test automatic id distribution
        Potion forgy = new PotionForge(new ResourceLocation(ForgeVersion.MOD_ID, "forgy"), true, 0x00ff00).setRegistryName(new ResourceLocation(ForgeVersion.MOD_ID, "forgy")); // test that ids above 127 work
        ForgeRegistries.POTIONS.register(forge);
        Potion.REGISTRY.register(200, forgy.getRegistryName(), forgy);

        Random rand = new Random();
        TIntSet taken = new TIntHashSet(100);
        int ra = rand.nextInt(100) + 100;
        taken.add(ra);

        // a new potion with a random id so that forge has to remap it
        //new PotionForge(ra, new ResourceLocation(ForgeModContainer.MOD_ID, "realRandomPotion"), false, 0x0000ff);

        for (int i = 0; i < 20; i++)
        {
            int r = rand.nextInt(200) + 35;
            while (taken.contains(r))
                r = rand.nextInt(200) + 35;
            //r = 32+i;
            taken.add(r);
            // this potions will most likely not have the same IDs between server and client.
            // The forge handshake on connect should fix this.
            //new PotionForge(new ResourceLocation(ForgeModContainer.MOD_ID, "randomPotion" + r), false, 0xff00ff);
        }
    }

    protected class PotionForge extends Potion
    {
        protected PotionForge(ResourceLocation location, boolean badEffect, int potionColor)
        {
            super(badEffect, potionColor);
            setPotionName("potion." + location.getResourcePath());
        }

        @Override
        public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
        {
            Potion potion = effect.getPotion();

            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_0");

            x += 6;
            y += 7;

            int width = 18;
            int height = width;

            float r = (float) (potion.getLiquidColor() >> 24 & 255) / 255.0F;
            float g = (float) (potion.getLiquidColor() >> 16 & 255) / 255.0F;
            float b = (float) (potion.getLiquidColor() >> 8 & 255) / 255.0F;
            float a = (float) (potion.getLiquidColor() & 255) / 255.0F;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(7, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color(r, g, b, a);
            buf.pos((double) x, (double) (y + height), 0.0D).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) (y + height), 0.0D).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) y, 0.0D).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
            buf.pos((double) x, (double) y, 0.0D).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
            tessellator.draw();
        }

        @Override
        public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
        {
            Potion potion = effect.getPotion();

            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/tnt_side");

            x += 3;
            y += 3;

            int width = 18;
            int height = width;

            float r = (float) (potion.getLiquidColor() >> 24 & 255) / 255.0F;
            float g = (float) (potion.getLiquidColor() >> 16 & 255) / 255.0F;
            float b = (float) (potion.getLiquidColor() >> 8 & 255) / 255.0F;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(7, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color(r, g, b, alpha);
            buf.pos((double) x, (double) (y + height), 0.0D).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) (y + height), 0.0D).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) y, 0.0D).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
            buf.pos((double) x, (double) y, 0.0D).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
            tessellator.draw();
        }
    }
}
