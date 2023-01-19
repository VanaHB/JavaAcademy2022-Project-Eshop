package vanek;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ProductController {
    //Products operations:
    //loadAll - return list of all products in database
    //loadById - return information of a single product defined by id
    //saveProduct - add a new product to the database (parameter is an object of the Product class)
    //updatePriceById - update price of a product defined by id
    //deleteOutOfSale - delete all out of sale products

    //creating of a productMethods class instance so that the methods can be used as they are not static
    //there might be SQLException while the instance of the class is being initialized

    /* this would work ok
    private ProductMethods productMethods = new ProductMethods();

    public ProductController() throws SQLException{
    }
     */
    private ProductMethods productMethods;

    public ProductController() {
        try {
            productMethods = new ProductMethods();
        } catch (SQLException e) {
            //what to do with it
        }
    }


    @GetMapping("/loadAll")
    public List<Product> loadAll() throws SQLException{
        return productMethods.loadAll();    //return value is automatically converted to json
    }

    @GetMapping("/loadById/{id}")
    public Product loadById(@PathVariable(value = "id") int id) throws SQLException{
        return productMethods.loadById(id);
    }

    @PostMapping("/saveProduct")
    public Product saveProduct(@RequestBody Product product) throws SQLException{
        return productMethods.saveProduct(product);
    }

    @PutMapping("/updatePriceByID/{id}")
    public void updatePriceByID(@PathVariable(value = "id") int id, @RequestParam(value = "price", required = true) BigDecimal price) throws SQLException {
        productMethods.updatePriceByID(id, price);
    }

    @DeleteMapping("/deleteOutOfSale")
    public void deleteOutOfSale() throws SQLException {
        productMethods.deleteOutOfSale();
    }
}
