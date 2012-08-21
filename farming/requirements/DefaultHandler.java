package scripts.farming.requirements;

import java.util.Map;

public class DefaultHandler {
	
	static Map<Integer, Integer> items;
	public static void setItemMap(Map<Integer,Integer> itemMap) {
		items = itemMap;
	}
	public static Map<Integer, Integer> getItemMap() {
		return items;
	}

	public static <Req extends Requirement<Req>, Req2 extends Requirement<Req2>> ReqHandler<Both<Req, Req2>> get(
			Both<Req, Req2> x) {
		return new ReqHandler<Both<Req, Req2>>() {

			@Override
			public void handle(Both<Req, Req2> req) {
				req.requirement.handle();
				req.requirement2.handle();
			}

		};
	}

	public static <Req extends Requirement<Req>, Req2 extends Requirement<Req2>> ReqHandler<OneOf<Req, Req2>> get(
			OneOf<Req, Req2> x) {
		return new ReqHandler<OneOf<Req, Req2>>() {

			@Override
			public void handle(OneOf<Req, Req2> req) {
				req.requirement.handle();
				req.requirement2.handle();
			}

		};
	}

	public static ReqHandler<SkillReq> get(SkillReq x) {
		return new ReqHandler<SkillReq>() {

			@Override
			public void handle(SkillReq req) {
			}

		};
	}

	public static ReqHandler<ItemReq> get(ItemReq x) {
		return new ReqHandler<ItemReq>() {

			@Override
			public void handle(ItemReq req) {
				Integer id = req.id.get();
				if(id == 0) return;
				if(items.containsKey(id)) {
					items.put(id, Math.max(req.amount,req.amount));
				} else {
					items.put(id, req.amount);
				}
			}

		};
	}

}
