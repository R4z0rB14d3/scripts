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

@Target("Cactus")
public class AlKharidLoadstone extends Module{

	public AlKharidLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Al Kharid Loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
		Tile[] path = new Tile[] {
				new Tile(3304,3183,0)
				,new Tile(3307,3190,0)
				,new Tile(3310,3197,0)
				,new Tile(3314,3203,0)
				};

	
		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(3297,3184,0),4,TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Standard.HomeTeleport));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 40).validate();
			}
		}, TELEPORTING) {
			public void run() {
				Mouse.move(Widgets.get(1092,40).getCentralPoint());
				Mouse.click(true);
			}
		});

		CASTED.add(new Timeout(INITIAL,12000));
		//TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
		//		new Timeout(INITIAL, 15000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(3297,3184,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,15000));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}