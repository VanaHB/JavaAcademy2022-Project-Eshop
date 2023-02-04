package vanek;

public class ProductException extends Exception{
    public ProductException(String message, String description) {
        super(message+" Errors: "+description);
    }
}
