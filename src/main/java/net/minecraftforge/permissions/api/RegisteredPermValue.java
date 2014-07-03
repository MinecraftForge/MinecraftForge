package net.minecraftforge.permissions.api;

public enum RegisteredPermValue
{
    TRUE(0), // for all
    OP1(1), // bypass spawn protection
    OP2(2), // allows /clear, /difficulty, /effect, /gamemode, /gamerule, /give, and /tp, and command blocks.
    OP3(3), // allows /ban, /deop, /kick, and /op.
    OP4(4), // allows /stop
    FALSE(5); // not allowed for players
    
    private int permLevel;
    
    private RegisteredPermValue(int level)
    {
        this.permLevel = level;
    }
    
    public static RegisteredPermValue fromInt(int level)
    {
        switch (level)
        {
        case 0:
            return RegisteredPermValue.TRUE;
        case 1:
            return RegisteredPermValue.OP1;
        case 2:
            return RegisteredPermValue.OP2;
        case 3:
            return RegisteredPermValue.OP3;
        case 4:
            return RegisteredPermValue.OP4;
        case 5:
            return RegisteredPermValue.FALSE;
        }
        return null;
    }
    
    public int getLevel()
    {
        return permLevel;
    }
}
