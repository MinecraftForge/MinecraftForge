package net.minecraftforge.client.model;

/**
 * Thrown if there is a problem parsing the model
 *
 * @author cpw
 *
 */
public class ModelFormatException extends RuntimeException {

    public ModelFormatException()
    {
        super();
    }

    public ModelFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ModelFormatException(String message)
    {
        super(message);
    }

    public ModelFormatException(Throwable cause)
    {
        super(cause);
    }

}
