package vanek;

import java.math.BigDecimal;

public class Product {
    private int id;
    private int partNo;
    private String name;
    private String description;
    private Boolean isForSale;
    private BigDecimal price;

    public Product(int id, int partNo, String name, String description, Boolean isForSale, BigDecimal price) {
        this.id = id;
        this.partNo = partNo;
        this.name = name;
        this.description = description;
        this.isForSale = isForSale;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getPartNo() {
        return partNo;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getIsForSale() {
        return isForSale;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setId(int id) { this.id = id; }
}
