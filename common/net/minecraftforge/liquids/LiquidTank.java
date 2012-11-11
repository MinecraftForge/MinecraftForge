package net.minecraftforge.liquids;

/**
 * Reference implementation of ILiquidTank. Use this or implement your own.
 */
public class LiquidTank implements ILiquidTank {
	private LiquidStack liquid;
	private int capacity;
	
	public LiquidTank(int capacity) {
		this(null, capacity);
	}
	
	public LiquidTank(int liquidId, int quantity, int capacity) {
		this(new LiquidStack(liquidId, quantity), capacity);
	}
	public LiquidTank(LiquidStack liquid, int capacity) {
		this.liquid = liquid;
		this.capacity = capacity;
	}
	
	@Override
	public LiquidStack getLiquid() {
		return this.liquid;
	}
	
	@Override
	public void setLiquid(LiquidStack liquid) {
		this.liquid = liquid;
	}
	
	@Override
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public int getCapacity() {
		return this.capacity;
	}
	
	@Override
	public int fill(LiquidStack resource, boolean doFill) {
		if(resource == null || resource.itemID <= 0)
			return 0;
		
		if(liquid == null || liquid.itemID <= 0) {
			if(resource.amount <= capacity) {
				if(doFill)
					this.liquid = resource.copy();
				return resource.amount;
			} else {
				if(doFill) {
					this.liquid = resource.copy();
					this.liquid.amount = capacity;
				}
				return capacity;
			}
		}
		
		if(!liquid.isLiquidEqual(resource))
			return 0;
		
		int space = capacity - liquid.amount;
		if(resource.amount <= space) {
			if(doFill)
				this.liquid.amount += resource.amount;
			return resource.amount;
		} else {

			if(doFill)
				this.liquid.amount = capacity;
			return space;
		}

	}
	@Override
	public LiquidStack drain(int maxDrain, boolean doDrain) {
		if(liquid == null || liquid.itemID <= 0)
			return null;
		if(liquid.amount <= 0)
			return null;
		
		int used = maxDrain;
		if(liquid.amount < used)
			used = liquid.amount;
		
		if(doDrain) {
			liquid.amount -= used;
		}
		
		LiquidStack drained = new LiquidStack(liquid.itemID, used, liquid.itemMeta);
		
		// Reset liquid if emptied
		if(liquid.amount <= 0)
			liquid = null;
		
		return drained;
	}
}
