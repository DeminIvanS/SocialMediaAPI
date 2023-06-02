package example.handler.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String msg){
        super("Entity with " + msg + "not found.");
    }
}
