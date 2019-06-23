package net.minecraftforge.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static com.mojang.blaze3d.platform.GlStateManager.DestFactor.ZERO;
import static com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE;
import static com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA;
import static org.lwjgl.opengl.GL11.*;

public class EarlyFontRenderer {

    private Optional<TextureManager> tm = Optional.empty();
    private final PreloadedTexture pt;
    private final ResourceLocation fontTexture;
    private final int char_height = 8;
    private final int row_height = 9;
    private final byte[] char_widths = new byte[]{4, 2, 4, 6, 6, 6, 6, 2, 4, 4, 4, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 4, 2, 4, 7, 9};
    private final int[] char_positions_x = new int[96], char_positions_y = new int[96];

    private static final EarlyFontRenderer INSTANCE = new EarlyFontRenderer(new ResourceLocation("textures/font/ascii.png"));

    public static EarlyFontRenderer get(){
        return INSTANCE;
    }

    public EarlyFontRenderer(ResourceLocation rl){
        this.fontTexture = rl;
        this.pt = new PreloadedTexture(fontTexture);
        for(int i = 0; i < 96; i++){
            char_positions_x[i] = 8 * (i % 16);
            char_positions_y[i] = 8 * (i / 16 + 2);
        }
    }

    public void drawString(int x, int y, String s, int color){
       float red = ((color>>16)&0xFF)/255.f, green = ((color>>8)&0xFF)/255.f, blue = ((color)&0xFF)/255.f, alpha = ((color>>24)&0xFF)/255.f;

        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        Tessellator tess = Tessellator.getInstance();

        GlStateManager.bindTexture(pt.getGlTextureId());
        BufferBuilder bb = tess.getBuffer();
        bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        int sx = x;
        for(int i = 0; i < s.length(); i++){
            char c;
            switch (c = s.charAt(i)){
                case '\n':
                    x = sx;
                    y += row_height;
                    break;
                case '\t':
                    int tab_width = char_widths[' ' - 32]*4;
                    x = (x+tab_width-1)/tab_width * tab_width; //round up to nearest multiple
                    break;
                default:
                    if(c < 32 | c >= 127)
                        c = '?';
                    int cx = char_positions_x[c - 32];
                    int cy = char_positions_y[c - 32];
                    this.blitWithColor(x, y, cx, cy, char_widths[c - 32], char_height, red, green, blue, alpha, bb);
                    x += char_widths[c - 32];
                    break;
            }
        }
        tess.draw();
    }

    private void blitWithColor(int x, int y, int tx, int ty, int tw, int th, float red, float green, float blue, float alpha, BufferBuilder buffer){
        buffer.pos((double)(x), (double)y, 0.0D).tex(tx/128., ty/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x), (double)(y+th), 0.0D).tex(tx/128., (ty+th)/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x+tw), (double)(y+th), 0.0D).tex((tx+tw)/128., (ty+th)/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x+tw), (double)y, 0.0D).tex((tx+tw)/128., ty/128.).color(red, green, blue, alpha).endVertex();
    }

    public void loadEarlyTexture() {
        try {
            this.pt.loadTexture(new FallbackResourceManager(ResourcePackType.CLIENT_RESOURCES)); //method is @NonNull so we pass something else in
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //from ResourceLoadProgressGui
    public static class PreloadedTexture extends SimpleTexture {
        public PreloadedTexture(ResourceLocation rl) {
            super(rl);
        }

        protected SimpleTexture.TextureData func_215246_b(IResourceManager unused) {
            Minecraft minecraft = Minecraft.getInstance();
            VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();

            try (InputStream inputstream = vanillapack.getResourceStream(ResourcePackType.CLIENT_RESOURCES, textureLocation)) {
                return new SimpleTexture.TextureData(null, NativeImage.read(inputstream));
            } catch (IOException ioexception) {
                return new SimpleTexture.TextureData(ioexception);
            }
        }
    }

}
