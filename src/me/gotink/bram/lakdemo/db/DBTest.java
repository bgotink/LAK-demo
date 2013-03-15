package me.gotink.bram.lakdemo.db;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

public class DBTest {
	
	private final DBConnection conn;
	
	public DBTest(DBConnection conn) {
		this.conn = conn;
	}
	
	public void test() {
		try {
			RepositoryConnection conn = this.conn.getConnection();
			try {
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, 
						"PREFIX led:<http://data.linkededucation.org/ns/linked-education.rdf#>\n"
				+		"PREFIX swrc:<http://swrc.ontoware.org/ontology#>\n"
				+ 		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
				+		"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"
				+		"SELECT ?paper ?title \n"
				+		"WHERE { ?paper dc:title ?title . ?paper swrc:year ?year . FILTER (?year = \"2011\") }");
				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) {
						BindingSet cur = result.next();
						Value paper = cur.getValue("title");
						System.out.println(paper.stringValue());
					}
				} finally {
					result.close();
				}
			} finally {
				conn.close();
			}
		} catch (OpenRDFException e) {
			e.printStackTrace();
		}
	}

}
