package org.spigotmc;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class LogFilter implements Filter
{

    public boolean isLoggable(LogRecord record)
    {
        if ( record.getMessage() != null )
        {
            for ( Pattern pattern : SpigotConfig.logFilters )
            {
                if ( pattern.matcher( record.getMessage() ).matches() )
                {
                    return false;
                }
            }
        }
        return true;
    }
}
