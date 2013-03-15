package me.gotink.bram.lakdemo.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.gotink.bram.lakdemo.model.Author;
import me.gotink.bram.lakdemo.model.Paper;
import me.gotink.bram.lakdemo.model.Subject;
import me.gotink.bram.lakdemo.model.University;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class DBLoader {

	private final DBConnection conn;

	private Map<String, Author> authors;
	private Map<String, Paper> papers;
	private Map<String, Subject> subjects;
	private Map<String, University> universities;

	private Map<String, String> duplicateNames;

	public DBLoader(DBConnection conn) {
		this.conn = conn;

		duplicateNames = new HashMap<String, String>();

		// authors
		duplicateNames.put("Ryan S.J.d. Baker", "Ryan S.j.d. Baker");

		// universities
		duplicateNames.put("Carnegie Learning Inc", "Carnegie Learning Inc.");

		// subjects
		duplicateNames.put("intelligent tutoring system", "intelligent tutoring systems");
		duplicateNames.put("bayesian network", "bayesian networks");
	}

	private String getString(String s) {
		return duplicateNames.containsKey(s) ? duplicateNames.get(s) : s;
	}

	private String getString(Value v) {
		return getString(v.stringValue().trim());
	}

	public void load() {
		authors = new HashMap<String, Author>();
		papers = new HashMap<String, Paper>();
		subjects = new HashMap<String, Subject>();
		universities = new HashMap<String, University>();

		try {
			RepositoryConnection conn = this.conn.getConnection();
			try {
				loadUniversities(conn);
				loadPapers(conn);
				linkPapers(conn);
			} finally {
				conn.close();
			}
		} catch (OpenRDFException e) {
			System.err.println("Exception when loading model:");
			e.printStackTrace(System.err);
		}
	}

	private void linkPapers(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"
						+ "PREFIX swrc:<http://swrc.ontoware.org/ontology#>\n"
						+ "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
						+ "SELECT DISTINCT ?title ?author\n"
						+ "WHERE { ?paper dc:creator ?auth . ?auth rdfs:label ?author . ?paper dc:title ?title }");
		TupleQueryResult result = query.evaluate();
		try {
			while (result.hasNext()) {
				BindingSet cur = result.next();
				String author = getString(cur.getValue("author"));
				String paper = getString(cur.getValue("title"));
				
				if (paper.length() == 0 || author.length() == 0) continue;
				
				if (!papers.containsKey(paper)) {
					System.out.println("Adding paper " + paper);
					papers.put(paper, new Paper(paper));
				}
				
				if (!authors.containsKey(author)) {
					System.out.format("Adding author %s with no affiliation\n", author);
					authors.put(author, new Author(author));
				}
				
				System.out.format("Linking paper %s to author %s\n", paper, author);
				papers.get(paper).addAuthor(authors.get(author));
			}
		} finally {
			result.close();
		}
	}

	private void loadPapers(RepositoryConnection conn) throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"
						+ "PREFIX swrc:<http://swrc.ontoware.org/ontology#>\n"
						+ "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n" + "SELECT DISTINCT ?title ?subject\n"
						+ "WHERE { ?paper dc:title ?title . ?paper dc:subject ?subject }");
		TupleQueryResult result = query.evaluate();
		try {
			while (result.hasNext()) {
				BindingSet cur = result.next();
				String title = getString(cur.getValue("title"));
				String subject = getString(cur.getValue("subject"));

				if (subject.length() == 0 || title.length() == 0)
					continue;

				if (!subjects.containsKey(subject)) {
					System.out.println("Adding subject " + subject);
					subjects.put(subject, new Subject(subject));
				}

				if (!papers.containsKey(title)) {
					System.out.println("Adding paper " + title);
					papers.put(title, new Paper(title));
				}

				System.out.format("Linking paper %s to subject %s\n", title, subject);
				papers.get(title).addSubject(subjects.get(subject));
			}
		} finally {
			result.close();
		}
	}

	private void loadUniversities(RepositoryConnection conn) throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		TupleQuery query = conn
				.prepareTupleQuery(
						QueryLanguage.SPARQL,
						"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"
								+ "PREFIX swrc:<http://swrc.ontoware.org/ontology#>\n"
								+ "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
								+ "SELECT DISTINCT ?author ?university\n"
								+ "WHERE { ?paper dc:creator ?auth . ?auth swrc:affiliation ?aff . ?auth rdfs:label ?author . ?aff rdfs:label ?university }");
		TupleQueryResult result = query.evaluate();
		try {
			while (result.hasNext()) {
				BindingSet cur = result.next();
				String university = getString(cur.getValue("university"));
				String author = getString(cur.getValue("author"));

				if (university.length() == 0)
					continue;
				if (author.length() == 0)
					continue;

				if (!universities.containsKey(university)) {
					System.out.println("Adding university " + university);
					universities.put(university, new University(university, 0, 0));
				}

				if (!authors.containsKey(author)) {
					System.out.format("Adding author %s with affiliation %s\n", author, university);
					Author a = new Author(author);
					a.setUniversity(universities.get(university));
					authors.put(author, a);
				}
			}
		} finally {
			result.close();
		}
	}
	
	public Set<Author> getAuthors() {
		return new HashSet<Author>(authors.values());
	}
	
	public Set<Paper> getPapers() {
		return new HashSet<Paper>(papers.values());
	}
	
	public Set<University> getUniversities() {
		return new HashSet<University>(universities.values());
	}
	
	public Set<Subject> getSubjects() {
		return new HashSet<Subject>(subjects.values());
	}
}
