/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.config;

public interface IArrayEntry
{
    void keyTyped(char eventChar, int eventKey);

    void updateCursorCounter();

    void mouseClicked(int x, int y, int mouseEvent);

    void drawToolTip(int mouseX, int mouseY);

    boolean isValueSavable();

    Object getValue();
}
