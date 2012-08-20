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
				return id_.get() != null;
			}
		}), s);
		id = id_;
		interaction = interaction_;
	}


	@Override
	public void run() {
		System.out.println("Interact with " + id.get().getWidgetChild().getIndex());
		Item item = id.get();
		if (item != null)
			item.getWidgetChild().interact(interaction);
	}
}
