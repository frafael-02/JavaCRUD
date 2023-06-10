package tvz.java.rafaelprojekt.exceptions;

public class LoginDataException extends Exception{
    public LoginDataException() {
    }

    public LoginDataException(String message) {
        super(message);
    }

    public LoginDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginDataException(Throwable cause) {
        super(cause);
    }

    public LoginDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
