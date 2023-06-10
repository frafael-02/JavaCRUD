package tvz.java.rafaelprojekt.exceptions;

public class WinnerDecisionException extends RuntimeException{
    public WinnerDecisionException() {
    }

    public WinnerDecisionException(String message) {
        super(message);
    }

    public WinnerDecisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public WinnerDecisionException(Throwable cause) {
        super(cause);
    }

    public WinnerDecisionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
