/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.stats.Achievement;

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
    
    private static LinkedList<AchievementPage> achievementPages = new LinkedList<AchievementPage>();
    
    /**
     * Registers an achievement page.
     * @param page The page.
     */
    public static void registerAchievementPage(AchievementPage page)
    {
        if (getAchievementPage(page.getName()) != null)
        {
            throw new RuntimeException("Duplicate achievement page name \"" + page.getName() + "\"!");
        }
        achievementPages.add(page);
    }
    
    /**
     * Will return an achievement page by its index on the list.
     * @param index The page's index.
     * @return the achievement page corresponding to the index or null if invalid index
     */
    public static AchievementPage getAchievementPage(int index)
    {
        return achievementPages.get(index);
    }
    
    /**
     * Will return an achievement page by its name.
     * @param name The page's name.
     * @return the achievement page with the given name or null if no such page
     */
    public static AchievementPage getAchievementPage(String name)
    {
        for (AchievementPage page : achievementPages)
        {
            if (page.getName().equals(name))
            {
                return page;
            }
        }
        return null;
    }
    
    /**
     * Will return the list of achievement pages.
     * @return the list's size
     */
    public static Set<AchievementPage> getAchievementPages()
    {
        return new HashSet<AchievementPage>(achievementPages);
    }
    
    /**
     * Will return whether an achievement is in any page or not.
     * @param achievement The achievement.
     */
    public static boolean isAchievementInPages(Achievement achievement)
    {
        for (AchievementPage page : achievementPages)
        {
            if (page.getAchievements().contains(achievement)) 
            {
                return true;
            }
        }
        return false;
    }
    
    public static String getTitle(int index)
    {
        return index == -1 ? "Minecraft" : getAchievementPage(index).getName();
    }
}