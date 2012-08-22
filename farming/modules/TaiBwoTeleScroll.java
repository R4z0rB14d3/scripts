package scripts.farming.modules;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.Value;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.InteractItem;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("TaiBwoWannai")
public class TaiBwoTeleScroll extends Module{

	public TaiBwoTeleScroll(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Teleport scroll",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		
		Tile[] path = new Tile[] {
				new Tile(2800,3088,0)
				,new Tile(2799,3095,0)
				,new Tile(2794,3100,0)
				};
	
		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(2792,3081,0),3,TELEPORTED));
		INITIAL.add(new InteractItem(Condition.TRUE,TELEPORTING, new Value<Item>() {
			public Item get() {
				return Inventory.getItem(19479);
			}
		},"Read"));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(2792,3081,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,15000));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}