/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.event;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * RenderNameplateEvent is fired whenever the entity renderer attempts to render a name plate/tag of an entity.
 * <br>
 * {@link #nameplateContent} contains the content being rendered on the name plate/tag. This can be changed by mods.<br>
 * {@link #originalContent} contains the original content being rendered on the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #entityRenderer} contains the entity renderer instance that renders the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #matrixStack} contains the matrix stack instance involved in rendering the name plate/tag. This cannot
 * be changed by mods.<br>
 * {@link #renderTypeBuffer} contains the render type buffer instance involved in rendering the name plate/tag.
 * This cannot be changed by mods.<br>
 * {@link #packedLight} contains the sky and block light values used in rendering the name plate/tag.<br>
 * <br>
 * This event has a result. {@link HasResult}. <br>
 * ALLOW will force-render name plate/tag, DEFAULT will ignore the hook and continue using the vanilla check
 * & DENY will prevent name plate/tag from rendering<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Event.HasResult
public class RenderNameplateEvent extends EntityEvent
{

    private String nameplateContent;
    private final String originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final MatrixStack matrixStack;
    private final IRenderTypeBuffer renderTypeBuffer;
    private final int packedLight;

    @Deprecated //TODO 1.16: upon removal, also remove @Nullable on getEntityRenderer(), and update its javadoc
    public RenderNameplateEvent(Entity entity, String content, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer)
    {
        this(entity, content, null, matrixStack, renderTypeBuffer, 0);
    }

    public RenderNameplateEvent(Entity entity, String content, EntityRenderer<?> entityRenderer, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight)
    {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
        this.packedLight = packedLight;
    }

    /**
     * Sets the content that is to be rendered on the name plate/tag
     */
    public void setContent(String contents)
    {
        this.nameplateContent = contents;
    }

    /**
     * The content being rendered on the name plate/tag
     */
    public String getContent()
    {
        return this.nameplateContent;
    }

    /**
     * The original content being rendered on the name plate/tag
     */
    public String getOriginalContent()
    {
        return this.originalContent;
    }

    /**
     * The entity renderer that renders the name plate/tag, if it was provided
     */
    @Nullable
    public EntityRenderer<?> getEntityRenderer()
    {
        return this.entityRenderer;
    }

    /**
     * The matrix stack used during the rendering of the name plate/tag
     */
    public MatrixStack getMatrixStack()
    {
        return this.matrixStack;
    }

    /**
     * The render type buffer used during the rendering of the name plate/tag
     */
    public IRenderTypeBuffer getRenderTypeBuffer()
    {
        return this.renderTypeBuffer;
    }

    /**
     * The packed values of sky and block light used during the rendering of the name plate/tag
     */
    public int getPackedLight()
    {
        return this.packedLight;
    }
}
