package scripts.farming.modules.processing;

import java.util.Arrays;
import java.util.List;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.Product;
import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.SharedModule;
import scripts.state.State;
import scripts.state.StateCreator;
import scripts.state.Value;
import scripts.state.edge.Either;
import scripts.state.edge.InteractItem;
import scripts.state.edge.Timeout;

public class Interact extends SharedModule<Product> {

	public Interact(final String interaction) {
		super("Interact " + interaction, new State(interaction + " : INITIAL"),
				new State(interaction + " : SUCCESS"), new State(interaction
						+ " NOTE : CRITICAL"));

		final Value<Integer> itemID = new Value<Integer>() {
			public Integer get() {
				return getProduct().getId();
			}
		};
		final Value<List<Item>> item = new Value<List<Item>>() {
			public List<Item> get() {
				final int id = itemID.get();
				return Arrays.asList(Inventory.getItems(new Filter<Item>() {
					public boolean accept(Item i) {
						return i.getId() == id;
					}
				}));
			}
		};

		State FOUND_ITEM = new ConsecutiveState<Item>(item, getInitialState(),
				new StateCreator<Item>() {
					public State getState(Item value, State nextState) {
						State state = new State();
						state.add(new InteractItem(Condition.TRUE, nextState,
								new Value<Item>() {
									public Item get() {
										return Inventory.getItem(itemID.get());
									}
								}, interaction));
						state.add(new Timeout(getSuccessState(), 1000));
						return state;
					}
				});

		getInitialState().add(new Either(new Condition() {
			public boolean validate() {
				return Inventory.getCount(itemID.get())>0;
			}
		}, FOUND_ITEM, getSuccessState()));

	}

	Product getProduct() {
		return intermediateValue;
	}
}