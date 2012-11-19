package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;

public class Category
{
	public String name;
	public String comment;
	public ArrayList<String> children;
	public HashMap<String, Property> properties;
	public final Category parent;
	
	public Category(String name)
	{
		this(name, null);
	}
	
	public Category(String name, Category parent)
	{
		this.name = name;
		children = new ArrayList<String>();
		properties = new HashMap<String, Property>();
		this.parent = parent;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof Category)
		{
			Category cat = (Category) obj;
			return name.equals(cat.name) && children.equals(cat.children);  
		}
		
		return false;
	}
	
	public String getQualifiedName()
	{
		if (parent == null)
			return name;
		else
		{
			return (new StringBuilder()).append(parent.getQualifiedName()).append(Configuration.CATEGORY_SPLITTER).append(name).toString();
		}
	}
	
	public Category getFirstParent()
	{
		if (parent == null)
			return this;
		else
			return parent.getFirstParent();
	}
}