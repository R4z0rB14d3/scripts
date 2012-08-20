package scripts.farming.modules.processing;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import scripts.farming.Product;
import scripts.state.Condition;
import scripts.state.SharedModule;
import scripts.state.State;
import scripts.state.Value;
import scripts.state.edge.Edge;
import scripts.state.edge.Either;
import scripts.state.edge.InteractNPC;
import scripts.state.edge.InteractWidget;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.UseItem;
import scripts.state.edge.UseItemWith;

public class LeprechaunNote extends SharedModule<Product> {

	public LeprechaunNote() {
		super("Leprechaun Note", new State("LEPRECHAUN NOTE : INITIAL"),
				new State("LEPRECHAUN NOTE : SUCCESS"), new State(
						"LEPRECHAUN NOTE : CRITICAL"));

		State FOUND_ITEM = new State("FI");
		State FOUND_LEPRECHAUN = new State("FL");
		State NOT_FOUND_LEPRECHAUN = new State("NFL");
		State NOT_FOUND_LEPRECHAUN_CAMERA_FIXED = new State("NFLCF");
		State USING_ITEM = new State("UI");
		State USED_ITEM = new State("UsdI");
		State COUNTING_ITEMS = new State("CNTI");
		State CLOSE_WIDGET = new State("CLW");
		//final Constant<Integer> amount = new Constant<Integer>(0);
		//final Constant<String> name = new Constant<String>("");

		final Value<Integer> itemID = new Value<Integer>() {
			public Integer get() {
				return getProduct().getId();
			}
		};
		final Value<NPC> leprechaun = new Value<NPC>() {
			public NPC get() {
				return NPCs.getNearest(7569, 3021, 5808, 7557, 4965);
			}
		};
		final Value<Item> item = new Value<Item>() {
			public Item get() {
				return Inventory.getItem(itemID.get());
			}
		};

		getInitialState().add(new Either(new Condition() {
			public boolean validate() {
				System.out.println("Is null: " + getProduct()==null);
				System.out.println("ID: " + itemID.get());
				return item.get() != null;
			}
		}, FOUND_ITEM, getSuccessState()));

		FOUND_ITEM.add(new Either(new Condition() {
			public boolean validate() {
				System.out.println("Found Item. Looking for leprechaun: " + leprechaun.get() != null);
				return leprechaun.get() != null;
			}
		}, FOUND_LEPRECHAUN, NOT_FOUND_LEPRECHAUN));

		NOT_FOUND_LEPRECHAUN.add(new Task(Condition.TRUE,
				NOT_FOUND_LEPRECHAUN_CAMERA_FIXED) {
			public void run() {
				Camera.setPitch(99);
				Camera.setAngle((540 - Camera.getYaw()) % 360);
			}
		});

		NOT_FOUND_LEPRECHAUN_CAMERA_FIXED.add(new Either(new Condition() {
			public boolean validate() {
				return leprechaun.get() == null;
			}
		}, FOUND_LEPRECHAUN, getCriticalState()));

		
		FOUND_LEPRECHAUN.add(new UseItemWith<NPC>(Condition.TRUE, COUNTING_ITEMS,itemID,leprechaun).setFilter(new Filter<String>() {
			public boolean accept(String s) {
				return s.contains("-> Tool");
			}
		}));
		/*FOUND_LEPRECHAUN.add(new UseItem(Condition.TRUE, USING_ITEM, itemID));
		USING_ITEM.add(new InteractNPC(Condition.TRUE, USED_ITEM, leprechaun,
				"Use", true));
		USED_ITEM.add(new Edge(new Condition() {
			public boolean validate() {
				return Inventory.getCount(itemID.get()) == 0;
			}
		}, COUNTING_ITEMS));
		USED_ITEM.add(new Timeout(getInitialState(), 5000));*/
		COUNTING_ITEMS.add(new Task(Condition.TRUE, CLOSE_WIDGET) {
			public void run() {
				/*if (ProductXYZ.notedProducts.containsKey(name.get())) {
					ProductXYZ.notedProducts
							.put(name.get(),
									(ProductXYZ.notedProducts.get(name.get()) + amount.get()));
				} else {
					ProductXYZ.notedProducts.put(name.get(),
							amount.get());
				}*/
			}
		});
		CLOSE_WIDGET.add(new InteractWidget(Condition.TRUE, getSuccessState(),
				new Value<WidgetChild>() {
					public WidgetChild get() {
						return Widgets.get(1189, 10);
					}
				}, "Continue"));
		CLOSE_WIDGET.add(new Timeout(getSuccessState(), 2000));

	}

	Product getProduct() {
		return intermediateValue;
	}
}