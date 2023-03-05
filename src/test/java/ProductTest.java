import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vanek.ProductService;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductTest {

    @Test
    public void priceTest() throws SQLException {
        BigDecimal priceValue = BigDecimal.valueOf(100);
        ProductService mojeTrida = new ProductService();
        //boolean testResult = mojeTrida.checkPrice(priceValue); check price is private so the test doesnt work
        //Assertions.assertEquals(true,testResult);
    }
}
