package net.minecraftforge.common.config;

public interface IConfigValueArray {
	
	public IConfigValueArray readFromString(String[] s);
	
	public String[] writeToString();

	public String usage();
}
