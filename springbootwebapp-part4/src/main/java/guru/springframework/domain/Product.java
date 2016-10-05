package guru.springframework.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    
    private int version;
    private String productId;
    private String description;
    
    private double price;
    private int clickrate;
    
    private int keywords_number;
    
    @OneToOne(cascade = {CascadeType.ALL})
    private CustomerImage customerImage;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

	public int getClickrate() {
		return clickrate;
	}

	public void setClickrate(int clickrate) {
		this.clickrate = clickrate;
	}

	public int getKeywords_number() {
		return keywords_number;
	}

	public void setKeywords_number(int keywords_number) {
		this.keywords_number = keywords_number;
	}

	public CustomerImage getCustomerImage() {
		return customerImage;
	}

	public void setCustomerImage(CustomerImage customerImage) {
		this.customerImage = customerImage;
	}
    
    
}
