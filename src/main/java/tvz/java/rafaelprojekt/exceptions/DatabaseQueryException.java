package tvz.java.rafaelprojekt.exceptions;

public class DatabaseQueryException extends RuntimeException{
    public DatabaseQueryException() {
    }

    public DatabaseQueryException(String message) {
        super(message);
    }

    public DatabaseQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseQueryException(Throwable cause) {
        super(cause);
    }

    public DatabaseQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
