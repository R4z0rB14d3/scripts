package scripts.state.edge;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.Value;

public class InteractSceneObject extends Task {

	Value<SceneObject> object;
	String interaction;

	Value<String> option = null;

	public InteractSceneObject(Condition c, State s,
			final Value<SceneObject> object_, String interaction_) {
		this(c, s, object_, interaction_, false);
	}

	public InteractSceneObject(final Condition c, State s,
			final Value<SceneObject> object_, String interaction_,
			final boolean turnCamera) {
		super(null, s);
		object = object_;
		interaction = interaction_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				if (turnCamera)
					Camera.turnTo(object.get());
				return object.get() != null && object.get().isOnScreen();
			}
		}));
	}

	// e.g. to filter certain Item -> Object strings
	public InteractSceneObject setOption(Value<String> option_) {
		option = option_;
		return this;
	}

	@Override
	public void run() {
		System.out.println("Interact scene object : " + object.get().getId()
				+ "/" + interaction + "/" + object.get().isOnScreen());
		SceneObject obj = object.get();
		if (obj == null)
			return;
		if (!obj.isOnScreen()) {
			Camera.setPitch(99);
			Camera.turnTo(obj);
		}
		if (option == null) {
			obj.interact(interaction);
		} else {
			obj.interact(interaction, option.get());
		}
	}
}
