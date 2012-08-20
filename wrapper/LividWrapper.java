package scripts.wrapper;

import org.ox5377656c6c.livid.Constants;
import org.ox5377656c6c.livid.doLivid;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.Magic;
import scripts.farming.ScriptWrapper;


@ScriptWrapper(banking = false)
public class LividWrapper {

	private static doLivid instance = null;
	public static ActiveScript getInstance() {
		if(instance == null) {
			instance = new doLivid();
		}
		return instance;
	}
	
	public static void prepare() {
		/* go to iLivid area */
		Item mudstaff = Inventory.getItem(6562);
		if (mudstaff != null)
			mudstaff.getWidgetChild().click(true);

		for(Area area : Constants.FARM_AREA) {
			if(area.contains(Players.getLocal())) return;
		}
		Magic.cast(Magic.Lunar.TeleportMoonClan.getWidgetId());
		Time.sleep(4300);
		Walking.walk(new Tile(2113,3929,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		Walking.walk(new Tile(2111,3939,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		Walking.walk(new Tile(2107,3945,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		//doLivid.RUN_TIME.reset();
	}

	public static void cleanup() {
		/* Destroy items so we have more space for herbs/.. */
		Item item;
		while((item = Inventory.getItem(Constants.LIVID_PLANT_SINGLE)) != null) {
			item.getWidgetChild().interact("Destroy");
			while(!Widgets.get(1183,27).validate()) Time.sleep(5);
			Widgets.get(1183,27).click(true);
			while(Widgets.get(1183,27).validate()) Time.sleep(5);
		}
		while((item = Inventory.getItem(Constants.LUNAR_LOGS)) != null) {
			item.getWidgetChild().interact("Destroy");
			while(!Widgets.get(1183,27).validate()) Time.sleep(5);
			Widgets.get(1183,27).click(true);
			while(Widgets.get(1183,27).validate()) Time.sleep(5);
		}		
		while((item = Inventory.getItem(Constants.LUNAR_PLANK)) != null) {
			item.getWidgetChild().interact("Destroy");
			while(!Widgets.get(1183,27).validate()) Time.sleep(5);
			Widgets.get(1183,27).click(true);
			while(Widgets.get(1183,27).validate()) Time.sleep(5);
		}
		while(Widgets.get(1081,0).isOnScreen()) {
			Mouse.click(Players.getLocal().getCentralPoint(),true);
		}
		Camera.setPitch(89);
		Time.sleep(1300);
	}


}