/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

public class ArmorProperties {
	public int damageRemove = 0;
	public boolean allowRegularComputation = false;
	
	public ArmorProperties () {
		
	}
	
	public ArmorProperties (int damageRemove, boolean allowRegularCompuation) {
		this.damageRemove = damageRemove;
		this.allowRegularComputation = allowRegularCompuation;
	}
}
