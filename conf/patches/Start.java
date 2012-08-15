import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;

public class Start
{
    public static void main(String[] args)
    {
        try
        {
            // set new minecraft data folder to prevent it from using the .minecraft folder
            // this makes it a portable version
            Field f = Minecraft.class.getDeclaredField("field_71463_am");
            Field.setAccessible(new Field[] { f }, true);
            f.set(null, new File("."));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        // start minecraft game application
        Minecraft.main(args);
    }
}
