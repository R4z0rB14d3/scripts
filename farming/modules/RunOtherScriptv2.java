package scripts.farming.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;

import scripts.farming.FarmingProject;
import scripts.farming.Magic;
import scripts.farming.Patch;
import scripts.farming.Patches;
import scripts.farming.ScriptWrapper;
import scripts.farming.requirements.EmptyReq;
import scripts.farming.requirements.Requirement;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.QueuedState;
import scripts.state.State;
import scripts.state.StateStrategy;
import scripts.state.edge.Animation;
import scripts.state.edge.Edge;
import scripts.state.edge.ExceptionSafeTask;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Option;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.tools.OptionSelector;

public class RunOtherScriptv2 extends Module {
	List<Strategy> newStrategies;
	public Class<?> runningScript = null;
	public ActiveScript activeScript;
	public static Requirement<?> requirements = new EmptyReq();

	public static ActiveScript initiateScript(FarmingProject main,
			Class<?> script) throws Exception {
		try {
			Method getReqs = script.getDeclaredMethod("getRequirements");
			requirements = (Requirement<?>) getReqs.invoke(null);
		} catch (NoSuchMethodException nsme) {
			// ignore
		}
		ActiveScript activeScript = (ActiveScript) script.getDeclaredMethod(
				"getInstance").invoke(null);
		Field contextField = ActiveScript.class.getDeclaredField("context");
		contextField.setAccessible(true);
		Field botField = Context.class.getDeclaredField("bot");
		botField.setAccessible(true);
		Bot bot = (Bot) botField.get(contextField.get(main));
		// Context newContext = new Context(bot);
		activeScript.init((Context) contextField.get(main));

		Method setupMethod = activeScript.getClass().getDeclaredMethod("setup");
		setupMethod.setAccessible(true);
		setupMethod.invoke(activeScript);
		return activeScript;
	}

