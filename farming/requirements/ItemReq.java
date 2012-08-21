package scripts.farming.requirements;


import org.powerbot.game.api.methods.tab.Inventory;

import scripts.farming.Equipment;
import scripts.state.Constant;
import scripts.state.Value;

public class ItemReq implements Requirement<ItemReq> {

	Value<Integer> id;
	int amount;
	ReqHandler<ItemReq> handler;

	public ItemReq(int id_, int amount_) {
		id = new Constant<Integer>(id_);
		amount = amount_;
		handler = DefaultHandler.get(this);
	}

	public ItemReq(int id_, int amount_, ReqHandler<ItemReq> handler_) {
		id = new Constant<Integer>(id_);
		amount = amount_;
		handler = handler_;
	}

	public ItemReq(Value<Integer> id_, int amount_) {
		id = id_;
		amount = amount_;
		handler = DefaultHandler.get(this);
	}

	public ItemReq(Value<Integer> id_, int amount_, ReqHandler<ItemReq> handler_) {
		id = id_;
		amount = amount_;
		handler = handler_;
	}

	@Override
	public boolean validate() {
		// use the cached getCount
		final Integer id_ = id.get();
		/*
		 * boolean b = Inventory.getItems(false, new Filter<Item>() { public
		 * boolean accept(Item item) { return item.getId() == id_; } }).length
		 * >= Math.max(1, amount);
		 */
		boolean b = Inventory.getCount(id.get())
				+ (Equipment.WEAPON.getEquipped() == id_ ? 1 : 0) >= Math.max(
				1, amount);
		if (!b)
			System.out.println("ITEM:: " + id_ + "/" + amount);
		return b;
	}

	@Override
	public void handle() {
		handler.handle(this);
	}

}
