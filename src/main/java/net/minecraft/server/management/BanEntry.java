package net.minecraft.server.management;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BanEntry
{
    private static final Logger field_151362_b = LogManager.getLogger();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final String username;
    private Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate;
    private String reason = "Banned by an operator.";
    private static final String __OBFID = "CL_00001395";

    public BanEntry(String par1Str)
    {
        this.username = par1Str;
    }

    public String getBannedUsername()
    {
        return this.username;
    }

    public Date getBanStartDate()
    {
        return this.banStartDate;
    }

    public String getBannedBy()
    {
        return this.bannedBy;
    }

    public void setBannedBy(String par1Str)
    {
        this.bannedBy = par1Str;
    }

    // JAVADOC METHOD $$ func_73681_a
    @SideOnly(Side.SERVER)
    public void setBanStartDate(Date par1Date)
    {
        this.banStartDate = par1Date != null ? par1Date : new Date();
    }

    public Date getBanEndDate()
    {
        return this.banEndDate;
    }

    public boolean hasBanExpired()
    {
        return this.banEndDate == null ? false : this.banEndDate.before(new Date());
    }

    public String getBanReason()
    {
        return this.reason;
    }

    public void setBanReason(String par1Str)
    {
        this.reason = par1Str;
    }

    public String buildBanString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(this.getBannedUsername());
        stringbuilder.append("|");
        stringbuilder.append(dateFormat.format(this.getBanStartDate()));
        stringbuilder.append("|");
        stringbuilder.append(this.getBannedBy());
        stringbuilder.append("|");
        stringbuilder.append(this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate()));
        stringbuilder.append("|");
        stringbuilder.append(this.getBanReason());
        return stringbuilder.toString();
    }

    @SideOnly(Side.SERVER)
    public void setBanEndDate(Date par1Date)
    {
        this.banEndDate = par1Date;
    }

    @SideOnly(Side.SERVER)
    public static BanEntry parse(String par0Str)
    {
        if (par0Str.trim().length() < 2)
        {
            return null;
        }
        else
        {
            String[] astring = par0Str.trim().split(Pattern.quote("|"), 5);
            BanEntry banentry = new BanEntry(astring[0].trim());
            byte b0 = 0;
            int j = astring.length;
            int i = b0 + 1;

            if (j <= i)
            {
                return banentry;
            }
            else
            {
                try
                {
                    banentry.setBanStartDate(dateFormat.parse(astring[i].trim()));
                }
                catch (ParseException parseexception1)
                {
                    field_151362_b.warn("Could not read creation date format for ban entry \'" + banentry.getBannedUsername() + "\' (was: \'" + astring[i] + "\')", parseexception1);
                }

                j = astring.length;
                ++i;

                if (j <= i)
                {
                    return banentry;
                }
                else
                {
                    banentry.setBannedBy(astring[i].trim());
                    j = astring.length;
                    ++i;

                    if (j <= i)
                    {
                        return banentry;
                    }
                    else
                    {
                        try
                        {
                            String s1 = astring[i].trim();

                            if (!s1.equalsIgnoreCase("Forever") && s1.length() > 0)
                            {
                                banentry.setBanEndDate(dateFormat.parse(s1));
                            }
                        }
                        catch (ParseException parseexception)
                        {
                            field_151362_b.warn("Could not read expiry date format for ban entry \'" + banentry.getBannedUsername() + "\' (was: \'" + astring[i] + "\')", parseexception);
                        }

                        j = astring.length;
                        ++i;

                        if (j <= i)
                        {
                            return banentry;
                        }
                        else
                        {
                            banentry.setBanReason(astring[i].trim());
                            return banentry;
                        }
                    }
                }
            }
        }
    }
}