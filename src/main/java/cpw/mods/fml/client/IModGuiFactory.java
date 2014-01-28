package cpw.mods.fml.client;

import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public interface IModGuiFactory {
    /**
     * Called when instantiated to initialize with the active minecraft instance.
     *
     * @param minecraftInstance the instance
     */
    public void initialize(Minecraft minecraftInstance);
    /**
     * Return the name of a class extending {@link GuiScreen}. This class will
     * be instantiated when the "config" button is pressed in the mod list. It will
     * have a single argument constructor - the "parent" screen, the same as all
     * Minecraft GUIs. The expected behaviour is that this screen will replace the
     * "mod list" screen completely, and will return to the mod list screen through
     * the parent link, once the appropriate action is taken from the config screen.
     *
     * A null from this method indicates that the mod does not provide a "config"
     * button GUI screen, and the config button will be hidden/disabled.
     *
     * This config GUI is anticipated to provide configuration to the mod in a friendly
     * visual way. It should not be abused to set internals such as IDs (they're gonna
     * keep disappearing anyway), but rather, interesting behaviours. This config GUI
     * is never run when a server game is running, and should be used to configure
     * desired behaviours that affect server state. Costs, mod game modes, stuff like that
     * can be changed here.
     *
     * @return A class that will be instantiated on clicks on the config button
     *  or null if no GUI is desired.
     */
    public Class<? extends GuiScreen> mainConfigGuiClass();


    /**
     * Return a list of the "runtime" categories this mod wishes to populate with
     * GUI elements.
     *
     * Runtime categories are created on demand and organized in a 'lite' tree format.
     * The parent represents the parent node in the tree. There is one special parent
     * 'Help' that will always list first, and is generally meant to provide Help type
     * content for mods. The remaining parents will sort alphabetically, though
     * this may change if there is a lot of alphabetic abuse. "AAA" is probably never a valid
     * category parent.
     *
     * Runtime configuration itself falls into two flavours: in-game help, which is
     * generally non interactive except for the text it wishes to show, and client-only
     * affecting behaviours. This would include things like toggling minimaps, or cheat modes
     * or anything NOT affecting the behaviour of the server. Please don't abuse this to
     * change the state of the server in any way, this is intended to behave identically
     * when the server is local or remote.
     *
     * @return the set of options this mod wishes to have available, or empty if none
     */
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories();

    /**
     * Return an instance of a {@link RuntimeOptionGuiHandler} that handles painting the
     * right hand side option screen for the specified {@link RuntimeOptionCategoryElement}.
     *
     * @param element The element we wish to paint for
     * @return The Handler for painting it
     */
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element);

    /**
     * Represents an option category and entry in the runtime gui options list.
     *
     * @author cpw
     *
     */
    public static class RuntimeOptionCategoryElement {
        public final String parent;
        public final String child;

        public RuntimeOptionCategoryElement(String parent, String child)
        {
            this.parent = parent;
            this.child = child;
        }
    }

    /**
     * Responsible for painting the mod specific section of runtime options GUI for a particular category
     *
     * @author cpw
     *
     */
    public interface RuntimeOptionGuiHandler {
        /**
         * Called to add widgets to the screen, such as buttons.
         * GUI identifier numbers should start at 100 and increase.
         * The callback will be through {@link #actionCallback(int)}
         *
         * @param x X
         * @param y Y
         * @param w width
         * @param h height
         */
        public void addWidgets(List<Gui> widgetList, int x, int y, int w, int h);

        /**
         * Called to paint the rectangle specified.
         * @param x X
         * @param y Y
         * @param w width
         * @param h height
         */
        public void paint(int x, int y, int w, int h);

        /**
         * Called if a widget with id >= 100 is fired.
         *
         * @param actionId the actionId of the firing widget
         */
        public void actionCallback(int actionId);

        /**
         * Called when this handler is about to go away (probably replaced by another one, or closing the
         * option screen)
         */
        public void close();
    }
}