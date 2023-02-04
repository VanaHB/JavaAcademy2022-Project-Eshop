import com.mysql.cj.SimpleQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vanek.ProductMethods;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductTest {

    @Test
    public void priceTest() throws SQLException {
        BigDecimal priceValue = BigDecimal.valueOf(100);
        ProductMethods mojeTrida = new ProductMethods();
        boolean testResult = mojeTrida.checkPrice(priceValue);
        Assertions.assertEquals(true,testResult);
    }
}
