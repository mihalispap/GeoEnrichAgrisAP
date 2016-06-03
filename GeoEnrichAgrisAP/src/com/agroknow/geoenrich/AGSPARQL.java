package com.agroknow.geoenrich;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class AGSPARQL 
{
	public String queryFAOGeo(String value)
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
	    }
		
		return "";
	}
}






