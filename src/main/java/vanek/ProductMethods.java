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
        //int without '' X String with ''
        String sqlString = "INSERT INTO products(partNo,name,description,isForSale,price) " +
                "VALUES ('"+product.getPartNo()+"','"+product.getName()+"','"+product.getDescription()+"',"+product.getIsForSale()+","+product.getPrice()+")";
        statement.executeUpdate(sqlString , Statement.RETURN_GENERATED_KEYS);

        ResultSet generatedKeys = statement.getGeneratedKeys(); //geting data is similar to select, only ids insted of lines
        generatedKeys.next();
        product.setId(generatedKeys.getInt(1));

        return product; //returns new added product including primary key
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
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getBoolean(5),
                resultSet.getBigDecimal(6)
        );
    }

    //methods to check input data
    public boolean checkPartNo(String partNoToCheck) {
        //database column partNo is set to varchar(20)
        //partnumber is mandatory
        /* this code was simplified
        if ( (partNoToCheck.isEmpty()) || (partNoToCheck.length() > 20) ) return false;
        return true;
         */
        return (!partNoToCheck.isEmpty()) && (partNoToCheck.length() <= 20);
    }

    public boolean checkName(String nameToCheck) {
        //database column name is set to varchar(45)
        //name is mandatory
        return (!nameToCheck.isEmpty()) && (nameToCheck.length() <= 45);
    }

    public boolean checkDescription(String descriptionToCheck) {
        //database column description is set to varchar(255)
        //description is optional
        return descriptionToCheck.length() <= 255;
    }

    public boolean checkPrice(BigDecimal priceToCheck) {
        //database column price is set to decimal(10,2) so it can holds -99 999 999,99 to 99 999 999,99
        //price is mandatory and has to be positive number zero include
        if (priceToCheck == null) return false;
        return (priceToCheck.compareTo(BigDecimal.valueOf(0)) >= 0 && priceToCheck.compareTo(BigDecimal.valueOf(99999999.99)) <= 0);
    }

    public void checkProduct(Product productToCheck) throws ProductException{
        List<String> listOfErrors = new ArrayList<>();

        if (!checkPartNo(productToCheck.getPartNo())) {
            listOfErrors.add("partnumber error");
        }
        if (!checkName(productToCheck.getName())) {
            listOfErrors.add("name error");
        }
        if (!checkDescription(productToCheck.getDescription())) {
            listOfErrors.add("description error");
        }
        if (!checkPrice(productToCheck.getPrice())) {
            listOfErrors.add("price error");
        }

        if (listOfErrors.size() != 0) {
            String stringOfErrors = "";
            for (String tmpError : listOfErrors) stringOfErrors += tmpError+", ";
            stringOfErrors = stringOfErrors.substring(0,stringOfErrors.length()-2);
            stringOfErrors += ".";
            throw new ProductException("Product check failed.",stringOfErrors);
        }
    }
}