	public RunOtherScriptv2(final FarmingProject main, State initial,
			State success, final State critical,
			OptionSelector<Class<?>> selector, final Condition run,
			final Condition interrupt) {
		super("Run other script", initial, success, critical);

		Set<Class<?>> scripts = main.loader.getScripts();

		Option<Class<?>> option = new Option<Class<?>>(run, selector);
		initial.add(option);
		for (final Class<?> script : scripts) {
			final State bankFirst = new State("SCRBF");
			final State prepared = new State("SCRPRP");
			final State cleaningUp = new State("SCRPCU");
			final State state = new QueuedState("SCRIPT");
			ScriptWrapper annotation = script
					.getAnnotation(ScriptWrapper.class);
			if (annotation.banking()) {
				/**
				 * Bank before and after the script options -> bankFirst ->
				 * deposit -> prepared cleaningUp -> withdraw -> success (back
				 * to FarmingProject)
				 */
				option.add(script, bankFirst);
				main.banker.addSharedStates(bankFirst, prepared,
						Banker.Method.DEPOSIT, Banker.Method.IDLE);
				main.banker.addSharedStates(cleaningUp, success,
						Banker.Method.WITHDRAW, Banker.Method.IDLE);
			} else {
				/**
				 * No banking options -> prepared cleaningUp -> success (back to
				 * FarmingProject)
				 */
				option.add(script, prepared);
				cleaningUp.add(new Edge(Condition.TRUE, success));

			}

			final State interrupted = new State();

			prepared.add(new ExceptionSafeTask(Condition.TRUE, state, critical) {
				public void run() throws Exception {
					state.removeAllEdges();

					script.getDeclaredMethod("prepare").invoke(null);
					runningScript = script;

					if (!main.gui.miscSettings.setupScript) {
						activeScript = initiateScript(main, script);
					} else {
						activeScript = main.gui.activeScript;
						if (activeScript == null)
							throw new Exception("Script Not Found");
					}

					Field executorField = ActiveScript.class
							.getDeclaredField("executor");
					executorField.setAccessible(true);
					StrategyDaemon sd = (StrategyDaemon) executorField
							.get(activeScript);
					Field strategiesField = StrategyDaemon.class
							.getDeclaredField("strategies");
					strategiesField.setAccessible(true);
					List<Strategy> strategies = (List<Strategy>) strategiesField
							.get(sd);

					// newStrategies = new ArrayList<Strategy>();

					/**
					 * Cleaning up state @ interrupt -> cleanup
					 */

					final Object strategyLock = new Object();

					state.add(new Edge(new Condition() {
						public boolean validate() {
							Timer timer = new Timer(0);
							boolean valid = interrupt.validate();
							System.out.println("Interrupt time: "
									+ timer.getElapsed());
							return valid;

						}
					}, interrupted));
					interrupted.add(new ExceptionSafeTask(Condition.TRUE,
							cleaningUp, critical) {
						public void run() throws Exception {
							for (Strategy s : newStrategies) {
								main.customRevoke(s);
							}
							script.getDeclaredMethod("cleanup").invoke(null);

							activeScript = null;
						}
					});

					/**
					 * Curing Patches while running script state @ patch
					 * diseased & use remote farm -> curing on error -> disable
					 * remote farm
					 */

					final State checkInterruptions = new State();
					final StateStrategy checkInterruptionsStrategy = new StateStrategy(
							checkInterruptions);

					State curingDiseased = new State("CD");
					State disablingRemoteFarm = new State("DRF"); // when
																	// something
																	// went
																	// wrong
					State interruptMovement = new State("IM");
					State closeRemoteFarm = new State("CRF");

					checkInterruptions.add(new Edge(new Condition() {
						public boolean validate() {
							Timer timer = new Timer(0);
							boolean valid = false;
							for (Patch patch : Patches.patches.values()) {
								if (patch.isDiseased())
									valid = true;
							}
							System.out.println("CheckDisease time: "
									+ timer.getElapsed());
							return FarmingProject.gui.miscSettings.useRemoteFarm
									&& valid;

						}
					}, interruptMovement));

					checkInterruptions.add(new Edge(new Condition() {
						public boolean validate() {
							return Widgets.get(1082, 154).validate();
						}
					}, closeRemoteFarm));

					closeRemoteFarm.add(new Task(Condition.TRUE,
							checkInterruptions) {
						public void run() {
							Widgets.get(1082, 154).click(true);
						}
					});

					interruptMovement.add(new MagicCast(new Condition() {
						public boolean validate() {
							return !Players.getLocal().isMoving();
						}
					}, new State("WAITINTFC").add(new Edge(new Condition() {
						public boolean validate() {
							return Widgets.get(1082, 4).isOnScreen();
						}
					}, curingDiseased)).add(
							new Timeout(disablingRemoteFarm, 5000)),
							disablingRemoteFarm, Magic.Lunar.RemoteFarm));

					curingDiseased.add(new ExceptionSafeTask(Condition.TRUE,
							checkInterruptions, checkInterruptions) {
						public void run() {
							System.out.println("CD");
							Magic.cureAllDiseased();
							System.out.println("/CD");
						}
					});

					disablingRemoteFarm.add(new Task(Condition.TRUE,
							checkInterruptions) {
						public void run() {
							main.gui.miscSettings.useRemoteFarm = false;
						}
					});

					final Field tasksField = Strategy.class
							.getDeclaredField("tasks");
					tasksField.setAccessible(true);

					/*
					 * State strategyState = new
					 * ConsecutiveState<Strategy>(strategies,state,new
					 * StateCreator<Strategy>() { public State getState(final
					 * Strategy strategy, State nextState) {
					 * 
					 * try { final org.powerbot.concurrent.Task[] tasks =
					 * (org.powerbot.concurrent.Task[]) tasksField
					 * .get(strategy); State validate = new State(); State run =
					 * new State(); validate.add(new Either(new Condition() {
					 * public boolean validate() { return strategy.validate(); }
					 * }, run, nextState));
					 * 
					 * 
					 * 
					 * run.add(new ExceptionSafeTask(Condition.TRUE, nextState,
					 * state) { public void run() {
					 * 
					 * for (org.powerbot.concurrent.Task task : tasks) {
					 * main.submit(task); System.out.print("<T"); //task.run();
					 * System.out.print("T>"); } } }); return validate; } catch
					 * (Exception e) { e.printStackTrace(); return nextState; }
					 * } });
					 * 
					 * state.add(new Edge(Condition.TRUE,strategyState));
					 */
					newStrategies = new ArrayList<Strategy>();

					for (final Strategy strategy : strategies) {
						//
						// System.out.println(strategy.getClass().getName());
						// Field policyField = Strategy.class
						// .getDeclaredField("policy");
						// policyField.setAccessible(true); final
						// org.powerbot.concurrent.strategy.Condition condition
						// = (org.powerbot.concurrent.strategy.Condition)
						// policyField .get(strategy);
						//

						/*
						 * State proceed = new State();
						 * 
						 * Field tasksField = Strategy.class
						 * .getDeclaredField("tasks");
						 * tasksField.setAccessible(true); final
						 * org.powerbot.concurrent.Task[] tasks =
						 * (org.powerbot.concurrent.Task[]) tasksField
						 * .get(strategy); State strategyState = new
						 * State("SS"); state.add(new Edge(new Condition() {
						 * public boolean validate() { return
						 * strategy.validate(); } }, strategyState));
						 * strategyState.add(new
						 * ExceptionSafeTask(Condition.TRUE, proceed,
						 * interrupted) { public void run() {
						 * 
						 * for (org.powerbot.concurrent.Task task : tasks) {
						 * main.submit(task); System.out.print("<T");
						 * //task.run(); System.out.print("T>"); } } });
						 * 
						 * proceed.add(new Edge(new Condition() { public boolean
						 * validate() { for (org.powerbot.concurrent.Task task :
						 * tasks) { if(!main.terminated(task)) return false; }
						 * return true; } },state));
						 */

						Strategy newStrategy;
						final org.powerbot.concurrent.Task[] tasks = (org.powerbot.concurrent.Task[]) tasksField
								.get(strategy);
						List<org.powerbot.concurrent.Task> customTaskList = new ArrayList<org.powerbot.concurrent.Task>();
						final org.powerbot.concurrent.Task[] customTasks = new org.powerbot.concurrent.Task[tasks.length];
						for (final org.powerbot.concurrent.Task task : tasks) {
							customTaskList
									.add(new org.powerbot.concurrent.Task() {
										public void run() {
											// To be safe, we close remote farm
											if (Widgets.get(1082, 154)
													.validate()) {
												Widgets.get(1082, 154).click(
														true);
											}
											System.out.print("<RUN");
											task.run();
											System.out.println(">");
										}

									});
						}
						Condition cond = (Condition) new Condition() {
							public boolean validate() {
								return checkInterruptionsStrategy
										.getCurrentState() == checkInterruptions;
							}
						}.and(interrupt.negate()).and(strategy);

						Condition cond2 = new Condition() {
							public boolean validate() {
								boolean checkIS, interruptNeg, strateg;
								// System.out.print("(" + (ru =
								// run.validate()));
								System.out.print(","
										+ (checkIS = (checkInterruptionsStrategy
												.getCurrentState() == checkInterruptions)));
								System.out.print(","
										+ (interruptNeg = !interrupt.validate()));
								System.out.print(","
										+ (strateg = strategy.validate()));
								return checkIS && interruptNeg && strateg;
							}
						};

						main.customProvide(newStrategy = new Strategy(cond2,
								customTaskList.toArray(customTasks)));
						newStrategies.add(newStrategy);
					}
					main.customProvide(checkInterruptionsStrategy);
					newStrategies.add(checkInterruptionsStrategy);

				}
			});

		}
	}
}
