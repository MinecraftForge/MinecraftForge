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
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private static final Random r = new Random();

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
        boolean bold=false, italic=false, underlined=false, strokentrhough=false, obfuscated=false;

        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);

        Tessellator tess = Tessellator.getInstance();

        GlStateManager.bindTexture(pt.getGlTextureId());
        BufferBuilder bb = tess.getBuffer();
        bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        List<Line> ll = new LinkedList<>();
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
                case '§':
                    if(1+i >= s.length()) break;
                    c = s.charAt(i+1);
                    TextFormatting tf = TextFormatting.fromFormattingCode(c);
                    if(tf != null){
                        if(tf.getColor()!=null){
                            red = ((tf.getColor()>>16)&0xFF)/255.f;
                            green = ((tf.getColor()>>8)&0xFF)/255.f;
                            blue = ((tf.getColor())&0xFF)/255.f;
                            bold = underlined = strokentrhough = italic = obfuscated = false;
                        }else switch (tf){
                            case BOLD:
                                bold = true;
                                break;
                            case ITALIC:
                                italic = true;
                                break;
                            case OBFUSCATED:
                                obfuscated = true;
                                break;
                            case STRIKETHROUGH:
                                strokentrhough = true;
                                break;
                            case UNDERLINE:
                                underlined = true;
                                break;
                            case RESET:
                                red = ((color>>16)&0xFF)/255.f;
                                green = ((color>>8)&0xFF)/255.f;
                                blue = ((color)&0xFF)/255.f;
                                bold = underlined = strokentrhough = italic = obfuscated = false;
                                break;
                        }
                        i++;
                        break;
                    }//else fall through
                default:
                    if(c < 32 | c >= 127)
                        c = '?';
                    if(obfuscated){
                        int cwc = char_widths[c-32];
                        c = (char) (r.ints(0,95).filter(v->char_widths[v]==cwc).findAny().orElse(0)+32);
                    }
                    int cx = char_positions_x[c - 32];
                    int cy = char_positions_y[c - 32];
                    int boldWidth = char_widths[c - 32] + (bold?1:0);
                    this.blitWithColor(x, y, cx, cy, char_widths[c - 32], char_height, red, green, blue, alpha, italic, bb);
                    if(bold)
                        this.blitWithColor(x+1, y, cx, cy, char_widths[c - 32], char_height, red, green, blue, alpha, italic, bb);
                    if(underlined)
                        ll.add(new Line(x,y+8,boldWidth, 1, red, green, blue, alpha));
                    if(strokentrhough)
                        ll.add(new Line(x,y+3.5,boldWidth, 1, red, green, blue, alpha));
                    x += boldWidth;
                    break;
            }
        }
        tess.draw();
        if(!ll.isEmpty()) {
            GlStateManager.disableTexture();
            bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            for (Line l : ll) {
                bb.pos(l.x,l.y,0).color(l.r,l.g,l.b,l.a).endVertex();
                bb.pos(l.x,l.y+l.h,0).color(l.r,l.g,l.b,l.a).endVertex();
                bb.pos(l.x+l.w,l.y+l.h,0).color(l.r,l.g,l.b,l.a).endVertex();
                bb.pos(l.x+l.w,l.y,0).color(l.r,l.g,l.b,l.a).endVertex();
            }
            tess.draw();
            GlStateManager.enableTexture();
        }
    }

    private void blitWithColor(int x, int y, int tx, int ty, int tw, int th, float red, float green, float blue, float alpha, boolean italic, BufferBuilder buffer){
        buffer.pos((double)(x+(italic?1:0)), (double)y, 0.0D).tex(tx/128., ty/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x+(italic?-1:0)), (double)(y+th), 0.0D).tex(tx/128., (ty+th)/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x+tw+(italic?-1:0)), (double)(y+th), 0.0D).tex((tx+tw)/128., (ty+th)/128.).color(red, green, blue, alpha).endVertex();
        buffer.pos((double)(x+tw+(italic?1:0)), (double)y, 0.0D).tex((tx+tw)/128., ty/128.).color(red, green, blue, alpha).endVertex();
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

    private static class Line{
        double x, y, w, h;
        float r, g, b, a;

        public Line(double x, double y, double w, double h, float r, float g, float b, float a) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

}
