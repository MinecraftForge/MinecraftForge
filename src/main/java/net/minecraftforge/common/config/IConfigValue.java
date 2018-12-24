package net.minecraftforge.common.config;

public interface IConfigValue {
	
	public IConfigValue readFromString(String s);
	
	public String writeToString();

	public String usage();
}
