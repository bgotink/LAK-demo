package me.gotink.bram.lakdemo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.gotink.bram.lakdemo.db.DBConnection;
import me.gotink.bram.lakdemo.db.DBLoader;
import me.gotink.bram.lakdemo.drawable.LinkedLineMarker;
import me.gotink.bram.lakdemo.drawable.NamedMarker;
import me.gotink.bram.lakdemo.model.Author;
import me.gotink.bram.lakdemo.model.Paper;
import me.gotink.bram.lakdemo.model.Subject;
import me.gotink.bram.lakdemo.model.University;

import org.openrdf.repository.RepositoryException;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.looksgood.ani.Ani;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;

	private Set<Author> authors;
	private Set<Subject> subjects;
	private Set<University> universities;
	private Set<Paper> papers;
	private Map<University, NamedMarker> uniMarkers;

	private UnfoldingMap map;

	private DBLoader dbLoader;

	public Main() throws RepositoryException {
		DBConnection dbConn = new DBConnection(
				"http://data.linkededucation.org/openrdf-sesame/repositories/lak-conference");
		dbLoader = new DBLoader(dbConn);
	}

	@Override
	public void setup() {
		// init animation framework
		Ani.init(this);
		
		size(1440, 900);//, GLConstants.GLGRAPHICS);
		smooth();

		dbLoader.load();

		authors = dbLoader.getAuthors();
		subjects = dbLoader.getSubjects();
		universities = dbLoader.getUniversities();
		papers = dbLoader.getPapers();
		uniMarkers = new HashMap<University, NamedMarker>();

		loadUniversityLocations();

		// create the map
		map = new UnfoldingMap(this);
		// restrict zoom level
		map.setZoomRange(1, 15);
		map.zoomToLevel(1);
		// enable zoom animations
		map.setTweening(true);
		MapUtils.createDefaultEventDispatcher(this, map);

		for (University uni : universities) {
			NamedMarker m = new NamedMarker(uni.getName(), uni.getLocation(), true);
			uniMarkers.put(uni, m);
		}

		drawLines();
		
		map.addMarkers(uniMarkers.values().toArray(new Marker[0]));
	}

	@Override
	public void draw() {
		map.draw();
	}

	@Override
	public void mouseMoved() {
		Collection<Marker> hitMarkers = map.getHitMarker(mouseX, mouseY);
		for (Marker m : map.getMarkers()) {
			m.setSelected(hitMarkers.contains(m));
		}
	}

	private void loadUniversityLocations() {
		String[] lines = loadStrings("geolocations.csv");

		Map<String, Location> locations = new HashMap<String, Location>();

		for (int i = 1; i < lines.length; i++) {
			String[] parts = lines[i].split(";");

			locations.put(parts[2], new Location(Double.valueOf(parts[0]), Double.valueOf(parts[1])));
		}

		for (University uni : universities) {
			String name = uni.getName();
			if (!locations.containsKey(name))
				continue;
			uni.setLocation(locations.get(name));
		}
	}

	private void drawLines() {
		for (Paper p : papers) {
			Set<University> unis = new HashSet<University>();
			for (Author a : p.getAuthors()) {
				unis.add(a.getUniversity());
			}

			if (unis.size() < 2)
				continue;

			for (University uni : unis) {
				for (University uni2 : unis) {
					if (uni == uni2)
						continue;
					if (uni == null || uni2 == null)
						continue;
					Marker m;
					map.addMarkers(m = new LinkedLineMarker(uniMarkers.get(uni), uniMarkers.get(uni2)));
					m.setStrokeWeight(1);
				}
			}
		}
	}
}
