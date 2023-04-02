/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.util.ConcatenatedListView;

import java.util.ArrayList;
import java.util.List;

public final class CreativeTabsScreenPage
{
    private final List<CreativeModeTab> tabs;
    private final List<CreativeModeTab> topTabs;
    private final List<CreativeModeTab> bottomTabs;
    private final List<CreativeModeTab> visibleTabs;

    public CreativeTabsScreenPage(List<CreativeModeTab> tabs)
    {
        this.tabs = tabs;
        this.topTabs = new ArrayList<>();
        this.bottomTabs = new ArrayList<>();
        this.visibleTabs = ConcatenatedListView.of(tabs, CreativeModeTabRegistry.getDefaultTabs());

        int maxLength = 10;
        int topLength = maxLength / 2;
        int length = tabs.size();

        for (int i = 0; i < length; i++)
        {
            CreativeModeTab tab = tabs.get(i);
            (i < topLength ? this.topTabs : this.bottomTabs).add(tab);
        }
    }

    public List<CreativeModeTab> getVisibleTabs()
    {
        return this.visibleTabs.stream().filter(CreativeModeTab::shouldDisplay).toList();
    }

    public boolean isTop(CreativeModeTab tab)
    {
        if (!this.tabs.contains(tab))
            return CreativeModeTabRegistry.getDefaultTabs().indexOf(tab) < (CreativeModeTabRegistry.getDefaultTabs().size() / 2);

        return this.topTabs.contains(tab);
    }

    public int getColumn(CreativeModeTab tab)
    {
//        if (!this.tabs.contains(tab)) {
//            return CreativeModeTabs.tabs().indexOf(tab) % 6;
//        }
//        return this.topTabs.contains(tab) ? this.topTabs.indexOf(tab) : this.bottomTabs.indexOf(tab);
        if (!this.tabs.contains(tab))
            return (CreativeModeTabRegistry.getDefaultTabs().indexOf(tab) % (CreativeModeTabRegistry.getDefaultTabs().size() / 2)) + 5;

        return this.topTabs.contains(tab) ? this.topTabs.indexOf(tab) : this.bottomTabs.indexOf(tab);
    }

    public CreativeModeTab getDefaultTab()
    {
        return this.tabs.get(0);
    }
}
