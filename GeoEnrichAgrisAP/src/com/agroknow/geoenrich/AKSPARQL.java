package com.agroknow.geoenrich;


import java.util.Iterator;

import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;


public class AKSPARQL 
{
	public String queryFAOGeo(String value)
	{
		
		String queryString = ""
				+ "PREFIX skos:	<http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX bibo: <http://purl.org/ontology/bibo/>"
				+ "	SELECT ?uri ?geouri { "
					+ "?uri skos:prefLabel \""+value+"\"@en ."
					+ "?uri skos:exactMatch ?geouri."
					+ "FILTER regex(str(?geouri), \"geopolitical\")"
				+ "}";
		// Open connection to a new temporary repository
        // (ruleset is irrelevant for this example)

		/*RepositoryManager repositoryManager =
		        new RemoteRepositoryManager( "http://83.212.115.164:7200/repositories/agris" );
		repositoryManager.initialize();*/

        /* Alternative: connect to a remote repository

        // Abstract representation of a remote repository accessible over HTTP
        HTTPRepository repository = new HTTPRepository("http://localhost:8080/graphdb/repositories/myrepo");

        // Separate connection to a repository
        RepositoryConnection connection = repository.getConnection();

        */
		
		RepositoryConnection connection = null;

        try {
        	// Abstract representation of a remote repository accessible over HTTP
            HTTPRepository repository = new HTTPRepository("http://83.212.115.164:7200/repositories/agrovoc");

            repository.setPreferredTupleQueryResultFormat(TupleQueryResultFormat.SPARQL);
            
            // Separate connection to a repository
            connection = repository.getConnection();
        	
            // Preparing a SELECT query for later evaluation
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL,
            		"PREFIX skos:	<http://www.w3.org/2004/02/skos/core#>"
            				+ "PREFIX bibo: <http://purl.org/ontology/bibo/>"
            				+ "	SELECT ?uri ?geouri { "
            					+ "?uri skos:prefLabel \""+value+"\"@en ."
            					+ "?uri skos:exactMatch ?geouri."
            					+ "FILTER regex(str(?geouri), \"geopolitical\")"
            				+ "}");

            // Evaluating a prepared query returns an iterator-like object
            // that can be traversed with the methods hasNext() and next()
            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
            while (tupleQueryResult.hasNext()) {
            	
            	/*tupleQueryResult.toString();
            	
            	if(true)
            		continue;*/
            	
                // Each result is represented by a BindingSet, which corresponds to a result row
                BindingSet bindingSet = tupleQueryResult.next();

                // Each BindingSet contains one or more Bindings
                for (Binding binding : bindingSet) {
                    // Each Binding contains the variable name and the value for this result row
                    String name = binding.getName();
                    Value valueT = binding.getValue();

                    System.out.println(name + " = " + valueT);
                }

                // Bindings can also be accessed explicitly by variable name
                //Binding binding = bindingSet.getBinding("x");
            }

            // Once we are done with a particular result we need to close it
            tupleQueryResult.close();

            // Doing more with the same connection object
            // ...
        } catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // It is best to close the connection in a finally block
            try {
				connection.close();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		/*
		String sparqlQuery;
		
		sparqlQuery=""
				+ "PREFIX skos:	<http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX bibo: <http://purl.org/ontology/bibo/>"
				+ "	SELECT ?uri ?geouri { "
					+ "?uri skos:prefLabel \""+value+"\"@en ."
					+ "?uri skos:exactMatch ?geouri."
					+ "FILTER regex(str(?geouri), \"geopolitical\")"
				+ "}";
		
		Query query2 = QueryFactory.create(sparqlQuery); //s2 = the query above
		//QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
		QueryExecution qExe = QueryExecutionFactory.sparqlService(
					"http://83.212.115.164:7200/repositories/agris", query 
				);
		
		
		System.out.println("QEXE str:"+qExe.toString());
		
		ResultSet results = qExe.execSelect();
		
		System.out.println(results.toString());
		
		while(results.hasNext())
	    {
			System.out.println("I got in!");
	        QuerySolution sol = results.nextSolution();
	        RDFNode geouri = sol.get("geouri"); 

	        return geouri.toString();
	        //System.out.println("geouri:"+geouri);
	    }
		
		return "";
	}
	public String queryFAOGeoAG(String value)
	{
		String sparqlQuery;
		
		sparqlQuery=""
				+ "PREFIX skos:	<http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX bibo: <http://purl.org/ontology/bibo/>"
				+ "	SELECT ?uri ?geouri { "
					+ "?uri skos:prefLabel \""+value+"\"@en ."
					+ "?uri skos:exactMatch ?geouri."
					+ "FILTER regex(str(?geouri), \"geopolitical\")"
				+ "}";
		
		Query query = QueryFactory.create(sparqlQuery); //s2 = the query above
		//QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
		QueryExecution qExe = QueryExecutionFactory.sparqlService(
					"http://202.45.139.84:10035/catalogs/fao/repositories/agrovoc", query 
				);
		
		
		ResultSet results = qExe.execSelect();
		while(results.hasNext())
	    {
	        QuerySolution sol = results.nextSolution();
	        RDFNode geouri = sol.get("geouri"); 

	        return geouri.toString();
	        //System.out.println("geouri:"+geouri);
	    }*/
		
		return "";
	}
}






