package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import scripts.farming.requirements.Both;
import scripts.farming.requirements.ItemReq;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Catherby")
public class LunarCatherby extends Module {
	public LunarCatherby(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar Catherby teleport", INITIAL, SUCCESS, CRITICAL,
				new Both(new ItemReq(Constants.AstralRune, 0)
				, new Both(new ItemReq(Constants.LawRune, 0)
				, new ItemReq(Constants.MudBattleStaff, 1))));
		State TELEPORTED = new State();
		State TELEPORTING = new State();

		Tile[] path = new Tile[] { new Tile(2807, 3463, 0) };

		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(2803,3451,0),3,TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL,
				Magic.Lunar.TeleportCatherby));

		TELEPORTING.add(new Animation(Condition.TRUE, 9606, TELEPORTED,
				new Timeout(INITIAL, 10000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(2803,3451,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,8000));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}