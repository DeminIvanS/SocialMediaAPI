package example.handler.exception;

public class UnableCreateEntityException extends RuntimeException{
    public UnableCreateEntityException(String msg){
        super("Unable to create entity: " + msg + ".");
    }
}
