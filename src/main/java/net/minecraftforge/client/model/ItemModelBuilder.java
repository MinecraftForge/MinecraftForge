package net.minecraftforge.client.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.util.ResourceLocation;

public class ItemModelBuilder extends BlockModelBuilder {
	
	protected List<ItemOverride> overrides = new ArrayList<>();
	
	public OverrideBuilder override() {
		return this.new OverrideBuilder();
	}
	
	public class OverrideBuilder {
		
		private ResourceLocation model;
		private final Map<ResourceLocation, Float> predicates = new LinkedHashMap<>();
		
		public OverrideBuilder model(ResourceLocation model) {
			this.model = model;
			return this;
		}
		
		public OverrideBuilder predicate(ResourceLocation key, float value) {
			this.predicates.put(key, value);
			return this;
		}
		
		public ItemModelBuilder build() {
			ItemModelBuilder.this.overrides.add(new ItemOverride(model, predicates));
			return ItemModelBuilder.this;
		}
	}

}
