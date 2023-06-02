package example.handler.exception;

public class UnableUpdateEntityException extends RuntimeException{
    public UnableUpdateEntityException (String msg){
        super("Unable update entity " + msg + ".");
    }
}
