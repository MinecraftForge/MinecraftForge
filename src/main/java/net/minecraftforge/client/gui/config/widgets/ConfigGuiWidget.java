/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.config.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract class which describes an input widget that is displayed in a config gui screen.
 * Consider this a mini gui screen that is displayed next to a label of the config entry.
 */
public abstract class ConfigGuiWidget implements ContainerEventHandler, NarratableEntry
{
    @Nullable
    private GuiEventListener focused;
    @Nullable
    private NarratableEntry lastNarratable;
    private boolean dragging;
    @NotNull
    private final ForgeConfigSpec.ValueSpec spec;
    @NotNull
    private final ValueManager valueManager;

    protected ConfigGuiWidget(@NotNull final ForgeConfigSpec.ValueSpec spec, final @NotNull ValueManager valueManager) {
        this.spec = spec;
        this.valueManager = valueManager;
    }

    @Override
    public boolean isDragging()
    {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean isDragging)
    {
        this.dragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused()
    {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener newFocus)
    {
        this.focused = newFocus;
    }

    @Override
    @SuppressWarnings("DuplicatedCode") //Copy of vanila we need it to propagate through the hierarchy
    public void updateNarration(@NotNull NarrationElementOutput output)
    {
        List<? extends NarratableEntry> narratables = this.narratables();
        Screen.NarratableSearchResult searchResult = Screen.findNarratableWidget(narratables, this.lastNarratable);
        if (searchResult != null)
        {
            if (searchResult.priority.isTerminal())
            {
                this.lastNarratable = searchResult.entry;
            }

            if (narratables.size() > 1)
            {
                output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list",
                        searchResult.index + 1, narratables.size()));
                if (searchResult.priority == NarratableEntry.NarrationPriority.FOCUSED)
                {
                    output.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                }
            }

            searchResult.entry.updateNarration(output.nest());
        }
    }

    @NotNull
    public ForgeConfigSpec.ValueSpec getSpec()
    {
        return spec;
    }

    @NotNull
    public ValueManager getValueManager()
    {
        return valueManager;
    }

    /**
     * Indicates to scroll list if this widget is currently valid, aka if the value currently entered or represented
     * by the state of this widget is valid for the configuration entry it represents.
     * Should check things like range etc.
     *
     * @return True for a valid entry, false for not.
     */
    public abstract boolean isValid();

    /**
     * Invoked to set the value of the widget.
     *
     * @param value The new value to set the widget to.
     */
    protected abstract void setValue(@NotNull Object value);

    /**
     * The current value for the configuration entry that this widgets value or state represents.
     *
     * @return The current value.
     */
    public abstract Object getValue();

    /**
     * The narratable entries for this widget.
     * Since this class is not actually a gui component, screen etc., we need a way to handle
     * the narration entries for it. So this controls the narration entries for this widget.
     *
     * @return The list of narratable entries.
     */
    public abstract List<? extends NarratableEntry> narratables();

    /**
     * Invoked to render this widget in the config entry.
     * This call also handles positioning the widget since it might be contained in a scrollable object.
     *
     * @param guiGraphics The graphics handler.
     * @param top         The top offset to render from.
     * @param left        The left offset to render from.
     * @param maxWidth    The maximal width that the widget can render to.
     * @param maxHeight   The maximal height that the widget can render to.
     * @param mouseX      The current mouse x position.
     * @param mouseY      The current mouse y position.
     * @param isHovered   Whether the mouse is currently hovering over this widget.
     * @param partialTick The partial tick time.
     */
    public abstract void render(final GuiGraphics guiGraphics, final int top, final int left, final int maxWidth,
                                final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                                final float partialTick);

    /**
     * The text component that describes the current error state of this widget.
     *
     * @return The error text component.
     * @throws IllegalStateException if the widget is valid.
     */
    public Component getError() {
        return this.spec.getError(getValue());
    }

    /**
     * Invoked to reset the state of this widget to the default value.
     * Implementers should use the {@link ValueManager} given to the widget factory to reset the value.
     */
    public void resetToDefault() {
        this.setValue(this.getSpec().getDefault());
        this.valueManager.setter().accept(getValue());
    }

    /**
     * Invoked to reset the state of this widget to the initial value when the screen was opened.
     * Implementers should use the {@link ValueManager} given to the widget factory to reset the value.
     */
    public void resetToInitial() {
        this.setValue(this.getValueManager().initial().get());
        this.valueManager.setter().accept(getValue());
    }

    /**
     * Configures the priority for the narration of this widget.
     * By default, this widget will get priority when it is overridden.
     * <p>
     * Override this method to change this behaviour.
     *
     * @return The narration priority.
     */
    @Override
    public @NotNull NarrationPriority narrationPriority()
    {
        return NarrationPriority.HOVERED;
    }

    /**
     * The tooltip for this widget.
     *
     * @return The tooltip, may be null if none is available.
     */
    public abstract @Nullable Tooltip getTooltip();

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.BooleanValue}.
     */
    public static class BooleanWidget extends ConfigGuiWidget
    {

        private static final ResourceLocation CHECKBOX_BLOCKED_TEXTURE = new ResourceLocation("forge", "textures/gui/checkbox_blocked.png");

        /**
         * The default factory to use for a boolean widget.
         */
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.BooleanValue)
            {
                return new BooleanWidget(valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not a boolean value!");
        };

        private final Checkbox checkbox;

        public BooleanWidget(final ForgeConfigSpec.ValueSpec spec, ValueManager valueManager, SpecificationData specificationData, Component name)
        {
            super(spec, valueManager);
            this.checkbox = new Checkbox(0,0,20, 20, name, (Boolean) valueManager.getter().get(), false) {
                @Override
                public void onPress()
                {
                    super.onPress();
                    valueManager.setter().accept(selected());
                }

                @NotNull
                @Override
                protected ResourceLocation getTexture()
                {
                    return CHECKBOX_BLOCKED_TEXTURE;
                }
            };

            this.checkbox.active = !specificationData.isSynced();
        }

        @Override
        public boolean isValid()
        {
            return true;
        }

        @Override
        protected void setValue(@NotNull final Object value)
        {
            if (this.checkbox.selected()  != (Boolean) value) {
                this.checkbox.onPress();
            }
        }

        @Override
        public Object getValue()
        {
            return this.checkbox.selected();
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.checkbox);
        }

        @Override
        public void render(final GuiGraphics guiGraphics, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.checkbox.setWidth(maxWidth);
            this.checkbox.setHeight(maxHeight);
            this.checkbox.setX(left);
            this.checkbox.setY(top);
            this.checkbox.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        public @Nullable Tooltip getTooltip()
        {
            return this.checkbox.getTooltip();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.checkbox);
        }
    }


    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.EnumValue}.
     */
    public static class EnumWidget<T extends Enum<T>> extends ConfigGuiWidget
    {
        /**
         * The default factory to use for a boolean widget.
         */
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.EnumValue<?> enumConfigValue)
            {
                return new EnumWidget<>(enumConfigValue, valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not an enum value!");
        };

        private final CycleButton<T> enumButton;

        @SuppressWarnings("unchecked")
        public EnumWidget(ForgeConfigSpec.EnumValue<T> value, final ForgeConfigSpec.ValueSpec spec,
                          final ValueManager valueManager, SpecificationData specificationData, Component name)
        {
            super(spec, valueManager);
            this.enumButton = CycleButton.<T>builder(t -> Component.literal(t.name()))
                                         .withValues(List.of(value.getEnumClass().getEnumConstants()))
                                         .displayOnlyValue()
                                         .withCustomNarration(CycleButton::createDefaultNarrationMessage)
                                         .withInitialValue((T) valueManager.getter().get())
                                         .create(0, 0, 68, 20, name, (button, newValue) -> valueManager.setter()
                                                                                                           .accept(newValue));

            this.enumButton.active = !specificationData.isSynced();
        }

        @Override
        public boolean isValid()
        {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void setValue(@NotNull final Object value)
        {
            this.enumButton.setValue((T) value);
        }

        @Override
        public Object getValue()
        {
            return this.enumButton.getValue();
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.enumButton);
        }

        @Override
        public void render(final GuiGraphics guiGraphics, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.enumButton.setWidth(maxWidth);
            this.enumButton.setHeight(maxHeight);
            this.enumButton.setX(left);
            this.enumButton.setY(top);
            this.enumButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @Nullable Tooltip getTooltip()
        {
            return this.enumButton.getTooltip();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.enumButton);
        }
    }

    /**
     * Defines a widget which uses a vanilla {@link EditBox} to edit the value.
     */
    public static class EditBoxBasedWidget extends ConfigGuiWidget {
        public static ConfigGuiWidgetFactory SIMPLE = (value, valueSpec, valueManager, spec, name) ->
        {
            try
            {
                // Try to cast it to something that we can handle to verify it is of the right type.
                // We should really just provide a spec for it but meh for now this suffices.
                return new EditBoxBasedWidget(valueSpec, valueManager, spec, name);
            }
            catch (ClassCastException ex)
            {
                throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not a Text value!", ex);
            }
        };

        private final EditBox editBox;

        protected EditBoxBasedWidget(final ForgeConfigSpec.@NotNull ValueSpec spec, final @NotNull ValueManager valueManager, @NotNull SpecificationData specificationData, @NotNull final Component name)
        {
            super(spec, valueManager);
            this.editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 122, 20, name)
            {
                @Override
                public int getBorderColor()
                {
                    return isValid() ? super.getBorderColor() : 0x55FF0000;
                }

                @Override
                public int getBorderColorFocused()
                {
                    return isValid() ? super.getBorderColor() : 0xFFFF0000;
                }
            };
            this.editBox.setValue(valueManager.getter().get().toString());
            this.editBox.setResponder(this::onInput);
            this.editBox.active = !specificationData.isSynced();
            this.editBox.setEditable(this.editBox.active);
        }

        public EditBox getEditBox()
        {
            return editBox;
        }

        @Override
        public boolean isValid()
        {
            return isValid(this.editBox.getValue());
        }

        @Override
        public void render(final GuiGraphics guiGraphics, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.editBox.setWidth(maxWidth - 4);
            this.editBox.setHeight(maxHeight);
            this.editBox.setX(left + 2);
            this.editBox.setY(top);
            this.editBox.render(guiGraphics, mouseX, mouseY, partialTick);
        }


        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.editBox);
        }

        public @Nullable Tooltip getTooltip()
        {
            return null;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.editBox);
        }

        @Override
        public Object getValue()
        {
            return this.getEditBox().getValue();
        }

        @Override
        protected void setValue(@NotNull final Object value)
        {
            this.editBox.setValue(value.toString());
        }

        protected boolean isValid(final String value) {
            return this.getSpec().test(value);
        }

        protected void onInput(final String input)
        {
            if (isValid(input))
            {
                getValueManager().setter().accept(input);
            }
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link Number} based {@link ForgeConfigSpec.ConfigValue}.
     * Uses a text box for input.
     *
     * @param <T> The type of the number.
     */
    public static class NumberWidget<T extends Number> extends EditBoxBasedWidget
    {
        public static ConfigGuiWidgetFactory INTEGER = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.IntValue)
            {
                return new NumberWidget<>(valueSpec, valueManager, spec, name, Integer::parseInt);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not an Integer value!");
        };

        public static ConfigGuiWidgetFactory LONG = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.LongValue)
            {
                return new NumberWidget<>(valueSpec, valueManager, spec, name, Long::parseLong);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not a Long value!");
        };

        public static ConfigGuiWidgetFactory DOUBLE = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.DoubleValue)
            {
                return new NumberWidget<>(valueSpec, valueManager, spec, name, Double::parseDouble);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".", value.getPath()) + " is not a Double value!");
        };


        private final Function<String, T> parser;

        public NumberWidget(ForgeConfigSpec.ValueSpec spec,
                            ValueManager valueManager, SpecificationData specificationData, Component name,
                            final Function<String, T> parser)
        {
            super(spec, valueManager, specificationData, name);
            this.parser = parser;
        }

        @Override
        protected void onInput(final String input)
        {
            if (isValid(input))
            {
                getValueManager().setter().accept(parser.apply(input));
            }
        }

        @Override
        public Object getValue()
        {
            try
            {
                return parser.apply(this.getEditBox().getValue());
            }
            catch (NumberFormatException e)
            {
                return super.getValue();
            }
        }

        protected boolean isValid(final String value)
        {
            try
            {
                return isValid(parser.apply(value));
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        private boolean isValid(T value)
        {
            return this.getSpec().test(value);
        }
    }
}
