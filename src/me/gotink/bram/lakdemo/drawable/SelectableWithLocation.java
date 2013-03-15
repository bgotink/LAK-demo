package me.gotink.bram.lakdemo.drawable;

import de.fhpotsdam.unfolding.geo.Location;

public interface SelectableWithLocation {

	public Location getLocation();
	
	public void addSelectedLine();
	public void removeSelectedLine();
}
