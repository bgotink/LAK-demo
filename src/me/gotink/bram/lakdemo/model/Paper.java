package me.gotink.bram.lakdemo.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Paper {

	private Set<Author> authors;
	private Set<Subject> subjects;
	
	private String title;
	
	public Paper(String title) {
		this.title = title;
		authors = new HashSet<Author>();
		subjects = new HashSet<Subject>();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Set<Author> getAuthors() {
		return Collections.unmodifiableSet(authors);
	}
	
	public void setAuthors(Set<Author> authors) {
		this.authors.clear();
		this.authors.addAll(authors);
	}
	
	public void addAuthor(Author author) {
		authors.add(author);
	}
	
	public Set<Subject> getSubjects() {
		return Collections.unmodifiableSet(subjects);
	}
	
	public void setSubjects(Set<Subject> subj) {
		for (Subject s : subjects) {
			s.removePaper(this);
		}
		subjects.clear();
		subjects.addAll(subj);
		for(Subject s : subjects) {
			s.addPaper(this);
		}
	}
	
	public void addSubject(Subject s) {
		subjects.add(s);
		s.addPaper(this);
	}
}
