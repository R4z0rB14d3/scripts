package scripts.farming.modules;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.requirements.ItemReq;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.Value;
import scripts.state.edge.AnimationPath;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.Either;
import scripts.state.edge.InteractItem;
import scripts.state.edge.Notification;
import scripts.state.edge.Timeout;
import scripts.state.edge.UseItem;
import scripts.state.edge.WalkPath;

@Target("Morytania")
public class EctovialMorytania extends Module {
	public EctovialMorytania(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Ectovial", INITIAL, SUCCESS, CRITICAL,
				new ItemReq(Constants.Ectophial, 1)); // Integer[]
																				// refillAnimations
																				// =
																				// new
																				// Integer[]
																				// {
																				// 9609,
																				// 8939,
																				// 8941,
																				// 832
																				// };
		Integer[] refillAnimations = new Integer[] { 832 };

		Tile[] path = new Tile[] { new Tile(3658, 3522, 0),
				new Tile(3659, 3529, 0), new Tile(3651, 3529, 0),
				new Tile(3642, 3529, 0), new Tile(3635, 3531, 0),
				new Tile(3628, 3533, 0), new Tile(3587, 3541, 0),
				new Tile(3627, 3533, 0), new Tile(3619, 3533, 0),
				new Tile(3611, 3533, 0), new Tile(3606, 3528, 0) };

		/**
		 * teleporting done > refill vial
		 */
		State ECTOFUNTUS_REFILL = new State();

		/**
		 * check vial state > if refilled, proceed > if not refilled, try to
		 * fill manually
		 */
		State ECTOFUNTUS_CHECK = new State();

		/**
		 * the refilling failed somehow > refill vial manually
		 */
		State ECTOFUNTUS_REFILL_MANUALLY = new State();

		/**
		 * refilling done > run to farming patch
		 */
		State ECTOFUNTUS_DONE = new State();

		/**
		 * something went wrong > try again
		 */
		State FAIL = new State();
		INITIAL.add(new AssureLocation(Condition.TRUE, new Tile(3661, 3522, 0),
				4, ECTOFUNTUS_CHECK));
		INITIAL.add(new InteractItem(Condition.TRUE, ECTOFUNTUS_REFILL,
				new Value<Item>() { public Item get() { return Inventory.getItem(Constants.Ectophial); }}, "Empty"));
		ECTOFUNTUS_REFILL.add(
				new AnimationPath(Condition.TRUE, refillAnimations,
						ECTOFUNTUS_CHECK, new Timeout(FAIL, 12000))).add(
				new Timeout(FAIL, 12000));

		ECTOFUNTUS_CHECK.add(new Either(new Condition() {
			public boolean validate() {
				return Inventory.getCount(4251) == 1;
			}
		}, ECTOFUNTUS_DONE, ECTOFUNTUS_REFILL_MANUALLY));

		ECTOFUNTUS_REFILL_MANUALLY.add(new UseItem(Condition.TRUE,
				ECTOFUNTUS_REFILL, 4251, 12345));

		ECTOFUNTUS_DONE.add(new WalkPath(Condition.TRUE, path, SUCCESS,
				new Timeout(FAIL, 10000)));

		FAIL.add(new Notification(Condition.TRUE, INITIAL, "Fail, try again!"));

	}
}