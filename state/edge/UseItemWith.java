package scripts.state.edge;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class UseItemWith<T extends Locatable & Entity> extends Task {

	Value<Integer> id;
	Value<T> object = null;
	Filter<String> filter = null;

	public UseItemWith(Condition c, State s, Integer id_,
			final Value<T> object_) {
		this(c, s, id_, object_, false);
	}

	public UseItemWith(Condition c, State s, Integer id_,
			final Value<T> object_, final boolean turnCamera) {
		this(c, s, new Constant<Integer>(id_), object_, turnCamera);
	}

	public UseItemWith(Condition c, State s, Value<Integer> id_,
			final Value<T> object_) {
		this(c, s, id_, object_, false);
	}	

	public UseItemWith(Condition c, State s, Value<Integer> id_,
			final Value<T> object_, boolean turnCamera) {
		super(null, s);
		id = id_;
		object = object_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getItem(id.get()) != null
						&& object.get().isOnScreen();
			}
		}));
	}

	// e.g. to filter certain Item -> Object strings
	public UseItemWith<T> setFilter(Filter<String> filter_) {
		filter = filter_;
		return this;
	}

	@Override
	public void run() {
		Item item = Inventory.getItem(id.get());
		if (item != null)
			item.getWidgetChild().interact("Use");
		T sceneObject = object.get();
		if (sceneObject != null) {
			if(!sceneObject.isOnScreen()) {
				Camera.setPitch(99);
				Camera.turnTo(sceneObject);
			}
			while (Mouse.getLocation().distance(sceneObject.getCentralPoint()) > 1)
				Mouse.move(sceneObject.getCentralPoint());
			if (filter == null) {
				Mouse.click(false);
				if (!Menu.select("Use")) {
					Time.sleep(100);
					sceneObject.interact("Use");
				}
			} else {
				for(String option : Menu.getOptions()) {
					if(filter.accept(option)) {
						sceneObject.interact("Use", option);
					}
				}
			}
		}
	}
}
