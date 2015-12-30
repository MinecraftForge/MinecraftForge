/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.SelectValueEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.BooleanEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import static net.minecraftforge.common.ForgeModContainer.VERSION_CHECK_CAT;

/**
 * This is the base GuiConfig screen class that all the other Forge-specific config screens will be called from.
 * Since Forge has multiple config files I thought I would use that opportunity to show some of the ways
 * that the config GUI system can be extended to create custom config GUIs that have additional features
 * over the base functionality of just displaying Properties and ConfigCategories.
 *
 * The concepts implemented here are:
 * - using custom IConfigEntry objects to define child-screens that have specific Properties listed
 * - using custom IConfigEntry objects to define a dummy property that can be used to generate new ConfigCategory objects
 * - defining the configID string for a GuiConfig object so that the config changed events will be posted when that GuiConfig screen is closed
 *      (the configID string is optional; if it is not defined the config changed events will be posted when the top-most GuiConfig screen
 *      is closed, eg when the parent is null or is not an instance of GuiConfig)
 * - overriding the IConfigEntry.enabled() method to control the enabled state of one list entry based on the value of another entry
 * - overriding the IConfigEntry.onGuiClosed() method to perform custom actions when the screen that owns the entry is closed (in this
 *      case a new ConfigCategory is added to the Configuration object)
 *
 * The config file structure looks like this:
 *      forge.cfg (general settings all in one category)
 *      forgeChunkLoading.cfg
 *          - Forge (category)
 *          - defaults (category)
 *          - [optional mod override categories]...
 *
 * The GUI structure is this:
 *      Base Screen
 *          - General Settings (from forge.cfg)
 *          - Chunk Loader Settings (from forgeChunkLoading.cfg)
 *              - Defaults (these elements are listed directly on this screen)
 *              - Mod Overrides
 *                  - Add New Mod Override
 *                  - Mod1
 *                  - Mod2
 *                  - etc.
 *
 * Other things to check out:
 *      ForgeModContainer.syncConfig()
 *      ForgeModContainer.onConfigChanged()
 *      ForgeChunkManager.syncConfigDefaults()
 *      ForgeChunkManager.loadConfiguration()
 */
