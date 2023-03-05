package vanek;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service    //dependency injections
public class ProductService {
    private Connection connection;

    //variable connection must be defined outside the constructor otherwise it would be gone by the time the constructor closes
    //connection has to be connected in constructor as there might be an exception and a class can not throw exceptions
    public ProductService() throws SQLException{
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
            listTmp.add(
                    new Product(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getBoolean(5),
                    resultSet.getBigDecimal(6)));
        }
        return listTmp;
    }

    public Product loadById(int id) throws SQLException{
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM products WHERE id = "+id);
        resultSet.next();
        //return extractFromResultSet(resultSet);
        return new Product(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getBoolean(5),
                resultSet.getBigDecimal(6)
        );
    }

    public Product saveNew(Product product) throws SQLException, ProductException{
        Statement statement = connection.createStatement();
        checkProduct(product);

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

    public void patchPriceByID(int id , BigDecimal price) throws SQLException, ProductException{
        int status;
        Statement statement = connection.createStatement();

        if (!checkPrice(price))
            throw new ProductException("Patching price error.","price empty or out of range error.");

        String sqlString = "UPDATE products SET price="+price+" WHERE id="+id;
        status = statement.executeUpdate(sqlString);
        if (status == 0) throw new ProductException("Patching price error.","no rows in SQL database was changed.");
    }

    public void patchIsForSalePriceByID(Product product) throws SQLException, ProductException {
        int status;
        Statement statement = connection.createStatement();

        if (!checkPrice(product.getPrice()))
            throw new ProductException("Patching is for sale, price error.","price empty or out of range error.");

        String sqlString = "UPDATE products SET isForSale="+product.getIsForSale()+",price="+product.getPrice()+" WHERE id="+product.getId();
        status = statement.executeUpdate(sqlString);
        if (status == 0) throw new ProductException("Patching is for sale, price error.","no rows in SQL database was changed.");
    }

    public void deleteOutOfSale() throws SQLException{
        Statement statement = connection.createStatement();

        String sqlString = "DELETE FROM products WHERE isForSale=0";
        statement.executeUpdate(sqlString);
    }

    public void deleteById(int id) throws SQLException, ProductException {
        int status;
        Statement statement = connection.createStatement();

        String sqlString = "DELETE FROM products WHERE id="+id;
        status = statement.executeUpdate(sqlString);
        if (status == 0) throw new ProductException("Delete product by ID failed.","no rows in SQL database was changed.");
    }

    private boolean checkIdIfExist (int id) {
        return true;
    }

    //methods to check input data
    private boolean checkPartNo(String partNoToCheck) {
        //database column partNo is set to varchar(20)
        //partnumber is mandatory
        /* this code was simplified
        if ( (partNoToCheck.isEmpty()) || (partNoToCheck.length() > 20) ) return false;
        return true;
         */
        if (partNoToCheck == null) return false;
        return (!partNoToCheck.isEmpty()) && (partNoToCheck.length() <= 20);
    }

    private boolean checkName(String nameToCheck) {
        //database column name is set to varchar(45)
        //name is mandatory
        if (nameToCheck == null) return false;
        return (!nameToCheck.isEmpty()) && (nameToCheck.length() <= 45);
    }

    private boolean checkDescription(String descriptionToCheck) {
        //database column description is set to varchar(255)
        //description is optional
        return descriptionToCheck.length() <= 255;
    }

    private boolean checkPrice(BigDecimal priceToCheck) {
        //database column price is set to decimal(10,2) so it can holds -99 999 999,99 to 99 999 999,99
        //price is mandatory and has to be positive number zero include
        if (priceToCheck == null) return false;
        return (priceToCheck.compareTo(BigDecimal.valueOf(0)) >= 0 && priceToCheck.compareTo(BigDecimal.valueOf(99999999.99)) <= 0);
    }

    private void checkProduct(Product productToCheck) throws ProductException{
        List<String> listOfErrors = new ArrayList<>();

        if (!checkPartNo(productToCheck.getPartNo())) {
            listOfErrors.add("partnumber empty or too long error");
        }
        if (!checkName(productToCheck.getName())) {
            listOfErrors.add("name empty or too long error");
        }
        if (!checkDescription(productToCheck.getDescription())) {
            listOfErrors.add("description too long error");
        }
        if (!checkPrice(productToCheck.getPrice())) {
            listOfErrors.add("price empty or out of range error");
        }

        if (listOfErrors.size() != 0) {
            String stringOfErrors = "";
            for (String tmpError : listOfErrors) stringOfErrors += tmpError+", ";
            stringOfErrors = stringOfErrors.substring(0,stringOfErrors.length()-2);
            stringOfErrors += ".";
            throw new ProductException("Adding new product check failed.",stringOfErrors);
        }
    }
}
