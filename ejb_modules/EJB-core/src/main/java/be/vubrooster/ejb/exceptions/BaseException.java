package be.vubrooster.ejb.exceptions;

/**
 * @author Maxim Van de Wynckel
 * @date 03-May-16
 */
public abstract class BaseException extends Exception{
    private int errorId = 0;

    public BaseException(int errorId){
        setErrorId(errorId);
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }
}
