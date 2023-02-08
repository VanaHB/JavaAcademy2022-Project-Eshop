package vanek;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
    }

    //ExceptionHandler catches Exceptions and allows us to adjust them in order to get custom response
    //example of SQLException without and with ExceptionHadler
    //{"timestamp":"2023-02-03T13:05:25.895+00:00","status":500,"error":"Internal Server Error","path":"/products"}
    //{"message":"Illegal operation on empty result set.","timeStamp":"2023-02-03T14:16:51.9623217"}
    @ExceptionHandler //no futher specification - catches all exceptions - can be Exception specific @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //sets response code to 500 (generic error), default is 200 - ok
    //method can have any name and return value as long as the result can be processed to json (it is done automaticaly)
    public ErrorResponse handleError(Exception e){ //all errors are collected as Exception e
        return new ErrorResponse(e.getMessage(), LocalDateTime.now());
    }

    @GetMapping("/products")
    public List<Product> loadAll() throws SQLException{
        return productMethods.loadAll();    //return value is automatically converted to json
    }

    @GetMapping("/products/{id}")
    public Product loadById(@PathVariable(value = "id") int id) throws SQLException{
        return productMethods.loadById(id);
    }

    @PostMapping("/products")
    public Product saveNew(@RequestBody Product product) throws SQLException, ProductException{
        productMethods.checkProduct(product);
        return productMethods.saveNew(product);
    }

    @PatchMapping("/products/{id}")
    public void patchById(@PathVariable(value = "id") int id) throws SQLException {
        //will be added in future
    }

    @DeleteMapping("/products/{id}")
    public void deleteById(@PathVariable(value = "id") int id) throws SQLException {
        productMethods.deleteById(id);
    }

    //these mappings do not really meet Rest API conventions, PatchMapping is supposed to be used
    //mapping is case-sensitive, "-" can be used, conventions are up to the team to decide
    @PutMapping("/updatePriceByID/{id}") //this is not used by frond-end
    public void updatePriceByID(@PathVariable(value = "id") int id, @RequestParam(value = "price", required = true) BigDecimal price) throws SQLException {
        productMethods.updatePriceByID(id, price);
    }

    @PutMapping("/updateIsForSalePriceByID")
    public void updateIsForSalePriceByID(@RequestBody Product product) throws Exception , SQLException {
        if (productMethods.checkPrice(product.getPrice())) {
            productMethods.updateIsForSalePriceByID(product);
        } else {
            throw new Exception("Price is out of range.");
        }
    }

    @DeleteMapping("/deleteOutOfSale") //this is not used by frond-end
    public void deleteOutOfSale() throws SQLException {
        productMethods.deleteOutOfSale();
    }

}
