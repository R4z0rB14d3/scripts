package scripts.wrapper;

import java.util.Arrays;
import java.util.List;

import org.itbarbfisher.iTBarbFisher;
import org.itbarbfisher.strategies.Drop;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.Magic;
import scripts.farming.ScriptWrapper;
import scripts.farming.modules.Constants;
import scripts.farming.requirements.Both;
import scripts.farming.requirements.ItemReq;
import scripts.farming.requirements.Requirement;

@ScriptWrapper(banking = false)
public class ITBarbFish {

	private static iTBarbFisher instance = null;

	public static ActiveScript getInstance() {
		if (instance == null) {
			instance = new iTBarbFisher();
		}
		return instance;
	}

	public static Requirement<?> getRequirements() {
		return new Both(new ItemReq(Constants.FireRune, 0)
		, new ItemReq(Constants.Feather, 0));
	}

	public static void prepare() {

		/*Item mudstaff = Inventory.getItem(6562);
		if (mudstaff != null)
			mudstaff.getWidgetChild().click(true);

		Magic.cast(Magic.Lunar.TeleportBarbarian.getWidgetId());*/

		Path path = new Path(Arrays.asList(new Tile[] {
				new Tile(2541, 3560, 0), new Tile(2534, 3558, 0),
				new Tile(2527, 3557, 0), new Tile(2527, 3549, 0),
				new Tile(2527, 3541, 0), new Tile(2522, 3531, 0),
				new Tile(2517, 3526, 0), new Tile(2512, 3521, 0),
				new Tile(2504, 3520, 0), new Tile(2499, 3514, 0) }));
		while (!path.isFinished())
			path.run();

		Camera.setAngle(220);
	}

	public static void cleanup() {
		/* Destroy items so we have more space for herbs/.. */
		Drop drop = new Drop();
		drop.run();
		Camera.setPitch(89);
		Time.sleep(1300);
	}

	public static class Path {
		List<Tile> nodes;
		int i;

		public Path(List<Tile> nodes_) {
			nodes = nodes_;
			i = 0;
		}

		public boolean isFinished() {
			return i >= nodes.size();
		}

		public void run() {
			Tile current = nodes.get(i);
			if (current.distance(Walking.getDestination()) >= 1)
				Walking.walk(current);

			if (current.distance(Players.getLocal().getLocation()) <= 6) {
				i++;
			} else if (current.distance(Players.getLocal().getLocation()) >= 50) {
				i = 0;
				current = nodes.get(i);
				if (current.distance(Players.getLocal().getLocation()) >= 50) {
					Item mudstaff = Inventory.getItem(6562);
					if (mudstaff != null)
						mudstaff.getWidgetChild().click(true);

					Magic.cast(Magic.Lunar.TeleportBarbarian.getWidgetId());
					Time.sleep(5000);
				}
			}
		}
	}

}