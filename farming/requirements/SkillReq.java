package scripts.farming.requirements;

import org.powerbot.game.api.methods.tab.Skills;

public class SkillReq implements Requirement<SkillReq> {

	int skill;
	int level;
	ReqHandler<SkillReq> handler;

	public SkillReq(int skill_, int level_) {
		skill = skill_;
		level = level_;
		handler = DefaultHandler.get(this);	
	}

	public SkillReq(int skill_, int level_, ReqHandler<SkillReq> handler_) {
		skill = skill_;
		level = level_;
		handler = handler_;
	}

	@Override
	public boolean validate() {
		return Skills.getLevel(skill)>=level;
	}

	@Override
	public void handle() {
		handler.handle(this);
	}


}
