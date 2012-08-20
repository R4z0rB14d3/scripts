package scripts.state.edge;

import org.powerbot.game.api.wrappers.widget.WidgetChild;

import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class InteractWidget extends Task {
	Value<WidgetChild> widget;
	String interaction;

	public InteractWidget(Condition c, State s, final Value<WidgetChild> widget_,
			String interaction_) {
		super(c.and(new Condition() {
			public boolean validate() {
				return widget_.get() != null && widget_.get().isOnScreen();
			}
		}), s);
		widget = widget_;
		interaction = interaction_;
	}

	public InteractWidget(Condition c, State s, final WidgetChild widget_, String interaction_) {
		this(c, s, new Constant<WidgetChild>(widget_), interaction_);
	}

	@Override
	public void run() {
		widget.get().interact(interaction);
	}
}
