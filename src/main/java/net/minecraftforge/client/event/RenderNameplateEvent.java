package net.minecraftforge.client.event;

import com.google.common.base.Strings;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * RenderNameplateEvent is fired whenever the entity renderer attempts to render a name plate/tag of an entity.
 * <br>
 * {@link #entity} contains the entity in which the plate is being rendered to. This cannot be changed by mods.<br>
 * {@link #nameplateContent} contains the content being rendered on the name plate/tag. This can be changed by mods.<br>
 * {@link #originalContent} contains the original content being rendered on the name plate/tag. This cannot be changed by mods.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the entity name plate/tag will not be rendered.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class RenderNameplateEvent extends Event {

    private final Entity entity;
    private String nameplateContent;
    private final String originalContent;

    public RenderNameplateEvent(Entity entity, String content){
        this.entity = entity;
        this.setContent(content);
        this.originalContent = Strings.nullToEmpty(content);
    }

    /**
     * The entity in which the name plate/tag is being rendered to
     */
    public Entity getEntity(){
        return this.entity;
    }

    /**
     * Sets the content that is to be rendered on the name plate/tag
     */
    public void setContent(String contents){
        this.nameplateContent = Strings.nullToEmpty(contents);
    }

    /**
     * The content being rendered on the name plate/tag
     */
    public String getContent(){
        return this.nameplateContent;
    }

    /**
     * The original content being rendered on the name plate/tag
     */
    public String getOriginalContent(){
        return this.nameplateContent;
    }

}