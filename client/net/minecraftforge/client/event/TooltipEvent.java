package net.minecraftforge.client.event;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class TooltipEvent extends Event
{
  public final ItemStack itemStack;
	
	public TooltipEvent(ItemStack itemStack)
	{
		this.itemStack = itemStack;
	}	
	
	@Cancelable
	public static class Hover extends TooltipEvent
	{
		public List tooltipList;
		
		public Hover(ItemStack itemStack, List tooltipList)
		{
			super(itemStack);
			this.tooltipList = tooltipList;
		}
		
	}
	
	@Cancelable
	public static class Scroll extends TooltipEvent
	{
		public String tooltip;
		public int color = 0xFFFFFF;
		public int posX;
		public int posY;
		
		public Scroll(ItemStack itemStack, String tooltip, int posX, int posY)
		{
			super(itemStack);
			this.tooltip = tooltip;
			this.posX = posX;
			this.posY = posY;
		}
		
	}	
	
	public static class Search extends TooltipEvent
	{		
		public List keywordList;
		public final String searchParameter;
		
		public Search(ItemStack itemStack, List keywordList, String searchParameter)
		{
			super(itemStack);
			this.keywordList = keywordList;
			this.searchParameter = searchParameter;
		}
		
	}
	
}
