package ccSensors.api;

import net.minecraft.src.World;

/*
 * Author: yoskaz01
 * Email: yevusi@gmail.com
 * 
 * Description:
 * the ISensorable Interface provides means of exposing specific information to external mods.
 * it acts like an "embedded" probe within the TileEntity that implements it.
 * 
 * i would've used IProbale if it didn't sound like something else:)
 *  
 * 
 * 
 * Use cases:
 * 
 * 1. 
 * 
 */
public interface ISensorable {

	
	

	
	/**
	 * 
	 * @return - a descriptive name of the Entity being probed.
	 * 
	 * Example:
	 * 		return "SuperDuperMachine";
	 */
	public String getProbeName();
	
	/**
	 * 
	 * @return array of strings containing the available probable reading names
	 * array index corresponds to readingId for: getReading.
	 * 
	 * Example:
	 * 
	 *  return new String[]{"EntityReading1","reading2","heat"};
	 * 
	 */
	public String[] getAvailableProbes();

	/**
	 * 
	 * @param readingId	- array index for the required reading received from getAvailableReadings()
	 * @return Object array containing the actual reading value(s).(one of more)
	 * 
	 *   Example:
	 *   
	 *   switch(readingId){
	 *   	case 0:
	 *   		return new Object[]{1,2,3};
	 *      case 1:
	 *      	return new Object[]{"heat",2,"maxheat",10};
	 *      default:
	 *      	return new Object[]{"Undefined reading id"};
	 *   }
	 *   
	 */
	public Object[] getReading(int readingId);
	
	/**
	 *  same as above but with more information provided to the getReading method
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 * @param readingId
	 * @param arguments		-	Object array containing arguments
	 * @return
	 */
	public Object[] getReading(World w,int x, int y, int z,int readingId ,Object[] arguments);
	
}
