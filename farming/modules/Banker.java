package scripts.farming.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;

import scripts.farming.Equipment;
import scripts.farming.FarmingProject;
import scripts.farming.Location;
import scripts.farming.Patch;
import scripts.farming.Patches;
import scripts.farming.requirements.DefaultHandler;
import scripts.farming.requirements.ItemReq;
import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.Module;
import scripts.state.SharedModule;
import scripts.state.State;
import scripts.state.StateCreator;
import scripts.state.Value;
import scripts.state.edge.Edge;
import scripts.state.edge.Option;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.tools.OptionSelector;

public class Banker extends SharedModule<Banker.Method> {

	// id -> amount
	public Map<Integer, Integer> getRequirements() {
		Map<Integer, Integer> items = new HashMap<Integer, Integer>();
		DefaultHandler.setItemMap(items);
		for (Location loc : Location.locations) {
			if (loc.activated) {
				DoPatches.getSeedRequirements(loc).handle();
				loc.getModule().getRequirement().handle();
				loc.selectedTeleportOption.getRequirement().handle();
			}
		}
		RunOtherScriptv2.requirements.handle();
		return items;
	}

	public enum Method {
		IDLE, DEPOSIT, WITHDRAW
	};

	public Banker(final FarmingProject main, State initial, State success,
			final State critical) {
		super("Banker", initial, success, critical);

		State AT_BANK = new State("BANK");
		Option<Module> chooseTeleport = new Option<Module>(Condition.TRUE,
				new OptionSelector<Module>() {
					public Module select() {
						return (Module) Location.getLocation("Bank").selectedTeleportOption;
					}
				});
		initial.add(new Edge(new Condition() {
			public boolean validate() {
				return Bank.open();
			}
		}, AT_BANK));
		initial.add(chooseTeleport);
		initial.add(new Timeout(critical, 15000));

		for (Module module : Location.getLocation("Bank").getTeleportOptions()) {
			chooseTeleport.add(module, module.getInitialState());
			module.getSuccessState().add(new Edge(Condition.TRUE, AT_BANK));
			module.getCriticalState().add(new Edge(Condition.TRUE, critical));
		}
		Location.getLocation("Bank").setModule(this);

		State BANKING_FINISHED = new State("BANKF");
		State BANKING_FINISHED_WITHDRAW = new State("BANKFWD");
		State BANK_OPEN = new State("BANKO");
		State DEPOSIT = new State("BANKD");

		State WITHDRAW = new ConsecutiveState<Entry<Integer, Integer>>(
				new Value<Set<Entry<Integer, Integer>>>() {
					public Set<Entry<Integer, Integer>> get() {
						return getRequirements().entrySet();
					}
				}, BANKING_FINISHED_WITHDRAW,
				new StateCreator<Entry<Integer, Integer>>() {
					public State getState(Entry<Integer, Integer> value,
							State nextState) {
						State state = new State(value.getKey().toString());
						final Integer id = value.getKey();
						final Integer amount = value.getValue();
						if (id > 0) {
							state.add(new Edge(new Condition() {
								public boolean validate() {
									return Bank.getItem(id) == null;
								}
							}, nextState));
							state.add(new Task(Condition.TRUE, state) {
								public void run() {
									Bank.withdraw(id, amount);
									Time.sleep(300);
								}
							});							state.add(new Edge(new Condition() {
								public boolean validate() {
									return new ItemReq(id, amount).validate();
								}
							}, nextState));
						} else {
							state.add(new Edge(Condition.TRUE, nextState));
						}
						return state;
					}
				});

		AT_BANK.add(new Edge(new Condition() {
			public boolean validate() {
				return Bank.open();
			}
		}, BANK_OPEN));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.intermediateValue == Method.DEPOSIT;
			}
		}, DEPOSIT));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.intermediateValue == Method.WITHDRAW;
			}
		}, WITHDRAW));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.intermediateValue == Method.IDLE;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Edge(new Condition() {
			public boolean validate() {
				return Inventory.getCount() == 0;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Task(Condition.TRUE, DEPOSIT) {
			public void run() {
				if (Equipment.WEAPON.getEquipped() > 0)
					Bank.depositEquipment();
				if (Inventory.getCount() > 0)
					Bank.depositInventory();
			}
		});

		BANKING_FINISHED.add(new Task(Condition.TRUE, success) {
			public void run() {
				// Bank.close();
			}
		});

		BANKING_FINISHED_WITHDRAW.add(new Task(Condition.TRUE, success) {
			public void run() {
				boolean change = false;
				for (Patch patch : Patches.patches.values()) {
					if (!patch.getRequirement().validate()) {
						System.out.println(patch + " deactivated");
						patch.activated = false;
						change = true;
					}
				}
				if (change)
					FarmingProject.gui.saveSettings();
				Bank.close();
			}
		});

	}
}
