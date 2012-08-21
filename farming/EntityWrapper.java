package scripts.farming;

import java.awt.Point;

import org.powerbot.game.api.wrappers.ViewportEntity;

public class EntityWrapper implements ViewportEntity {

	ViewportEntity entity;

	public EntityWrapper(ViewportEntity entity_) {
		entity = entity_;
	}

	@Override
	public boolean validate() {
		return entity.validate();
	}

	@Override
	public Point getCentralPoint() {
		return entity.getCentralPoint();
	}

	@Override
	public Point getNextViewportPoint() {
		Point gnvp = entity.getNextViewportPoint();
		Point gcp = entity.getCentralPoint();
		// TODO Auto-generated method stub

		return new Point((int)Math.round((gnvp.getX() + gcp.getX()) / 2),
				(int)Math.round((gnvp.getY() + gcp.getY()) / 2));
	}

	@Override
	public boolean contains(Point point) {
		return entity.contains(point);
	}

}
