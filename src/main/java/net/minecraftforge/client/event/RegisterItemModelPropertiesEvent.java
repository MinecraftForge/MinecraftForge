package net.minecraftforge.client.event;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom item properties used in model predicates.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public abstract class RegisterItemModelPropertiesEvent extends Event implements IModBusEvent
{

    @ApiStatus.Internal
    protected RegisterItemModelPropertiesEvent() {}

    /**
     * Allows users to register custom model properties for the "minecraft:select" model type.
     * In the json you can specify several cases and a fallback.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see SelectItemModelProperty
     * @see net.minecraft.client.renderer.item.SelectItemModel
     */
    public static class Select extends RegisterItemModelPropertiesEvent
    {

        /**
         * Register a {@linkplain SelectItemModelProperty.Type select property type} with a specific id.
         */
        public void register(ResourceLocation id, SelectItemModelProperty.Type<?, ?> codec) {
            SelectItemModelProperties.register(id, codec);
        }
    }

    /**
     * Allows users to register custom boolean model properties for the "minecraft:condition" model type.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see ConditionalItemModelProperty
     * @see net.minecraft.client.renderer.item.ConditionalItemModel
     */
    public static class Conditional extends RegisterItemModelPropertiesEvent
    {

        /**
         * Register a {@linkplain ConditionalItemModelProperty conditional property} {@linkplain MapCodec map codec} with a specific id.
         */
        public void register(ResourceLocation id, MapCodec<? extends ConditionalItemModelProperty> codec) {
            ConditionalItemModelProperties.register(id, codec);
        }
    }

    /**
     * Allows users to register custom numerical model properties for the "minecraft:range_dispatch" model type.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see RangeSelectItemModelProperty
     * @see net.minecraft.client.renderer.item.RangeSelectItemModel
     */
    public static class RangeSelect extends RegisterItemModelPropertiesEvent
    {

        /**
         * Register an {@linkplain RangeSelectItemModelProperty range select property} {@linkplain MapCodec map codec} with a specific id.
         */
        public void register(ResourceLocation id, MapCodec<? extends RangeSelectItemModelProperty> codec) {
            RangeSelectItemModelProperties.register(id, codec);
        }
    }
}
