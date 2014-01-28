package net.minecraft.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IScoreObjectiveCriteria
{
    Map field_96643_a = new HashMap();
    IScoreObjectiveCriteria field_96641_b = new ScoreDummyCriteria("dummy");
    IScoreObjectiveCriteria deathCount = new ScoreDummyCriteria("deathCount");
    IScoreObjectiveCriteria playerKillCount = new ScoreDummyCriteria("playerKillCount");
    IScoreObjectiveCriteria totalKillCount = new ScoreDummyCriteria("totalKillCount");
    IScoreObjectiveCriteria health = new ScoreHealthCriteria("health");

    String func_96636_a();

    int func_96635_a(List var1);

    boolean isReadOnly();
}