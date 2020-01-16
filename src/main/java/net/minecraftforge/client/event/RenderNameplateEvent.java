package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
 * {@link #matrixStack} contains the matrix stack instance involved in rendering the name plate/tag. This cannot
 * be changed by mods.<br>
 * {@link #renderTypeBuffer} contains the render type buffer instance involved in rendering the name plate/tag.
 * This cannot be changed by mods.<br>
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
    private final MatrixStack matrixStack;
    private final IRenderTypeBuffer renderTypeBuffer;

    public RenderNameplateEvent(Entity entity, String content, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer)
    {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
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
}