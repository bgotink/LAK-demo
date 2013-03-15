package me.gotink.bram.lakdemo.db;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class DBConnection {

	private final Repository repo;
	
	public DBConnection(String url) throws RepositoryException {
		repo = new HTTPRepository(url);
		repo.initialize();
	}
	
	public void close() {
		try {
			repo.shutDown();
		} catch (RepositoryException e) {
			System.err.println("Exception when shutting down sesame repository:");
			e.printStackTrace(System.err);
		}
	}
	
	RepositoryConnection getConnection() throws RepositoryException {
		return repo.getConnection();
	}
	
}
