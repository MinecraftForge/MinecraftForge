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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

@SuppressWarnings("rawtypes")
public abstract class LivingRendererEvent extends Event implements IModBusEvent{

	private final LivingRenderer renderer;
    
    private LivingRendererEvent(LivingRenderer renderer) {
        this.renderer = renderer;
    }
    
    public LivingRenderer getRenderer() {
        return renderer;
    }
    
    public static class RendererCreationEvent extends LivingRendererEvent {
    	public RendererCreationEvent(LivingRenderer renderer) {
    		super(renderer);
    	}
    }
    
    public static class LayerEvent extends LivingRendererEvent {
    
        private final LayerRenderer layer;
    	
    	private LayerEvent(LivingRenderer renderer, LayerRenderer layer) {
    		super(renderer);
            this.layer = layer;
    	}
    	
        @Override
        public boolean isCancelable() {
            return true;
        }
    	
        public LayerRenderer getLayer() {
            return layer;
        }
    	
	    public static final class Add extends LayerEvent {
	        public Add(LivingRenderer renderer, LayerRenderer layer) {
	            super(renderer, layer);
	        }
	    }
    }
    
}
