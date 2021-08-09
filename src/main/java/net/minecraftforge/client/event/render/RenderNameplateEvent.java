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

package net.minecraftforge.client.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before an entity renderer renders the nameplate of an entity.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and  {@linkplain HasResult has a result}. <br/>
 * <ul>
 *     <li><b>ALLOW</b> - the nameplate will be forcibly rendered</li>
 *     <li><b>DEFAULT</b> - the vanilla logic will be used</li>
 *     <li><b>DENY</b> - the nameplate will not be rendered</li>
 * </ul>
 * </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see EntityRenderer
 */
@Event.HasResult
public class RenderNameplateEvent extends EntityEvent
{
    private Component nameplateContent;
    private final Component originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final float partialTick;
    
    public RenderNameplateEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int packedLight, float partialTicks)
    {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.poseStack = poseStack;
        this.bufferSource = renderTypeBuffer;
        this.packedLight = packedLight;
        this.partialTick = partialTicks;
    }

    /**
     * Sets the new text on the nameplate
     *
     * @param contents the new text
     */
    public void setContent(Component contents)
    {
        this.nameplateContent = contents;
    }

    /**
     * {@return the text on the nameplate that will be rendered, if the event is not {@link Result#DENY DENIED}}
     */
    public Component getContent()
    {
        return this.nameplateContent;
    }

    /**
     * {@return the original text on the nameplate}
     */
    public Component getOriginalContent()
    {
        return this.originalContent;
    }

    /**
     * {@return the entity renderer rendering the nameplate}
     */
    public EntityRenderer<?> getEntityRenderer()
    {
        return this.entityRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return this.poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getBufferSource()
    {
        return this.bufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see net.minecraft.client.renderer.LightTexture
     */
    public int getPackedLight()
    {
        return this.packedLight;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return this.partialTick;
    }
}
