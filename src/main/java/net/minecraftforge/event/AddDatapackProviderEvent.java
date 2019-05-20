package net.minecraftforge.event;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when the Server DataGenerator is created, so that mods can register DataProviders.<br/>
 * This event is not {@link Cancelable}.<br/>
 * This event does not have a result. {@link HasResult}.<br/>
 * This event is fired on the {@link MinecraftForge.EVENT_BUS}
 * @author chorm
 *
 */
public class AddDatapackProviderEvent extends Event {
	
	private final DataGenerator generator;
	private final boolean server;
	private final boolean client;
	private final boolean dev;
	private final boolean reports;
	
	public AddDatapackProviderEvent(DataGenerator gen,boolean server,boolean client,boolean dev,boolean reports)
	{
		this.generator = gen;
		this.server = server;
		this.client = client;
		this.dev = dev;
		this.reports = reports;
	}
	
	/**
	 * Gets the DataGenerator to load providers to.
	 */
	public DataGenerator getGenerator() 
	{
		return generator;
	}
	
	/**
	 * Checks if the server option was set when initializing the DataGenerator.<br/>
	 * Most DataProviders will want to register when this is true.
	 */
	public boolean isServer() 
	{
		return server;
	}
	
	/**
	 * Checks if the client option was set when initializing the DataGenerator.
	 */
	public boolean isClient() 
	{
		return client;
	}
	
	/**
	 * Checks if the dev option was set when initializing the DataGenerator.
	 */
	public boolean isDev() 
	{
		return dev;
	}
	
	/**
	 * Checks if the reports option was set when initializing the DataGenerator.
	 */
	public boolean shouldAddReports() {
		return reports;
	}
}
