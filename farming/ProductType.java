package scripts.farming;

import java.util.ArrayList;
import java.util.List;

import scripts.farming.modules.processing.ProcessingModules;
import scripts.state.SharedModule;

public class ProductType {

	public static final ProductType GARBAGE = new ProductType("Garbage")
			.add(ProcessingModules.DROP);

	public static final ProductType GRIMY_HERB = new ProductType("Grimy Herb")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.CLEAN)
	// .add(ProcessingModules.LEPRECHAUN_NOTE)
	;
	public static final ProductType CLEAN_HERB = new ProductType("Clean Herb")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	public static final ProductType FLOWER = new ProductType("Flower")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	public static final ProductType VEGETABLE = new ProductType("Vegetable")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	
	public static final ProductType CACTUS = new ProductType("Cactus")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	
	public static final ProductType BELLADONNA = new ProductType("Belladonna")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	
	public static final ProductType EVIL_TURNIP = new ProductType("Evil Turnip")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);
	
	public static final ProductType CALQUAT = new ProductType("Calquat")
	// .add(ProcessingModules.DROP)
			.add(ProcessingModules.LEPRECHAUN_NOTE);

	String name;
	List<SharedModule<Product>> modules = new ArrayList<SharedModule<Product>>();

	ProductType(String name_) {
		name = name_;
	}

	ProductType add(SharedModule<Product> module) {
		modules.add(module);
		return this;
	}

	public String getName() {
		return name;
	}

	public List<SharedModule<Product>> getModules() {
		return modules;
	}

	public SharedModule<Product> getSelectedModule() {
		System.out.println("Get Selected Module:");
		for(SharedModule<Product> module : modules) {
			System.out.println(module);
		}
		return modules.get(0);
	}
}
