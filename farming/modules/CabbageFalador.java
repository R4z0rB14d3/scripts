package scripts.farming.modules;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.farming.requirements.ItemReq;
import scripts.farming.requirements.OneOf;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.Value;
import scripts.state.edge.AnimationPath;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.InteractItem;
import scripts.state.edge.InteractSceneObject;
import scripts.state.edge.Timeout;
import scripts.state.edge.Walk;

@Target("Falador")
public class CabbageFalador extends Module {

	public CabbageFalador(State INITIAL, State SUCCESS, State CRITICAL) {
		super(
				"Cabbage port",
				INITIAL,
				SUCCESS,
				CRITICAL,
				new OneOf(new ItemReq(Constants.ExplorersRing3, 1)
						, new ItemReq(Constants.ExplorersRing4, 1))
				);

		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State IN_FRONT_OF_GATE = new State();
		INITIAL.add(new AssureLocation(Condition.TRUE, new Tile(3054, 3289, 0),
				3, TELEPORTED));
		INITIAL.add(new InteractItem(Condition.TRUE, TELEPORTING,
				new Value<Item>() { public Item get() { return Inventory.getItem(Constants.ExplorersRing3); }}, "Cabbage-port"));
		INITIAL.add(new InteractItem(Condition.TRUE, TELEPORTING,
				new Value<Item>() { public Item get() { return Inventory.getItem(Constants.ExplorersRing4); }}, "Cabbage-port"));
		TELEPORTING.add(new AnimationPath(Condition.TRUE, new Integer[] { 9984,
				9986 }, INITIAL, new Timeout(INITIAL, 6000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE, new Tile(3054, 3289,
				0), 3, TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL, 12000));
		TELEPORTED.add(new Walk(Condition.TRUE, new Tile(3052, 3299, 0),
				IN_FRONT_OF_GATE, new Timeout(INITIAL, 5000)));
		IN_FRONT_OF_GATE.add(new InteractSceneObject(new Condition() {
			public boolean validate() {
				SceneObject gate = SceneEntities.getNearest(7049);
				return gate != null
						&& gate.getLocation().distance(Players.getLocal()) < 10;
			}
		}, IN_FRONT_OF_GATE, new Value<SceneObject>() {
			public SceneObject get() {
				return SceneEntities.getNearest(7049);
			}
		}, "Open"));
		IN_FRONT_OF_GATE.add(new Walk(Condition.TRUE, new Tile(3056, 3307, 0),
				SUCCESS, new Timeout(INITIAL, 5000)));

	}
}