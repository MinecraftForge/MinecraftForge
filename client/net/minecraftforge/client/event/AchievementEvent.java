package net.minecraftforge.client.event;

import net.minecraft.src.Achievement;
import net.minecraft.src.StatBase;

public class AchievementEvent extends StatEvent
{
    public Achievement achievement;
    public AchievementEvent(Achievement achievement, int incrementAmount)
    {
        super(achievement, incrementAmount);
        this.achievement = achievement;
    }

}
