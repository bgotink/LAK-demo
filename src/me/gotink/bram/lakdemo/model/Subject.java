package me.gotink.bram.lakdemo.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Subject {

	private String subject;
	
	private Set<Paper> papers;
	
	public Subject(String s) {
		subject = s;
		papers = new HashSet<Paper>();
	}
	
	public Set<Paper> getPapers() {
		return Collections.unmodifiableSet(papers);
	}
	
	void addPaper(Paper p) {
		papers.add(p);
	}
	
	void removePaper(Paper p) {
		papers.remove(p);
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override public int hashCode() {
		return subject.hashCode();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof Subject)) return false;
		return subject.equals(((Subject) o).subject);
	}
}
