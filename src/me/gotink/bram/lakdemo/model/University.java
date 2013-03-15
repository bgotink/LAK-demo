package me.gotink.bram.lakdemo.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.fhpotsdam.unfolding.geo.Location;

public class University {

	private String name;
	private Location location;
	
	private Set<Author> authors;
	
	public University(String name, double lon, double lat) {
		this.name = name;
		location = new Location(lon, lat);
		authors = new HashSet<Author>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Set<Author> getAuthors() {
		return Collections.unmodifiableSet(authors);
	}
	
	public void addAuthor(Author a) {
		authors.add(a);
		a.setUni(this);
	}
	
	public void removeAuthor(Author a) {
		authors.remove(a);
		a.setUni(null);
	}
}
