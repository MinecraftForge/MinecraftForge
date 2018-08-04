/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

public class ClientStateReset {
    
    /**
     * Resets all opengl and network state on the client side, during a crash
     */
    public static void resetAllClientState() {
        Minecraft mc = Minecraft.getMinecraft();
        
        int j;
        while ((j = GlStateManager.glGetError()) != 0) //clear existing gl errors
        {
            String s = GLU.gluErrorString(j);
            System.out.printf("########## GL ERROR ##########");
            System.out.printf("@ {}", s);
            System.out.printf("{}: {}", Integer.valueOf(j), s);
        }
        
        //This was made by Runemoro for their VanillaFix mod, original available at https://github.com/DimensionalDevelopment/VanillaFix/blob/master/src/main/java/org/dimdev/utils/GlUtil.java
        //I modified it a lot tho
        
        if (mc.entityRenderer.isShaderActive()) mc.entityRenderer.stopUseShader();
        
        //reset opengl matrix stacks
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        try {
            do GL11.glPopMatrix(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        try {
            do GL11.glPopMatrix(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        try {
            do GL11.glPopMatrix(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_COLOR);
        try {
            do GL11.glPopMatrix(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        GlStateManager.loadIdentity();
        
        //TODO: add these lines in 1.13 where lwjgl should be fixed
        
        /*
        try {
             do GL11.glPopAttrib(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        try {
            do GL11.glPopClientAttrib(); while (GlStateManager.glGetError() == 0);
        } catch (Throwable ignored) {}
        */
        
        GlStateManager.bindTexture(0);
        GlStateManager.disableTexture2D();
        
        GlStateManager.disableLighting();
        GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));
        for (int i = 0; i < 8; ++i) {
            GlStateManager.disableLight(i);
            GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_POSITION, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));

            if (i == 0) {
                GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
            } else {
                GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GlStateManager.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            }
        }
        
        GlStateManager.disableColorMaterial();
        GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        
        GlStateManager.disableDepth();
        GlStateManager.depthFunc(GL11.GL_LESS);
        GlStateManager.depthMask(true);
        
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);
        
        GlStateManager.disableFog();
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogDensity(1.0F);
        GlStateManager.setFogStart(0.0F);
        GlStateManager.setFogEnd(1.0F);
        GlStateManager.glFog(GL11.GL_FOG_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
        if (GLContext.getCapabilities().GL_NV_fog_distance) GlStateManager.glFogi(GL11.GL_FOG_MODE, 34140);
        
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        
        GlStateManager.disableColorLogic();
        GlStateManager.colorLogicOp(5379);
        
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.texGen(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 1000);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 1000);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, -1000);
        GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
        
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL11.GL_TEXTURE);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL13.GL_PREVIOUS);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_RGB, GL13.GL_CONSTANT);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_ALPHA, GL11.GL_TEXTURE);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_ALPHA, GL13.GL_PREVIOUS);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_ALPHA, GL13.GL_CONSTANT);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);
        GlStateManager.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, 1.0F);
        GlStateManager.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, 1.0F);
        
        GlStateManager.disableNormalize();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableRescaleNormal();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
        GlStateManager.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
    }
    
}
