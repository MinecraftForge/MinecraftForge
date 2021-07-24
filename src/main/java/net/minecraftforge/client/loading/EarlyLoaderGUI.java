/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL30C.*;

public class EarlyLoaderGUI {
    private final Minecraft minecraft;
    private final Window window;
    private boolean handledElsewhere;
    
    private final StartupMessageRenderer renderer;
    
    public EarlyLoaderGUI(final Minecraft minecraft) {
        this.minecraft = minecraft;
        this.window = minecraft.getWindow();
        renderer = new StartupMessageRenderer();
    }
    
    public void handleElsewhere() {
        this.handledElsewhere = true;
    }
    
    void renderFromGUI() {
        renderer.render(window.getScreenWidth(), window.getScreenHeight(), 2, minecraft.options.darkMojangStudiosBackground);
    }
    
    void renderTick() {
        if (handledElsewhere) {
            return;
        }
        int guiScale = window.calculateScale(0, false);
        window.setGuiScale(guiScale);
        
        boolean isDarkMode = minecraft.options.darkMojangStudiosBackground;
        glClearColor(isDarkMode ? 0 : (239F / 255F), isDarkMode ? 0 : (50F / 255F), isDarkMode ? 0 : (61F / 255F), 1);
        glClear(GL_COLOR_BUFFER_BIT);
        
        renderer.render(window.getScreenWidth(), window.getScreenHeight(), 2, isDarkMode);
        window.updateDisplay();
    }
}
