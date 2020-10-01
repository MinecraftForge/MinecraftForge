package net.minecraftforge.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.gui.config.ControlCreator.Interactor;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Popup {@link ConfigScreen} that displays and modifies a {@link List}.
 *
 * @author Cadiboo
 */
public abstract class ListConfigScreen extends ConfigScreen {
    protected List value;

    public ListConfigScreen(Screen configScreen, ITextComponent title, List<?> value) {
        super(configScreen, title, new StringTextComponent("List"));
        this.value = value;
    }

    public abstract void onModified(List newValue);

    @Override
    protected ConfigElementList makeConfigElementList() {
        return new ListConfigElementList(this, field_230706_i_);
    }

    protected void setAndNotify(int index, Object newValue) {
        value.set(index, newValue);
        onModified(value);
    }

    protected void onAddRemove(int index) {
        // TODO: Don't take the lazy way out
        // Lazy way out
        // Whenever anything changes, recreate from scratch
        // Saves the headache of dealing with indexing of adds/removes
        field_230705_e_.remove(configElementList); // children.remove
        double scrollAmount = configElementList.func_230966_l_(); // getScrollAmount
        if (index == value.size() - 1)
            // If we're at the bottom, scroll even further because if it's an add
            // we want to scroll to the new bottom
            scrollAmount += this.configElementList.getItemHeight();
        configElementList = makeConfigElementList();
        configElementList.func_230932_a_(scrollAmount); // getScrollAmount
        field_230705_e_.add(configElementList); // children.add
    }

    public class ListConfigElementList extends ConfigElementList {

        public ListConfigElementList(ConfigScreen configScreen, Minecraft mcIn) {
            super(configScreen, mcIn);
            for (int i = 0; i < value.size(); i++) {
                Object item = value.get(i);
                func_230513_b_(createElement(i, item));
            }
        }

        protected ConfigElement createElement(int index, Object item) {
            Interactor<?> interactor = tryCreateInteractor(index, item);
            if (interactor == null) {
                ITextComponent title = new StringTextComponent(index + ": " + item + (item == null ? "" : " (" + item.getClass().getSimpleName() + ")"));
                return new ListItemConfigElement(title, index);
            } else {
                interactor.saveValue = newValue -> setAndNotify(index, newValue);
                ConfigElement configElement = new ListItemConfigElement(interactor.title, index);
                configElement.setMainWidget(interactor.control);
                return configElement;
            }
        }

        @Nullable
        protected Interactor<?> tryCreateInteractor(int index, Object item) {
            ITextComponent title = new StringTextComponent(Integer.toString(index));
            ControlCreator creator = configScreen.getControlCreator();
            Interactor<?> interactor = new Interactor<>(title, item);
            try {
                creator.createInteractionWidget(interactor);
            } catch (Exception e) {
                return null;
            }
            if (interactor.control == null)
                return null;
            return interactor;
        }

        /**
         * Element for an item inside a List.
         * See {@link ConfigElement} for documentation.
         */
        class ListItemConfigElement extends ConfigElement {

            public ListItemConfigElement(ITextComponent title, int index) {
                super(title, title, Collections.emptyList());
                Button addBelowButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("+"), b -> {
                    value.add(index + 1, ControlCreator.copyMutable(value.get(index)));
                    onModified(value);
                    onAddRemove(index + 1);
                });
                addBelowButton.setFGColor(ControlCreator.GREEN);
                Button removeButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("-"), b -> {
                    value.remove(index);
                    onModified(value);
                    onAddRemove(index);
                });
                removeButton.setFGColor(ControlCreator.RED);
                addWidget(addBelowButton);
                addWidget(removeButton);
            }
        }

    }

}
