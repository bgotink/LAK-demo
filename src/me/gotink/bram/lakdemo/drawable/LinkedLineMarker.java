package me.gotink.bram.lakdemo.drawable;

import javax.vecmath.Point2f;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class LinkedLineMarker extends SimpleLinesMarker {

	private SelectableWithLocation from, to;

	public LinkedLineMarker(SelectableWithLocation from, SelectableWithLocation to) {
		super(from.getLocation(), to.getLocation());

		this.from = from;
		this.to = to;
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected == this.selected) return;
		
		if (selected) {
			System.out.println("LinkedLineMarker#setSelected(" + selected + ")");
			(new Error()).printStackTrace(System.out);
		}
		super.setSelected(selected);
		if (selected) {
			to.addSelectedLine();
			from.addSelectedLine();
		} else {
			to.removeSelectedLine();
			from.removeSelectedLine();
		}
	}
	
	private static Point2f getClosestPoint(Point2f v, Point2f w, Point2f p) {
		float l2 = v.distanceSquared(w);
		if (l2 == 0)
			return v;

		float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
		if (t < 0) return v;
		if (t > 1) return w;
		return new Point2f(
				v.x + t * (w.x - v.x),
				v.y + t * (w.y - v.y)
				);
	}

	public static float getDistanceFromLineSegment(ScreenPosition l, ScreenPosition from, ScreenPosition to) {
		Point2f p = new Point2f(l.x, l.y);
		Point2f v = new Point2f(from.x, from.y);
		Point2f w = new Point2f(to.x, from.y);

		return p.distance(getClosestPoint(v, w, p));
	}
	
	@Override
	public boolean isInside(UnfoldingMap map, float checkX, float checkY) {
		ScreenPosition f = map.getScreenPosition(from.getLocation());
		ScreenPosition t = map.getScreenPosition(to.getLocation());
		if (getDistanceFromLineSegment(new ScreenPosition(checkX, checkY), f, t) < 40) {
			System.out.println(getDistanceFromLineSegment(new ScreenPosition(checkX, checkY), f, t));
			return true;
		}
		return false;
	}
	
	@Override public double getDistanceTo(Location l) {
		Point2f v = new Point2f(from.getLocation().x, from.getLocation().y);
		Point2f w = new Point2f(to.getLocation().x, to.getLocation().y);
		Point2f p = new Point2f(l.x, l.y);
		
		Point2f closest = getClosestPoint(v, w, p);
		return GeoUtils.getDistance(l, new Location(closest.x, closest.y));
	}

}
