/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.Achievement;

public class AchievementPage
{
    private String name;
    private LinkedList<Achievement> achievements;

    public AchievementPage(String name, Achievement... achievements)
    {
        this.name = name;
        this.achievements = new LinkedList<Achievement>(Arrays.asList(achievements));
    }

    public String getName()
    {
        return name;
    }

    public List<Achievement> getAchievements()
    {
        return achievements;
    }
}