package scripts.farming.modules;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Draynor")
public class DraynorMansionLoadstone extends Module {

	public DraynorMansionLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Draynor loadstone", INITIAL, SUCCESS, CRITICAL);

		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();

		Tile[] path = new Tile[] { 
				new Tile(3104,3305,0)
				,new Tile(3103,3312,0)
				,new Tile(3104,3319,0)
				,new Tile(3106,3326,0)
				,new Tile(3109,3333,0)
				,new Tile(3106,3342,0)
				,new Tile(3099,3346,0)
				,new Tile(3092,3347,0)
				,new Tile(3089,3354,0)
				,new Tile(3087,3361,0)
				};

		INITIAL.add(new AssureLocation(Condition.TRUE, new Tile(3105, 3298, 0),
				3, TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Standard.HomeTeleport));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 44).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Mouse.move(Widgets.get(1092, 44).getCentralPoint());
				Widgets.get(1092, 44).click(true);
				Time.sleep(700);
			}
		});
		CASTED.add(new Timeout(INITIAL,12000));
		// TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
		// new Timeout(INITIAL, 15000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE, new Tile(3105, 3298,
				0), 3, TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL, 15000));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}