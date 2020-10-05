/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public abstract class LivingRendererEvent<E extends LivingEntity, M extends EntityModel<E>> extends Event implements IModBusEvent{

    private final LivingRenderer<E, M> renderer;
    
    private LivingRendererEvent(LivingRenderer<E,M> renderer) {
        this.renderer = renderer;
    }
    
    public LivingRenderer<E, M> getRenderer() {
        return renderer;
    }
    
    public static class RendererCreationEvent<E extends LivingEntity, M extends EntityModel<E>> extends LivingRendererEvent<E, M> {
    	public RendererCreationEvent(LivingRenderer<E, M> renderer) {
    		super(renderer);
    	}
    }
    
    public static class LayerEvent<E extends LivingEntity, M extends EntityModel<E>> extends LivingRendererEvent<E, M> {
    
        private final LayerRenderer<E, M> layer;
    	
    	private LayerEvent(LivingRenderer<E, M> renderer, LayerRenderer<E, M> layer) {
    		super(renderer);
            this.layer = layer;
    	}
    	
        @Override
        public boolean isCancelable() {
            return true;
        }
    	
        public LayerRenderer<E, M> getLayer() {
            return layer;
        }
    	
	    public static final class Add<E extends LivingEntity, M extends EntityModel<E>> extends LayerEvent<E, M> {
	        public Add(LivingRenderer<E, M> renderer, LayerRenderer<E, M> layer) {
	            super(renderer, layer);
	        }
	    }
	    
	    public static final class Remove<E extends LivingEntity, M extends EntityModel<E>> extends LayerEvent<E, M> {
	        public Remove(LivingRenderer<E, M> renderer, LayerRenderer<E, M> layer) {
	            super(renderer, layer);
	        }
	    }
    }
    
}
