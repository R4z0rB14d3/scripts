package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Equipment;
import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.Equip;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Trollheim")
public class LunarTrollheim extends Module {

	public LunarTrollheim(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar Trollheim teleport", INITIAL, SUCCESS, CRITICAL,
				new Requirement[] {
						new Requirement(0, Constants.AstralRune),
						new Requirement(0, Constants.LawRune),
						new Requirement(1, Constants.MudBattleStaff)
								.or(new Requirement(1,
										Constants.MysticMudBattleStaff)) });
		State TELEPORTED = new State();
		State TELEPORTING = new State();

		Tile[] path = new Tile[] { new Tile(2814, 3679, 0) };

		INITIAL.add(new Equip(Condition.TRUE, INITIAL,
				Constants.MudBattleStaff, Equipment.WEAPON, new Timeout(
						INITIAL, 5000)));

		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL,
				Magic.Lunar.TeleportTrollheim));
		TELEPORTING.add(new Animation(Condition.TRUE, 9606, TELEPORTED,
				new Timeout(INITIAL, 10000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}