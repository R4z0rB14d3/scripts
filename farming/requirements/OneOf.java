package scripts.farming.requirements;


public class OneOf<Req extends Requirement<Req>,
					Req2 extends Requirement<Req2>> implements Requirement<OneOf<Req,Req2>> {
	Req requirement;
	Req2 requirement2;
	ReqHandler<OneOf<Req,Req2>> handler;
	
	public OneOf(Req requirement_, Req2 requirement2_) {
		requirement = requirement_;
		requirement2 = requirement2_;
		handler = DefaultHandler.get(this);	}
	
	public OneOf(Req requirement_, Req2 requirement2_, ReqHandler<OneOf<Req,Req2>> handler_) {
		requirement = requirement_;
		requirement2 = requirement2_;
		handler = handler_;
	}
	
	
	public boolean validate() {
		if(requirement.validate()) return true;
		if(requirement2.validate()) return true;
		return false;
	}

	@Override
	public void handle() {
		handler.handle(this);
	}


	
}
