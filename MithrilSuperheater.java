package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "djabby" }, name = "MithrilSuperheater", description = "Superheats mithril", version = 1.00)
public class MithrilSuperheater extends ActiveScript implements PaintListener,
		MessageListener {

	@Override
	protected void setup() {
		timer = new Timer(0);
		Superheat fb = new Superheat();
		provide(new Strategy(fb, fb));

		Withdraw w = new Withdraw();
		provide(new Strategy(w, w));
	}

	public class Withdraw extends Strategy implements Condition, Task {

		@Override
		public void run() {
			int xPos, yPos;
			Timer start = new Timer(0);
			Timer bankTimer = new Timer(0);
			System.out.print("N");
			if (Bank.getNearest() == null || !Bank.getNearest().isOnScreen()) {
				Walking.walk(new Tile(3147, 3502, 0));
			}
			bank = Bank.getNearest().getCentralPoint();
			System.out.println("D");
			while (!Widgets.get(763, 0).validate() && isRunning()) {
				if (!bankTimer.isRunning()) {
					xPos = (int) bank.getX();// + Random.nextInt(-1,1);
					yPos = (int) bank.getY();// + Random.nextInt(-1,1);
					Mouse.hop(xPos, yPos);
					Mouse.click(false);
					Time.sleep(10, 25);
					Mouse.hop(xPos, yPos + 48);
					Mouse.click(true);
					bankTimer.setEndIn(600);
					if (Bank.getNearest() == null
							|| !Bank.getNearest().isOnScreen()) {
						Walking.walk(new Tile(3147, 3502, 0));
					}
					bank = Bank.getNearest().getCentralPoint();
				}
				Time.sleep(5);
			}
			// Bank.open();
			// if (Inventory.getCount(2359) > 0) {
			int cnt = 0;
			Point mithrilBar = new Point(578, 313);
			/*
			 * for (WidgetChild wdg : Widgets.get(763, 0).getChildren()) { if
			 * (wdg.getChildId() == 2359) { cnt++; mithrilBar =
			 * wdg.getCentralPoint(); } }
			 */
			// System.out.println("COUNT" = cnt);
			// if(cnt>0) {
			xPos = (int) mithrilBar.getX();// + Random.nextInt(-3,3);
			yPos = (int) mithrilBar.getY();// + Random.nextInt(-3,3);
			System.out.print("D");
			Mouse.hop(xPos, yPos);
			Timer cancelTimer = new Timer(600);
			while (!Menu.contains("Deposit-5", "Mithril bar") && isRunning()) {
				String option1 = Menu.getOptions()[0];
				if (Menu.getActions()[0].equals("Cancel")
						&& !cancelTimer.isRunning())
					break;
				System.out.print("(" + option1 + ")");
				if (option1.equals("Mithril ore")) {
					if (Inventory.getCount(2359) > 0)
						while (!Bank.deposit(2359, 5) && isRunning())
							Time.sleep(5);
					if (Inventory.getCount(447) > 0)
						while (!Bank.deposit(447, 5) && isRunning())
							Time.sleep(5);
					break;
				} else if (option1.equals("Coal")) {
					if (Inventory.getCount(2359) > 0)
						while (!Bank.deposit(2359, 5) && isRunning())
							Time.sleep(5);
					if (Inventory.getCount(453) > 0)
						while (!Bank.deposit(453, 5) && isRunning())
							Time.sleep(5);
					break;
				}
				Mouse.hop(xPos, yPos);
				Time.sleep(5);
			}
			Mouse.click(false);
			Time.sleep(20, 25);
			Mouse.hop(xPos, yPos + 48);
			Mouse.click(true);
			// }

			// while (!Bank.deposit(2359, 5) && isRunning()) {
			// Bank.open();
			// }
			barsProduced += 5;
			// }
			// while(!Bank.withdraw(447, 5)) Bank.open();
			if (mithril == null || coal == null) {
				mithril = Bank.getItem(447).getWidgetChild().getCentralPoint();
				coal = Bank.getItem(453).getWidgetChild().getCentralPoint();
			}
			// Mouse.hop((int) Math.round(mithril.getX()),
			// (int) Math.round(mithril.getY()));
			// Mouse.click(false);
			// mithril.interact("Withdraw-5");
			xPos = (int) mithril.getX() + Random.nextInt(-2, 2);
			yPos = (int) mithril.getY() + Random.nextInt(-2, 2);
			System.out.print("W");
			Mouse.hop(xPos, yPos);
			while (!Menu.contains("Withdraw-5", "Mithril ore") && isRunning()) {
				Mouse.hop(xPos, yPos);
				Time.sleep(5);
			}
			Mouse.click(false);
			Time.sleep(20, 25);
			Mouse.hop(xPos, yPos + 48);
			Mouse.click(true);

			xPos = (int) coal.getX() + Random.nextInt(-1, 1);
			System.out.print("W");
			yPos = (int) coal.getY() + Random.nextInt(-1, 1);
			Mouse.hop(xPos, yPos);
			cancelTimer.reset();
			while (!Menu.contains("Withdraw-All", "Coal") && isRunning()) {
				if (Menu.getActions()[0].equals("Cancel")
						&& !cancelTimer.isRunning()) {
					Mouse.click(false);
					Time.sleep(20, 25);
					Mouse.hop(xPos, yPos + 28);
					Mouse.click(true);
				}
				Mouse.hop(xPos, yPos);
				Time.sleep(5);
			}
			Mouse.click(false);
			Time.sleep(20, 25);
			Mouse.hop(xPos, yPos + 108);
			Mouse.click(true);
			// Menu.select("Withdraw-5");
			// Mouse.hop(450, 300);
			// Menu.select("Withdraw-All");
			// while(!Bank.withdraw(453, 0)) Bank.open();

			xPos = (int) bankclose.getX() + Random.nextInt(-1, 1);
			yPos = (int) bankclose.getY() + Random.nextInt(-1, 1);
			System.out.println("C");
			Mouse.hop(xPos, yPos);
			while (!Menu.contains("Close") && isRunning()) {
				Mouse.hop(xPos, yPos);
				Time.sleep(5);
			}
			Mouse.click(true);

			mithrilOres = 5;
			// System.out.println(getItemAt(4));
			// Time.sleep(1000);
			long time = start.getElapsed();
			if (time > maxBankTime) {
				maxBankTime = maxBankTime == 0 ? 1 : time;
			}

		}

		public boolean validate() {
			return mithrilOres == 0;
		}

	}

	Point bank = null, coal = null, mithril = null, bankclose = new Point(490,
			87);
	Timer timer;
	int mithrilOres = 5;
	int barsProduced = 0;
	long maxBankTime = 0;
	long maxSuperHeatTime = 0;
	long totalBankTime = 0;
	long totalSuperHeatTime = 0;

	public class Superheat extends Strategy implements Condition, Task {

		@Override
		public void run() {
			Timer start = new Timer(0);
			while (!Tabs.MAGIC.open() && isRunning())
				Time.sleep(10);
			Point myPoint = new Point(578, 313);
			Timer clickTimer = new Timer(0);
			Timer endTimer = new Timer(8000);
			int lastamount = -1;
			while (endTimer.isRunning() && isRunning()) {
				Timer castTimer = new Timer(1000);
				Mouse.hop((int) myPoint.getX(), (int) myPoint.getY());
				boolean cast = false;
				boolean onore = false;
				while (!((onore = Menu.contains("Cast",
						"Superheat Item -> Mithril Ore")) || (Menu.contains(
						"Cast", "Superheat Item") && Menu.getOptions()[0]
						.equals("Superheat Item")))
						&& isRunning()) {
					String option1 = Menu.getOptions()[0];
					if (option1.equals("Mithril bar")
							|| option1.equals("Superheat Item -> Mithril bar")
							&& !castTimer.isRunning()) {
						while (!Tabs.MAGIC.open() && isRunning())
							Time.sleep(10);
						break;
					}
					if (Menu.getActions()[0].equals("Use")
							&& !castTimer.isRunning()) {
						while (!Tabs.MAGIC.open() && isRunning())
							Time.sleep(10);
						castTimer.reset();
					}
					Mouse.hop((int) myPoint.getX(), (int) myPoint.getY());
					Time.sleep(5);
				}
				if (!clickTimer.isRunning()) {
					int amount = 2;
					if (Tabs.INVENTORY.isOpen()) {
						amount = Inventory.getCount(447);
						if (amount == 0) {
							break;
						}
						// System.out.println("Amount: " + amount);
						if (lastamount == amount)
							return;
						lastamount = amount;
					}
					Mouse.hop((int) myPoint.getX(), (int) myPoint.getY());
					System.out.print("C");
					Mouse.click(true);
					if (amount == 1) {
						break;
					}
					clickTimer.setEndIn(100);
				}
			}
			long time = start.getElapsed();
			if (time > maxSuperHeatTime) {
				maxSuperHeatTime = maxSuperHeatTime == 0 ? 1 : time;
			}
			mithrilOres = 0;
			Time.sleep(600);
			System.out.println("D");
		}

		public boolean validate() {
			return mithrilOres > 0;
		}

	}

	public void onRepaint(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 110, 135);
		g.setColor(Color.BLACK);
		g.drawString("Time: " + timer.toElapsedString(), 7, 18);
		g.drawString("Bars: " + barsProduced, 7, 38);
		g.drawString(
				"/H: "
						+ Math.round((float) barsProduced
								/ (timer.getElapsed() + 1) * 3600000), 7, 58);
		g.drawString("mx Bank: " + maxBankTime, 7, 78);
		g.drawString("mx Heat: " + maxSuperHeatTime, 7, 98);
		// g.drawString("avg Bank: " + totalBankTime/(mithrilOres*5+1), 7, 118);
		// g.drawString("avg Heat" + totalSuperHeatTime/(mithrilOres*5+1), 7,
		// 138);
	}

	@Override
	public void messageReceived(MessageEvent messageevent) {
		if (messageevent.getMessage().contains("four heaps of coal")) {
			mithrilOres = 0;
		}
	}

}