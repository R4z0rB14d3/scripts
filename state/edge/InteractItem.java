package scripts.state.edge;

import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.Value;

public class InteractItem extends Task {

	Value<Item> id;
	String interaction;

	public InteractItem(Condition c, State s, final Value<Item> id_,
			String interaction_) {
		super(c.and(new Condition() {
			public boolean validate() {
				return id_.get() != null;//Inventory.getCount(id_.get()) > 0;
			}
		}), s);
		id = id_;
		interaction = interaction_;
	}

	/*public InteractItem(Condition c, State s, final Value<Integer> id_,
			String interaction_) {
		super(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getCount(id_.get()) > 0;
			}
		}), s);
		id = id_;
		interaction = interaction_;
	}

	public InteractItem(Condition c, State s, final int id_, String interaction_) {
		this(c, s, new Constant<Integer>(id_), interaction_);
	}*/

	@Override
	public void run() {
		System.out.println("Interact with " + id.get());
		Item item = id.get();
		if (item != null)
			item.getWidgetChild().interact(interaction);
	}
}
