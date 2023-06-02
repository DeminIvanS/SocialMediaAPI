package example.handler.exception;

public class ErrorException extends RuntimeException{
    public ErrorException(String msg){
        super(msg);
    }
}
