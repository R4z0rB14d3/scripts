package scripts.farming;

public class Product {
	ProductType type;
	int id;
	
	public Product(ProductType type_, int id_) {
		type = type_;
		id = id_;
	}
	
	public ProductType getType() { return type; }
	public int getId() { return id; }
}
