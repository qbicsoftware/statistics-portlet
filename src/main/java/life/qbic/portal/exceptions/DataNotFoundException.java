package life.qbic.portal.exceptions;


/**
 * @author fhanssen
 */
public class DataNotFoundException extends Exception {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(Throwable cause) {
        super(cause);
    }

    public DataNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
