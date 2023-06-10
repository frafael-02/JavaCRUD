package tvz.java.rafaelprojekt.exceptions;

public class NoActivityException  extends  Exception{
    public NoActivityException() {
    }

    public NoActivityException(String message) {
        super(message);
    }

    public NoActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoActivityException(Throwable cause) {
        super(cause);
    }

    public NoActivityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
