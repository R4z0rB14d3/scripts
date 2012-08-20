package scripts.farming.modules.processing;

import scripts.farming.Product;
import scripts.state.SharedModule;

public class ProcessingModules {
	public static final SharedModule<Product> LEPRECHAUN_NOTE = new LeprechaunNote();
	public static final SharedModule<Product> DROP = new Interact("Drop");
	public static final SharedModule<Product> CLEAN = new Interact("Clean");
}
