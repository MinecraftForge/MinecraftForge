/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.test;


import net.minecraft.init.Bootstrap;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeTestRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

@RunWith(ForgeTestRunner.class)
public class ConfigurationTest {

    private Configuration config;
    private ConfigCategory category;

    @BeforeClass
    public static void setupClass()
    {
        Loader.instance();
        Bootstrap.register();
    }

    @Before
    public void setup()
    {
        Property enabledProperty = new Property("enabled", "true", Property.Type.BOOLEAN);
        enabledProperty.setComment("enabled property comment");

        Property backgroundProperty = new Property("background", "0xFFFFFF", Property.Type.COLOR);
        backgroundProperty.setComment("background property comment");

        config = new Configuration();
        category = config.getCategory("defaults");
        category.put(enabledProperty.getName(), enabledProperty);
        category.put(backgroundProperty.getName(), backgroundProperty);
    }

    @Test
    public void testRenameProperty_newNameNotInUse()
    {
        boolean propertyRenamed = config.renameProperty("defaults", "enabled", "defaultEnabled");

        Property enabledProperty = category.get("enabled");
        Property defaultEnabledProperty = category.get("defaultEnabled");

        assertTrue("Property was not renamed", propertyRenamed);
        assertNull("Old property was not removed", enabledProperty);
        assertNotNull("New property was not added", defaultEnabledProperty);
        assertEquals("The property's name was not changed", "defaultEnabled", defaultEnabledProperty.getName());
        assertEquals("The property's value changed", "true", defaultEnabledProperty.getString());
        assertEquals("The property's type was changed", Property.Type.BOOLEAN, defaultEnabledProperty.getType());
        assertEquals("The property's comment was changed", "enabled property comment", defaultEnabledProperty.getComment());
    }

    @Test
    public void testRenameProperty_newNameInUse_replaceExistingProperty()
    {
        boolean propertyRenamed = config.renameProperty("defaults", "enabled", "background");

        Property enabledProperty = category.get("enabled");
        Property backgroundProperty = category.get("background");

        assertTrue("Property was not renamed", propertyRenamed);
        assertNull("Old property was not removed", enabledProperty);
        assertNotNull("New property was not added", backgroundProperty);
        assertEquals("The property's name was not changed", "background", backgroundProperty.getName());
        assertEquals("The property's value changed", "true", backgroundProperty.getString());
        assertEquals("The property's type was changed", Property.Type.BOOLEAN, backgroundProperty.getType());
        assertEquals("The property's comment was changed", "enabled property comment", backgroundProperty.getComment());
    }
}
