package vanek;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController {
    //First option:
    //Creating of a productMethods class instance so that the methods can be used as they are not static
    //there might be SQLException while the instance of the class is being initialized
    /* this would work ok
    private ProductMethods productMethods = new ProductMethods();

    public ProductController() throws SQLException{
    }
    */
    /* other option
    public ProductController() {
        try {
            productMethods = new ProductMethods();
        } catch (SQLException e) {
            //what to do with it
        }
    }*/

    //Second option:
    //Dependency Injection
    private final ProductMethods productMethods; //service class

    public ProductController(ProductMethods productMethods) {
        this.productMethods = productMethods;
    } //dependency injections

    @GetMapping("/products")
    public List<Product> loadAll() throws SQLException{
        return productMethods.loadAll();    //return value is automatically converted to json
    }

    @GetMapping("/products/{id}")
    public Product loadById(@PathVariable(value = "id") int id) throws SQLException{
        return productMethods.loadById(id);
    }

    @PostMapping("/products")
    public Product saveNew(@RequestBody Product product) throws SQLException{
        return productMethods.saveNew(product);
    }

    @PatchMapping("/products/{id}")
    public void patchById(@PathVariable(value = "id") int id) throws SQLException {
        //tady dopsat metodu
    }


    @DeleteMapping("/products/{id}")
    public void deleteById(@PathVariable(value = "id") int id) throws SQLException {
        productMethods.deleteById(id);
    }

    //these mappings do not really meet Rest API conventions, PatchMapping is supposed to be used
    //mapping is case-sensitive, "-" can be used, conventions are up to the team to decide
    @PutMapping("/updatePriceByID/{id}")
    public void updatePriceByID(@PathVariable(value = "id") int id, @RequestParam(value = "price", required = true) BigDecimal price) throws SQLException {
        productMethods.updatePriceByID(id, price);
    }

    @PutMapping("/updateIsForSalePriceByID")
    public void updateIsForSalePriceByID(@RequestBody Product product) throws SQLException {
        //tady by se mohlo kontrolovat zda jsou produkty v range
        //ošetřit pomocí error handleru
        productMethods.updateIsForSalePriceByID(product);
    }

    @DeleteMapping("/deleteOutOfSale")
    public void deleteOutOfSale() throws SQLException {
        productMethods.deleteOutOfSale();
    }

}
