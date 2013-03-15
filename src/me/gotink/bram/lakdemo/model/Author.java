package me.gotink.bram.lakdemo.model;

public class Author {

	private University university;
	private String name;
	
	public Author(String name) {
		this.name = name;
	}
	
	void setUni(University uni) {
		university = uni;
	}
	
	public void setUniversity(University university) {
		if (this.university != null)
			this.university.removeAuthor(this);
		if (university != null)
			university.addAuthor(this);
	}
	
	public University getUniversity() {
		return university;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
