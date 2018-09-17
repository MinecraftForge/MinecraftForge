package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FMLNetworking
{
    private static final Logger LOGGER = LogManager.getLogger();
    static final Marker NETWORK = MarkerManager.getMarker("FMLNETWORK");
    static final AttributeKey<String> FML_MARKER = AttributeKey.valueOf("fml:marker");
}
