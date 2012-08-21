package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.bot.event.listener.PaintListener;

import scripts.farming.EntityWrapper;

@Manifest(authors = { "djabby" }, name = "PathRecorder", description = "s=start/stop,w=write,r=read,r=run", version = 1.37)
public class PathRecorder extends ActiveScript implements KeyListener,
		PaintListener {

	public class Path {
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
			if (!tilesEqual(current, Walking.getDestination()))
				Walking.walk(current);
			if (current.distance(Players.getLocal().getLocation()) <= inRange)
				i++;
		}
	}

	Path path = null;
	List<Tile> currentPath;
	Tile getLatest = null;

	enum State {
		NIL, COLLECT, STOPPED, RUNNING
	};

	int minimalDistance = 7;
	int inRange = 6;
	State state = State.NIL;

	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public <T extends Entity & Locatable> boolean safeInteract(T obj,
			final String interaction, Filter<String> filter) {
		if (obj != null && obj.isOnScreen()) {
			// move mouse to the object to get all options
			
			//Mouse.move(obj.getNextViewportPoint());
			Mouse.move(obj.getCentralPoint());
			String[] actions = Menu.getActions();
			String[] options = Menu.getOptions();
			int size = actions.length;
			for (int i=0;i<size;i++) {
				if (actions[i].equals(interaction) && filter.accept(options[i])) {
					return obj.interact(interaction, options[i]);
				}
			}
		}
		return false;
	}

	public <T extends Entity & Locatable> boolean safeUseWith(Item item, T obj,
			Filter<String> filter) {
		if (item != null) {
			// select only if item isn't selected yet
			if (item.getWidgetChild().getBorderThickness() < 2)
				item.getWidgetChild().interact("Use");
			System.out.println("And now try to use::");
			return safeInteract(obj, "Use", filter);
		} else {
			return false;
		}
	}

	class Constants {
		public static final int LANTADYME = 7218;
		public static final int LEPRECHAUN = 7557;
	}

	public void examples() {
		Inventory.getItem(5096).getWidgetChild().interact("Use");
		//SceneEntities.getNearest(7847).interact("Use","Marigold seed -> Flower Patch");
		SceneObject patch = SceneEntities.getNearest(7847);
		EntityWrapper vew = new EntityWrapper(patch);
		Mouse.apply(vew,new Filter<Point>() {
			public boolean accept(Point p) {
				return Menu.select("Use","Marigold seed -> Flower Patch");
			}
		});
		
		/** Example 1: Let the leprechaun note our lantadyme **/
		/*if (safeUseWith(Inventory.getItemAt(0),
				SceneEntities.getNearest(7848), new Filter<String>() {
					public boolean accept(String option) {
						return option.toLowerCase().contains("-> marigold");
					}
				})) {
			System.out.println("Lantadyme successfully noted");
		} else {
			System.out.println("Something went oh-so wrong :(");
		}*/
/*
		/// Example 2: Talk with the leprechaun, NOT with the gardener 
		if (safeInteract(NPCs.getNearest(Constants.LEPRECHAUN), "Talk-to",
				new Filter<String>() {
					public boolean accept(String option) {
						return option.equalsIgnoreCase("Tool leprechaun");
					}
				})) {
			System.out.println("Talking with leprechaun...");
		} else {
			System.out.println("F*ck this gardener!");
		}*/
	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'm':
			// actually not part of the path recorder
			// SceneObject pipe = SceneEntities.getNearest(20210);
			Mouse.click(false);
			Time.sleep(1000);
			// while(!Menu.isOpen()) Time.sleep(10);
			System.out.println("#actions=" + Menu.getActions().length);
			System.out.println("#options=" + Menu.getOptions().length);
			for (String action : Menu.getActions()) {
				System.out.println(action);
			}
			for (String action : Menu.getOptions()) {
				System.out.println(action);
			}
			break;
		case 's':
			switch (state) {
			case NIL:
			case STOPPED:
				currentPath = new ArrayList<Tile>();
				state = State.COLLECT;
				break;
			case COLLECT:
			case RUNNING:
				state = State.STOPPED;
				getLatest = null;
				path = null;
				break;
			}
			break;
		case 'w':
			if (state != State.NIL) {
				System.out.println("new Tile[] {");
				boolean b = false;
				Tile lastTile = null;
				for (Tile tile : currentPath) {
					// filter wrong tiles
					if (lastTile != null
							&& tile.distance(lastTile) < minimalDistance * 1.5) {
						System.out.print(b ? "," : "");
						System.out.println("new Tile(" + tile.getX() + ","
								+ tile.getY() + "," + tile.getPlane() + ")");
						b = true;
					}
					lastTile = tile;
				}
				System.out.println("};");
			}
			break;
		case 'r':
			if (state == State.STOPPED) {
				state = State.RUNNING;
				path = new Path(currentPath);
			}
		}

	}

	BufferedImage proggy;

	public void setup() {
		BufferedImage img;
		try {
			File imgPath = new File("src/scripts/images/proggy.png");
			if (imgPath.exists()) {
				System.out.println("File found!");
				img = ImageIO.read(imgPath);
				int w = img.getWidth(null);
				int h = img.getHeight(null);
				System.out.println("Width: " + h);
				proggy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics g = proggy.getGraphics();
				g.drawImage(img, 0, 0, null);
			}
		} catch (IOException e) {
			proggy = new BufferedImage(0,0,BufferedImage.TYPE_INT_RGB);
			e.printStackTrace();
		}

		provide(new Collect());
		provide(new Running());
		provide(new Example());
	}
	
	public class Example extends Strategy implements Task {
		public void run() {
			try {
			examples();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public boolean validate() {
			return true;
		}
	}

	public class Collect extends Strategy implements Task {
		public void run() {
			if (currentPath.size() == 0) {
				getLatest = Players.getLocal().getLocation();
				currentPath.add(getLatest);
			} else if (!tilesEqual(Players.getLocal().getLocation(), getLatest)
					&& getLatest.distance(Players.getLocal().getLocation()) > minimalDistance) {
				getLatest = Players.getLocal().getLocation();
				currentPath.add(getLatest);
			}
		}

		public boolean validate() {
			return state == State.COLLECT;
		}
	}

	public class Running extends Strategy implements Task {
		public void run() {
			path.run();
		}

		public boolean validate() {
			return state == State.RUNNING && !path.isFinished();
		}
	}

	public static boolean tilesEqual(Tile tile1, Tile tile2) {
		return tile1.getX() == tile2.getX() && tile1.getY() == tile2.getY()
				&& tile1.getPlane() == tile2.getPlane();
	}

	@Override
	public void onRepaint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 510, 200, 30);
		Point p = Mouse.getLocation();
		g.drawLine(0, (int)p.getY(), 640, (int)p.getY());
		g.drawLine((int)p.getX(), 0,(int) p.getX(), 480);
		try {
			float[] scales = { 1f, 1f, 1f, 0.95f };
			float[] offsets = new float[4];
			RescaleOp rescale = new RescaleOp(scales, offsets, null);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(proggy, rescale, 0, 390);
			//g2d.drawImage(proggy, 6, 395, null);
			// g2d.drawImage(proggy, rescale, 6, 395);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if (state == State.NIL)
			return;
		for (Tile t : currentPath) {

			Polygon[] bounds = t.getBounds();
			if (bounds.length == 1) {
				g.setColor(Color.RED);
				g.fillPolygon(bounds[0]);

			}
		}

	}

}