public class ForgeGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() { return ForgeConfigGui.class; }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) { return null; }

    public static class ForgeConfigGui extends GuiConfig
    {
        public ForgeConfigGui(GuiScreen parentScreen)
        {
            super(parentScreen, getConfigElements(), "Forge", false, false, I18n.format("forge.configgui.forgeConfigTitle"));
        }

        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            list.add(new DummyCategoryElement("forgeCfg", "forge.configgui.ctgy.forgeGeneralConfig", GeneralEntry.class));
            list.add(new DummyCategoryElement("forgeClientCfg", "forge.configgui.ctgy.forgeClientConfig", ClientEntry.class));
            list.add(new DummyCategoryElement("forgeChunkLoadingCfg", "forge.configgui.ctgy.forgeChunkLoadingConfig", ChunkLoaderEntry.class));
            list.add(new DummyCategoryElement("forgeVersionCheckCfg", "forge.configgui.ctgy.VersionCheckConfig", VersionCheckEntry.class));
            return list;
        }

        /**
         * This custom list entry provides the General Settings entry on the Minecraft Forge Configuration screen.
         * It extends the base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
         */
        public static class GeneralEntry extends CategoryEntry
        {
            public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                // This GuiConfig object specifies the configID of the object and as such will force-save when it is closed. The parent
                // GuiConfig object's entryList will also be refreshed to reflect the changes.
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(ForgeModContainer.getConfig().getCategory(Configuration.CATEGORY_GENERAL))).getChildElements(),
                        this.owningScreen.modID, Configuration.CATEGORY_GENERAL, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(ForgeModContainer.getConfig().toString()));
            }
        }

        /**
         * This custom list entry provides the Client only Settings entry on the Minecraft Forge Configuration screen.
         * It extends the base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
         */
        public static class ClientEntry extends CategoryEntry
        {
            public ClientEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                // This GuiConfig object specifies the configID of the object and as such will force-save when it is closed. The parent
                // GuiConfig object's entryList will also be refreshed to reflect the changes.
                return new GuiConfig(this.owningScreen,
                                     (new ConfigElement(ForgeModContainer.getConfig().getCategory(Configuration.CATEGORY_CLIENT))).getChildElements(),
                                     this.owningScreen.modID, Configuration.CATEGORY_CLIENT, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                                     this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                                     GuiConfig.getAbridgedConfigPath(ForgeModContainer.getConfig().toString()));
            }
        }

        /**
         * This custom list entry provides the Forge Chunk Manager Config entry on the Minecraft Forge Configuration screen.
         * It extends the base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
         */
        public static class ChunkLoaderEntry extends CategoryEntry
        {
            public ChunkLoaderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                List<IConfigElement> list = new ArrayList<IConfigElement>();

                list.add(new DummyCategoryElement("forgeChunkLoadingModCfg", "forge.configgui.ctgy.forgeChunkLoadingModConfig",
                        ModOverridesEntry.class));
                list.addAll((new ConfigElement(ForgeChunkManager.getDefaultsCategory())).getChildElements());

                // This GuiConfig object specifies the configID of the object and as such will force-save when it is closed. The parent
                // GuiConfig object's propertyList will also be refreshed to reflect the changes.
                return new GuiConfig(this.owningScreen, list, this.owningScreen.modID, "chunkLoader",
                        this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(ForgeChunkManager.getConfig().toString()),
                        I18n.format("forge.configgui.ctgy.forgeChunkLoadingConfig"));
            }
        }

        /**
         * This custom list entry provides the Forge Version Checking Config entry on the Minecraft Forge Configuration screen.
         * It extends the base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
         */
        public static class VersionCheckEntry extends CategoryEntry
        {
            public VersionCheckEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                ConfigCategory cfg = ForgeModContainer.getConfig().getCategory(VERSION_CHECK_CAT);
                Map<String, Property> values = new HashMap<String, Property>(cfg.getValues());
                values.remove("Global");

                Property global = ForgeModContainer.getConfig().get(VERSION_CHECK_CAT, "Global", true);

                List<Property> props = new ArrayList<Property>();

                for (ModContainer mod : ForgeVersion.gatherMods().keySet())
                {
                    values.remove(mod.getModId());
                    props.add(ForgeModContainer.getConfig().get(VERSION_CHECK_CAT, mod.getModId(), true)); //Get or make the value in the config
                }
                props.addAll(values.values()); // Add any left overs from the config
                Collections.sort(props, new Comparator<Property>()
                {
                    @Override
                    public int compare(Property o1, Property o2)
                    {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                List<IConfigElement> list = new ArrayList<IConfigElement>();
                list.add(new ConfigElement(global));
                for (Property prop : props)
                {
                    list.add(new ConfigElement(prop));
                }

                // This GuiConfig object specifies the configID of the object and as such will force-save when it is closed. The parent
                // GuiConfig object's propertyList will also be refreshed to reflect the changes.
                return new GuiConfig(this.owningScreen,
                        list,
                        this.owningScreen.modID, VERSION_CHECK_CAT, true, true,
                        GuiConfig.getAbridgedConfigPath(ForgeModContainer.getConfig().toString()));
            }
        }

        /**
         * This custom list entry provides the Mod Overrides entry on the Forge Chunk Loading config screen.
         * It extends the base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
         * In this case it adds the custom entry for adding a new mod override and lists the existing mod overrides.
         */
        public static class ModOverridesEntry extends CategoryEntry
        {
            public ModOverridesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            /**
             * This method is called in the constructor and is used to set the childScreen field.
             */
            @Override
            protected GuiScreen buildChildScreen()
            {
                List<IConfigElement> list = new ArrayList<IConfigElement>();

                list.add(new DummyCategoryElement("addForgeChunkLoadingModCfg", "forge.configgui.ctgy.forgeChunkLoadingAddModConfig",
                        AddModOverrideEntry.class));
                for (ConfigCategory cc : ForgeChunkManager.getModCategories())
                    list.add(new ConfigElement(cc));

                return new GuiConfig(this.owningScreen, list, this.owningScreen.modID,
                        this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, this.owningScreen.title,
                        I18n.format("forge.configgui.ctgy.forgeChunkLoadingModConfig"));
            }

            /**
             * By overriding the enabled() method and checking the value of the "enabled" entry this entry is enabled/disabled based on the value of
             * the other entry.
             */
            @Override
            public boolean enabled()
            {
                for (IConfigEntry entry : this.owningEntryList.listEntries)
                {
                    if (entry.getName().equals("enabled") && entry instanceof BooleanEntry)
                    {
                        return Boolean.valueOf(entry.getCurrentValue().toString());
                    }
                }

                return true;
            }

            /**
             * Check to see if the child screen's entry list has changed.
             */
            @Override
            public boolean isChanged()
            {
                if (childScreen instanceof GuiConfig)
                {
                    GuiConfig child = (GuiConfig) childScreen;
                    return child.entryList.listEntries.size() != child.initEntries.size() || child.entryList.hasChangedEntry(true);
                }
                return false;
            }

            /**
             * Since adding a new entry to the child screen is what constitutes a change here, reset the child
             * screen listEntries to the saved list.
             */
            @Override
            public void undoChanges()
            {
                if (childScreen instanceof GuiConfig)
                {
                    GuiConfig child = (GuiConfig) childScreen;
                    for (IConfigEntry ice : child.entryList.listEntries)
                        if (!child.initEntries.contains(ice) && ForgeChunkManager.getConfig().hasCategory(ice.getName()))
                            ForgeChunkManager.getConfig().removeCategory(ForgeChunkManager.getConfig().getCategory(ice.getName()));

                    child.entryList.listEntries = new ArrayList<IConfigEntry>(child.initEntries);
                }
            }
        }

        /**
         * This custom list entry provides a button that will open to a screen that will allow a user to define a new mod override.
         */
        public static class AddModOverrideEntry extends CategoryEntry
        {
            public AddModOverrideEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                List<IConfigElement> list = new ArrayList<IConfigElement>();

                list.add(new DummyConfigElement("modID", "", ConfigGuiType.STRING, "forge.configgui.modID").setCustomListEntryClass(ModIDEntry.class));
                list.add(new ConfigElement(new Property("maximumTicketCount", "200", Property.Type.INTEGER, "forge.configgui.maximumTicketCount")));
                list.add(new ConfigElement(new Property("maximumChunksPerTicket", "25", Property.Type.INTEGER, "forge.configgui.maximumChunksPerTicket")));

                return new GuiConfig(this.owningScreen, list, this.owningScreen.modID,
                        this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, this.owningScreen.title,
                        I18n.format("forge.configgui.ctgy.forgeChunkLoadingAddModConfig"));
            }

            @Override
            public boolean isChanged()
            {
                return false;
            }
        }

        /**
         * This custom list entry provides a Mod ID selector. The control is a button that opens a list of values to select from.
         * This entry also overrides onGuiClosed() to run code to save the data to a new ConfigCategory when the user is done.
         */
        public static class ModIDEntry extends SelectValueEntry
        {
            public ModIDEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop, getSelectableValues());
                if (this.selectableValues.size() == 0)
                    this.btnValue.enabled = false;
            }

            private static Map<Object, String> getSelectableValues()
            {
                Map<Object, String> selectableValues = new TreeMap<Object, String>();

                for (ModContainer mod : Loader.instance().getActiveModList())
                    // only add mods to the list that have a non-immutable ModContainer
                    if (!mod.isImmutable() && mod.getMod() != null)
                        selectableValues.put(mod.getModId(), mod.getName());

                return selectableValues;
            }

            /**
             * By overriding onGuiClosed() for this entry we can perform additional actions when the user is done such as saving
             * a new ConfigCategory object to the Configuration object.
             */
            @Override
            public void onGuiClosed()
            {
                Object modObject = Loader.instance().getModObjectList().get(Loader.instance().getIndexedModList().get(currentValue));
                int maxTickets = 200;
                int maxChunks = 25;
                if (modObject != null)
                {
                    this.owningEntryList.saveConfigElements();
                    for(IConfigElement ice : this.owningScreen.configElements)
                        if ("maximumTicketCount".equals(ice.getName()))
                            maxTickets = Integer.valueOf(ice.get().toString());
                        else if ("maximumChunksPerTicket".equals(ice.getName()))
                            maxChunks = Integer.valueOf(ice.get().toString());

                    ForgeChunkManager.addConfigProperty(modObject, "maximumTicketCount", String.valueOf(maxTickets), Property.Type.INTEGER);
                    ForgeChunkManager.addConfigProperty(modObject, "maximumChunksPerTicket", String.valueOf(maxChunks), Property.Type.INTEGER);

                    if (this.owningScreen.parentScreen instanceof GuiConfig)
                    {
                        GuiConfig superParent = (GuiConfig) this.owningScreen.parentScreen;
                        ConfigCategory modCtgy = ForgeChunkManager.getConfigFor(modObject);
                        modCtgy.setPropertyOrder(ForgeChunkManager.MOD_PROP_ORDER);
                        ConfigElement modConfig = new ConfigElement(modCtgy);

                        boolean found = false;
                        for (IConfigElement ice : superParent.configElements)
                            if (ice.getName().equals(currentValue))
                                found = true;

                        if (!found)
                            superParent.configElements.add(modConfig);

                        superParent.needsRefresh = true;
                        superParent.initGui();
                    }
                }
            }
        }
    }
}
