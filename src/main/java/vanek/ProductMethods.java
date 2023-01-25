package vanek;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service    //dependency injections
public class ProductMethods {
    private Connection connection;

    //variable connection must be defined outside the constructor otherwise it would be gone by the time the constructor closes
    //connection has to be connected in constructor as there might be an exception and a class can not throw exceptions
    public ProductMethods() throws SQLException{
        connection = DriverManager.getConnection(
                "jdbc:mysql://"+Setting.getMysqlDatabase(),
                Setting.getMysqlUser(),
                Setting.getMysqlPassword());
    }

    public List<Product> loadAll() throws SQLException{
        Statement statement = connection.createStatement();
        List<Product> listTmp = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
        while (resultSet.next()) {
            listTmp.add(extractFromResultSet(resultSet));
        }
        return listTmp;
    }

    public Product loadById(int id) throws SQLException{
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM products WHERE id = "+id);
        resultSet.next();
        return extractFromResultSet(resultSet);
    }

    public Product saveNew(Product product) throws SQLException{
        Statement statement = connection.createStatement();

        //product.getIsForSale() is automaticylly converted from source true/false to database representation 1/0
        String sqlString = "INSERT INTO products(partNo,name,description,isForSale,price) " +
                "VALUES ("+product.getPartNo()+",'"+product.getName()+"','"+product.getDescription()+"',"+product.getIsForSale()+","+product.getPrice()+")";
        statement.executeUpdate(sqlString , Statement.RETURN_GENERATED_KEYS);

        ResultSet generatedKeys = statement.getGeneratedKeys(); //podobně jako select, jen tam nejsou řádky, ale id
        generatedKeys.next();

        product.setId(generatedKeys.getInt(1));
        return product;
    }

    public void updatePriceByID(int id , BigDecimal price) throws SQLException{
        Statement statement = connection.createStatement();

        String sqlString = "UPDATE products SET price="+price+" WHERE id="+id;
        statement.executeUpdate(sqlString);
    }

    public void updateIsForSalePriceByID(Product product) throws SQLException {
        Statement statement = connection.createStatement();

        String sqlString = "UPDATE products SET isForSale="+product.getIsForSale()+",price="+product.getPrice()+" WHERE id="+product.getId();
        statement.executeUpdate(sqlString);
    }

    public void deleteOutOfSale() throws SQLException{
        Statement statement = connection.createStatement();

        String sqlString = "DELETE FROM products WHERE isForSale=0";
        statement.executeUpdate(sqlString);
    }

    public void deleteById(int id) throws SQLException {
        Statement statement = connection.createStatement();

        String sqlString = "DELETE FROM products WHERE id="+id;
        statement.executeUpdate(sqlString);
    }

    private Product extractFromResultSet(ResultSet resultSet)  throws SQLException{
        return new Product(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getBoolean(5),
                resultSet.getBigDecimal(6)
        );
    }

}
