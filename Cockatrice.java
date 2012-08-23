package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "djabby" }, name = "Cockatrice", description = "Eggs->Cockatrice Eggs", version = 1.00)
public class Cockatrice extends ActiveScript implements PaintListener,
		KeyListener, MessageListener {

	Timer timer = new Timer(0);
	int count = 0;
	int lastcount = 0;
	boolean suicide = true;

	@Override
	protected void setup() {
		provide(new Withdraw());
		provide(new Summon());
		provide(new Drink());
		provide(new Produce());
		lastcount = -Inventory.getCount(12109);
	}

	public class Withdraw extends Strategy implements Task {

		public void run() {
			int countVials = Inventory.getCount(229);
			int countSummDrinks = Inventory.getCount(12146)
					+ Inventory.getCount(12144) * 2 + Inventory.getCount(12142)
					* 3 + Inventory.getCount(12140) * 4;
			int countCockatriceEggs = Inventory.getCount(12109);
			int countPouches = Inventory.getCount(12015);

			if (!Bank.open()) {
				System.out.println("Failed to open bank. End script");
				stop();
				return;
			}
			if (countVials > 2 || (countSummDrinks < 6 && countVials > 0)) {
				Timer bankTimer = new Timer(1000);
				while(!Bank.deposit(229, 0)) {
					if(!bankTimer.isRunning()) {
						System.out.println("Failed to store empt vials. Try later");
						return;
					}
				}
			}
			if (countSummDrinks < 6) {
				Timer bankTimer = new Timer(1000);
				while(!Bank.deposit(12140, 5)) {
					if(!bankTimer.isRunning()) {
						System.out.print("Failed to withdraw summoning pots: ");
						if (Bank.getItem(12140) == null) {
							System.out
									.println("Out of summoning pots (4). End script");
							stop();
						}
						System.out.println("Try later");
						return;
					}
				}	

			}
			if (countCockatriceEggs > 0) {
				Timer bankTimer = new Timer(1000);
				while(true) {
					if(!bankTimer.isRunning()) {
						System.out.println("Failed to store cockatrice eggs. Try later");
						return;
					}
					if(Bank.deposit(12109, 0)) {
						lastcount = count;
						break;
					}
				}
			}
			if (Widgets.get(747, 0).getTextureId() == 1244) {
				Timer bankTimer = new Timer(1000);
				while(!Bank.deposit(12109, 0)) {
					if(!bankTimer.isRunning()) {
						System.out.print("Failed to withdraw pouch: ");
						if (Bank.getItem(12015) == null) {
							System.out.println("Out of eggs. End script");
							stop();
							return;
						}
						System.out.println("Try later");
						return;
					}
				}				
			}
			if (!Bank.withdraw(1944, 0)) {
				Timer bankTimer = new Timer(1000);
				while(!Bank.deposit(12109, 0)) {
					if(!bankTimer.isRunning()) {
						System.out.print("Failed to withdraw eggs: ");
						if (Bank.getItem(1944) == null) {
							System.out.println("Out of eggs. End script");
							stop();
						}
						System.out.println("Try later");
						return;
					}
				}	
			}
			Bank.close();
		}

		public boolean validate() {
			return Inventory.getCount(1944) == 0 || Settings.get(1177) <= 3;
		}

	}

	public class Summon extends Strategy implements Task {

		public void run() {
			Item item = Inventory.getItem(12015);
			if (item != null) {
				item.getWidgetChild().interact("Summon");
			}
		}

		public boolean validate() {
			return Inventory.getCount(12015) > 0
					&& Widgets.get(747, 0).getTextureId() == 1244;
		}
	}

	public class Drink extends Strategy implements Task {

		public void run() {
			Item item = Inventory.getItem(12146, 12144, 12142, 12140);
			if (item != null) {
				item.getWidgetChild().interact("Drink");
			}
		}

		public boolean validate() {
			return Settings.get(1177) < 30;
		}
	}

	public class Produce extends Strategy implements Task {

		public Item lastEgg = null;
		public Timer suicideCastTimer = new Timer(0);

		public void run() {
			if (!suicide) {
				Widgets.get(747, 2).interact("Cast", "Ophidian Incubation");
				Item egg = Inventory.getItem(1944);
				if (egg != null) {
					egg.getWidgetChild().interact("Cast", "Ophidian Incubation -> Egg");
				}
			} else if(!suicideCastTimer.isRunning()){
				Timer castTimer = new Timer(500);
				Point p = randomizePoint(Widgets.get(747, 2).getCentralPoint(),
						1);
				Mouse.hop((int) p.getX(), (int) p.getY());
				while (!Menu.getOptions()[0].equals("Ophidian Incubation")
						&& castTimer.isRunning()) {
					Mouse.hop((int) p.getX(), (int) p.getY());
					Time.sleep(10);
				}
				if (!Menu.getActions()[0].equals("Cast")) {
					System.out
							.println("Casting scroll has to be the left-click option in suicide mode. Ignore");
					return;
				}
				Mouse.click(true);
				Item[] eggs = Inventory.getItems(new Filter<Item>() {
					public boolean accept(Item item) {
						return item.getId() == 1944 && item != lastEgg;
					}
				});
				Item egg;
				if (eggs.length == 0) {
					// Try last egg again
					if (lastEgg.getId() == 1944) {
						egg = lastEgg;
					} else {
						System.out.println("No egg found");
						return;
					}
				} else {
					egg = eggs[0];
				}

				p = randomizePoint(egg.getWidgetChild().getCentralPoint(), 4);
				Mouse.hop((int) p.getX(), (int) p.getY());
				castTimer.reset();
				while (!Menu.contains("Cast", "Ophidian Incubation -> Egg")
						&& castTimer.isRunning()) {
					if(Menu.contains("Cast","Ophidian Incubation -> Cockatrice egg")) {
						egg = Inventory.getItem(1944);
						p = randomizePoint(egg.getWidgetChild().getCentralPoint(), 4);
						castTimer.reset();
					}
					Mouse.hop((int) p.getX(), (int) p.getY());
					Time.sleep(10);
				}
				Mouse.click(true);
				suicideCastTimer.setEndIn(500);
			}
			count = lastcount + Inventory.getCount(12109);
		}

		public boolean validate() {
			return Inventory.getCount(1944) > 0 && Settings.get(1177) > 3;
		}

	}

	private Point randomizePoint(Point p, int x) {
		return new Point((int) Math.round(p.getX() + Random.nextInt(-x, x)),
				(int) Math.round(p.getY() + Random.nextInt(-x, x)));
	}

	@Override
	public void onRepaint(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 150, 65);
		g.setColor(Color.BLACK);
		g.drawString("Time: " + timer.toElapsedString(), 7, 18);
		g.drawString("Eggs: " + count, 7, 33);
		g.drawString("Suicide ['s']: " + suicide, 7, 63);
		g.drawString(Math.round((float) count
								/ (timer.getElapsed() + 1) * 3600000) +"/H", 7, 48);
	}

	@Override
	public void keyPressed(KeyEvent k) {

	}

	@Override
	public void keyReleased(KeyEvent k) {

	}

	@Override
	public void keyTyped(KeyEvent k) {
		if (k.getKeyChar() == 's') {
			suicide = !suicide;
		}
	}

	@Override
	public void messageReceived(MessageEvent messageevent) {
		if (messageevent.getMessage().contains("too big to summon here")) {
			System.out.println("Bad location. End script");
			stop();
		}
	}

}