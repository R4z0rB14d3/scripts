package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "djabby" }, name = "MithrilSuperheater", description = "Superheats mithril", version = 1.00)
public class MithrilSuperheater extends ActiveScript implements PaintListener,
		MessageListener {

	int error = 0;
	int urns = 0;

	@Override
	protected void setup() {
		timer = new Timer(0);
		Superheat fb = new Superheat();
		provide(new Strategy(fb, fb));

		Withdraw w = new Withdraw();
		provide(new Strategy(w, w));
	}

	public Map<String, Point> pointCache = new HashMap<String, Point>();

	public class BankingAction {
		WidgetChild wc;
		String name;
		String ia;

		public BankingAction(WidgetChild wc_, String name_, String ia_) {
			wc = wc_;
			name = name_;
			ia = ia_;
		}

		public boolean run() {
			String key = name + ia.charAt(0);
			if (wc == null || !wc.validate()) {
				System.out.println("Error: " + this);
				error++;
				return false;
			}

			int xPos;
			int yPos;
			if (!pointCache.containsKey(key)) {
				xPos = (int) wc.getAbsoluteX() + wc.getWidth() / 2;
				yPos = (int) wc.getAbsoluteY() + wc.getHeight() / 2;
				if (xPos == 46 && (yPos == 151 || yPos == -479)) {
					error++;
					return false;
				}
				if (ia.charAt(0) == 'W') {
					System.out.println("Cached: " + key + " => " + xPos + "/"
							+ yPos);
					pointCache.put(key, new Point(xPos, yPos));
				}
			} else {
				Point cachedPoint = pointCache.get(key);
				xPos = (int) cachedPoint.getX();
				yPos = (int) cachedPoint.getY();
			}
			System.out.println(wc.getIndex() + "|" + wc.getAbsoluteX() + "|"
					+ wc.getAbsoluteY());
			System.out.println("Run: " + this + " -> " + xPos + "/" + yPos);
			Mouse.hop(xPos, yPos);
			Timer actionTimer = new Timer(600);
			while (!(Menu.contains("Deposit", name) || Menu.contains(ia, name))
					&& isRunning()) {
				Mouse.hop(xPos, yPos);
				Time.sleep(5);
				if (!actionTimer.isRunning()) {
					error++;
					return false;
				}
			}
			if (Menu.contains(ia, name)) {
				Mouse.click(false);
				Time.sleep(20, 25);
				int n = Menu.getActions().length;
				String[] act = Menu.getActions();
				String[] opt = Menu.getOptions();
				int index = 0;
				for (int i = 0; i < n; i++) {
					if (act[i].equals(ia) && opt[i].equals(name)) {
						index = i;
						break;
					}
				}
				System.out.println(index);
				// Mouse.hop(xPos, yPos + index * 16);

				if (ia.contains("All"))
					Mouse.hop(xPos, yPos + 108);
				else if (ia.contains("10"))
					Mouse.hop(xPos, yPos + 60);
				else if (ia.contains("5"))
					Mouse.hop(xPos, yPos + 44);
				else
					Mouse.hop(xPos, yPos + 28);

				Mouse.click(true);
			} else {
				Mouse.click(true);
			}
			return true;
		}

		public String toString() {
			return "[" + name + "," + ia + "]";
		}
	}

	public class BankingStrategy {
		List<BankingAction> actions = new ArrayList<BankingAction>();

		public void add(BankingAction ba) {
			actions.add(ba);
		}

		public void run() {
			for (BankingAction ba : actions) {
				ba.run();
			}
		}

		public String toString() {
			String s = "{";
			for (BankingAction ba : actions) {
				s += ba + "\n";
			}
			return s + "}";
		}
	}

	boolean teleUrn = false;

	public class Withdraw extends Strategy implements Condition, Task {

		@Override
		public void run() {
			try {
				int xPos, yPos;
				if (teleUrn) {
					Item urn = Inventory.getItem(20288);
					if (urn != null) {
						if (new BankingAction(urn.getWidgetChild(),
								"Smelting urn (full)", "Teleport urn").run())
							teleUrn = false;
					}
				}
				Timer start = new Timer(0);
				Timer bankTimer = new Timer(0);
				System.out.print("N");
				if (Bank.getNearest() == null
						|| !Bank.getNearest().isOnScreen()) {
					Walking.walk(new Tile(3147, 3502, 0));
				}
				bank = Bank.getNearest().getCentralPoint();
				System.out.println("D");
				openbank: while ((!Widgets.get(763, 0).isOnScreen() || !Widgets
						.get(762, 1).isOnScreen()) && isRunning()) {
					if (!bankTimer.isRunning()) {
						xPos = (int) bank.getX() + Random.nextInt(-3, 3);
						yPos = (int) bank.getY() + Random.nextInt(-3, 3);
						Mouse.hop(xPos, yPos);
						Mouse.click(false);
						while (!Menu.contains("Bank") && isRunning()) {
							Mouse.hop(xPos, yPos);
							if (Widgets.get(763, 0).isOnScreen()
									&& Widgets.get(762, 1).isOnScreen())
								break openbank;
							Time.sleep(5);
						}
						if (Menu.getActions()[0].equals("Bank"))
							Mouse.click(true);
						else {
							Mouse.click(false);
							Mouse.hop(xPos, yPos + 44);
							Mouse.click(true);
						}
						bankTimer.setEndIn(600);
						if (Bank.getNearest() == null
								|| !Bank.getNearest().isOnScreen()) {
							Walking.walk(new Tile(3147, 3502, 0));
						}
						bank = Bank.getNearest().getCentralPoint();
					}
					Time.sleep(5);
				}
				System.out.println("BS");
				BankingStrategy strategy = new BankingStrategy();
				BankingAction depositBars = null;
				WidgetChild firstOre = null;
				int countOre = 0;
				WidgetChild firstCoal = null;
				int countCoal = 0;
				int countBar = 0;
				int countUrn = 0;
				int countFullUrns = 0;
				WidgetChild misplacedUrn = null;
				WidgetChild firstUrn = null;
				WidgetChild bankInventory = Widgets.get(763, 0);
				if (bankInventory != null) {
					for (WidgetChild wdg : Widgets.get(763, 0).getChildren()) {
						// System.out.print("(" + wdg.getIndex() + ")");
						if (wdg.getChildId() == 2359) {
							countBar++;
							if (depositBars == null)
								depositBars = new BankingAction(wdg,
										"Mithril bar", "Deposit-5");
						} else if (wdg.getChildId() == 447) {
							countOre++;
							if (firstOre == null)
								firstOre = wdg;
						} else if (wdg.getChildId() == 453
								&& wdg.getIndex() < 10) {
							countCoal++;
							if (firstCoal == null)
								firstCoal = wdg;
						} else if (wdg.getChildId() == 20286
								|| wdg.getChildId() == 20287) {
							countUrn++;
							if (wdg.getIndex() < 5)
								misplacedUrn = wdg;
							if (firstUrn == null)
								firstUrn = wdg;
						} else if (wdg.getChildId() == 20288) {
							countFullUrns++;
						}
					}
				}
				// if (mithril == null || coal == null) {

				Item urn = null;
				while (Bank.getItem(447) == null && isRunning())
					Time.sleep(50);

				mithril = Bank.getItem(447);
				coal = Bank.getItem(453);
				urn = Bank.getItem(20286);
				Item startedUrn = Bank.getItem(20287);

				// }
				System.out.println("ABA");

				if (depositBars != null)
					strategy.add(depositBars);
				if (misplacedUrn != null) {
					error++;
					if (misplacedUrn.getChildId() == 20286)
						strategy.add(new BankingAction(misplacedUrn,
								"Smelting urn (r)", "Deposit-1"));
					else {
						strategy.add(new BankingAction(misplacedUrn,
								"Smelting urn", "Deposit-1"));
					}
				}
				if (countOre > 5) {
					error++;
					strategy.add(new BankingAction(firstOre, "Mithril ore",
							"Deposit-10"));
				} else if (countOre > 1) {
					error++;
					strategy.add(new BankingAction(firstOre, "Mithril ore",
							"Deposit-5"));
				} else if (countOre == 1) {
					error++;
					strategy.add(new BankingAction(firstOre, "Mithril ore",
							"Deposit-1"));
				}
				if (countCoal > 5) {
					error++;
					strategy.add(new BankingAction(firstCoal, "Coal",
							"Deposit-10"));
				} else if (countCoal > 1) {
					error++;
					strategy.add(new BankingAction(firstCoal, "Coal",
							"Deposit-5"));
				} else if (countCoal == 1) {
					error++;
					strategy.add(new BankingAction(firstCoal, "Coal",
							"Deposit-1"));
				}
				if (mithril != null && mithril.getWidgetChild() != null && mithril.getWidgetChild().getChildStackSize() > 4) {
					strategy.add(new BankingAction(mithril.getWidgetChild(), "Mithril ore",
							"Withdraw-5"));
				} else if (mithril != null) {
					System.out.println("Out of mithril ore");
					stop();
					return;
				}
				if (startedUrn != null) {
					strategy.add(new BankingAction(urn.getWidgetChild(), "Smelting urn",
							"Withdraw-1"));
				} else if (urn != null && countUrn == 0) {
					strategy.add(new BankingAction(urn.getWidgetChild(), "Smelting urn (r)",
							"Withdraw-1"));
				}
				if (coal != null && coal.getWidgetChild() != null && coal.getWidgetChild().getChildStackSize() > 19) {
					strategy.add(new BankingAction(coal.getWidgetChild(), "Coal", "Withdraw-All"));
				} else if (coal != null) {
					System.out.println("Out of coal");
					stop();
					return;
				}
				System.out.println("RS");

				strategy.run();
				System.out.println(strategy);
				barsProduced += countBar;

				xPos = (int) bankclose.getX() + Random.nextInt(-1, 1);
				yPos = (int) bankclose.getY() + Random.nextInt(-1, 1);
				System.out.println("C");
				Mouse.hop(xPos, yPos);
				while (!Menu.contains("Close") && isRunning()) {
					Mouse.hop(xPos, yPos);
					Time.sleep(5);
				}
				Mouse.click(true);

				if (countFullUrns > 0) {
					Item urnitem = Inventory.getItem(20288);
					if (urnitem != null) {
						if (new BankingAction(urnitem.getWidgetChild(),
								"Smelting urn (full)", "Teleport urn").run()) {
							if (countFullUrns > 1) {
								Timer urnTimer = new Timer(1500);
								while (urnTimer.isRunning() && isRunning()
										&& !Widgets.get(905, 14).isOnScreen()) {
									Time.sleep(10);
								}
								Widgets.get(905, 14).interact("Teleport all");
								teleUrn = false;
							} else {
								teleUrn = (countFullUrns == 1);
							}
						}
					}
				}
				mithrilOres = 5;
				// System.out.println(getItemAt(4));
				// Time.sleep(1000);
				long time = start.getElapsed();
				if (time > maxBankTime) {
					maxBankTime = maxBankTime == 0 ? 1 : time;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public boolean validate() {
			return mithrilOres == 0;
		}

	}

	Item coal = null;
	Item mithril = null;
	Point bank = null, bankclose = new Point(490, 87);
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
			Timer clickTimer = new Timer(0);
			Timer endTimer = new Timer(8000);
			int lastamount = -1;
			outerloop: while (endTimer.isRunning() && isRunning()
					&& mithrilOres > 0) {
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
					if ((option1.equals("Mithril bar") || option1
							.equals("Superheat Item -> Mithril bar"))
							&& !castTimer.isRunning()) {
						while (!Tabs.MAGIC.open() && isRunning())
							Time.sleep(10);
						break outerloop;
					} else if (option1.equals("Teleport to House")) {
						myPoint = new Point(588, 303);
					}

					if (Tabs.INVENTORY.isOpen()
							&& Inventory.getItemAt(4).getId() != 447) {
						break outerloop;
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
						amount = Inventory.getCount(new Filter<Item>() {
							public boolean accept(Item i) {
								return i.getWidgetChild().getIndex() < 5
										&& i.getId() == 447;
							}
						});
						if (amount == 0) {
							break;
						}
						// System.out.println("Amount: " + amount);
						lastamount = amount;
					}
					Mouse.hop((int) myPoint.getX(), (int) myPoint.getY());
					System.out.print("X");
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

	Point myPoint = new Point(578, 313);

	public void onRepaint(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 110, 155);
		g.setColor(Color.BLACK);
		g.drawString("Time: " + timer.toElapsedString(), 7, 18);
		g.drawString("Bars: " + barsProduced, 7, 38);
		g.drawString(
				"/H: "
						+ Math.round((float) barsProduced
								/ (timer.getElapsed() + 1) * 3600000), 7, 58);
		g.drawString(
				"Err/H: "
						+ Math.round((float) error / (timer.getElapsed() + 1)
								* 3600000), 7, 78);
		g.drawString("mx Bank: " + maxBankTime, 7, 98);
		g.drawString("mx Heat: " + maxSuperHeatTime, 7, 118);
		g.drawString("Urns: " + urns, 7, 138);
		g.drawString(
				"/H: "
						+ Math.round((float) urns / (timer.getElapsed() + 1)
								* 3600000), 7, 158);
		// g.drawString("avg Bank: " + totalBankTime/(mithrilOres*5+1), 7, 118);
		// g.drawString("avg Heat" + totalSuperHeatTime/(mithrilOres*5+1), 7,
		// 138);
	}

	@Override
	public void messageReceived(MessageEvent messageevent) {
		if (messageevent.getMessage().contains("four heaps of coal")
				|| messageevent.getMessage().contains(
						"cast superheat item on ore")) {
			mithrilOres = 0;
		}
		if (messageevent.getMessage().contains("urn is full"))
			teleUrn = true;
		if (messageevent.getMessage().contains("a new smelting urn"))
			urns++;
	}

}