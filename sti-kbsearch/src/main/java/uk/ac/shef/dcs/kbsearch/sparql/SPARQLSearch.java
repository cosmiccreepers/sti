package uk.ac.shef.dcs.kbsearch.sparql;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.shef.dcs.kbsearch.KBSearch;

import java.io.IOException;
import java.util.*;


/**
 * test queries:
 * <p>
 * SELECT DISTINCT ?s ?o WHERE {
 * ?s <http://www.w3.org/2000/01/rdf-schema#label> ?o .
 * FILTER ( regex (str(?o), "\\bcat\\b", "i") ) }
 * <p>
 * <p>
 * SELECT DISTINCT ?s WHERE {
 * ?s <http://www.w3.org/2000/01/rdf-schema#label> "Nature Cat"@en .
 * }
 * <p>
 * SELECT DISTINCT ?p ?o WHERE {
 * wd:Q21043336 ?p ?o .
 * }
 */
public abstract class SPARQLSearch extends KBSearch {

    protected String sparqlEndpoint;

    protected static final Map SPARQL_ESCAPE_SEARCH_REPLACEMENTS = ImmutableMap.builder()
            .put("\t", "\\t")
            .put("\n", "\\n")
            .put("\r", "\\r")
            .put("\b", "\\b")
            .put("\f", "\\f")
            .put("\"", "\\\"")
            .put("'", "\\'")
            .put("\\", "\\\\")
            .build();

    protected StringMetric stringMetric = new Levenshtein();

    /**
     * @param fuzzyKeywords   given a query string, kbsearch will firstly try to fetch results matching the exact query. when no match is
     *                        found, you can set fuzzyKeywords to true, to let kbsearch to break the query string based on conjunective words.
     *                        So if the query string is "tom and jerry", it will try "tom" and "jerry"
     * @param cacheEntity     the solr instance to cache retrieved entities from the kb. pass null if not needed
     * @param cacheConcept    the solr instance to cache retrieved classes from the kb. pass null if not needed
     * @param cacheProperty   the solr instance to cache retrieved properties from the kb. pass null if not needed
     * @param cacheSimilarity the solr instance to cache computed semantic similarity between entity and class. pass null if not needed
     * @throws IOException
     */
    public SPARQLSearch(String sparqlEndpoint, Boolean fuzzyKeywords,
                        EmbeddedSolrServer cacheEntity,
                        EmbeddedSolrServer cacheConcept,
                        EmbeddedSolrServer cacheProperty,
                        EmbeddedSolrServer cacheSimilarity) throws IOException {
        super(fuzzyKeywords, cacheEntity, cacheConcept, cacheProperty, cacheSimilarity);
        this.sparqlEndpoint = sparqlEndpoint;
    }


    /**
     * SELECT DISTINCT ?s ?o
     * WHERE
     * {
     * {
     * ?s  <http://www.w3.org/2000/01/rdf-schema#label>  ?o      .
     * ?o  bif:contains                                  "Ramji"
     * }
     * UNION
     * {
     * ?s  <http://www.w3.org/2000/01/rdf-schema#label>  ?o      .
     * ?o  bif:contains                                  "Manjhi"
     * }
     * }
     * ORDER BY ?s
     *
     * @param content
     * @return
     */
    protected String createRegexQuery(String content) {
        String query = "SELECT DISTINCT ?s ?o WHERE {" +
                "?s <" + RDFEnum.RELATION_HASLABEL.getString() + "> ?o . \n" +
                //"?o bif:contains \""+content+"\"}";
                "FILTER ( regex (str(?o), \"\\\\b" + content + "\\\\b\", \"i\") +" +
                "&& (regex(str(?s), \"^http://dbpedia.org/ontology\", \"i\") || regex(str(?s),\"^http://dbpedia.org/resource\",\"i\") ) ) }";
        return query;
    }

    protected String createRegexQuery(String content, String... types) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT ?s ?o WHERE {").append(
                "?s <").append(RDFEnum.RELATION_HASLABEL.getString()).append("> ?o .").append("\n");

        if (types.length > 0) {
            query.append("{?s a <").append(types[0]).append(">}\n");
            for (int i = 1; i < types.length; i++) {
                query.append("UNION { ?s a <").append(types[i]).append(">}\n");
            }
            query.append(".\n");
        }

        query.append("FILTER ( regex (str(?o), \"\\b").append(content).append("\\b\", \"i\") && (regex(str(?s), \"^http://dbpedia.org/ontology\", \"i\") || regex(str(?s), \"^http://dbpedia.org/resource\",\"i\") ) }");
        return query.toString();
    }

    protected String createExactMatchQueries(String content) {
        String query = "SELECT DISTINCT ?s WHERE {" +
                "?s <" + RDFEnum.RELATION_HASLABEL.getString() + "> \"" + content + "\"@en .\n" +
                "FILTER (regex(str(?s), \"^http://dbpedia.org/ontology\", \"i\")  \n" +
                        "|| regex(str(?s), \"http://dbpedia.org/resource\",\"i\") ) .\n" +
                "}";

        return query;
    }

    protected String createExactMatchWithOptionalTypes(String content) {
        String query = "SELECT DISTINCT ?s ?o WHERE {" +
                "?s <" + RDFEnum.RELATION_HASLABEL.getString() + "> \"" + content + "\"@en . \n" +
                "FILTER (regex(str(?s), \"^http://dbpedia.org/ontology\", \"i\") \n" +
                        "|| regex(str(?s), \"http://dbpedia.org/resource\",\"i\") ) .\n" +
                "OPTIONAL {?s a ?o} }";

        return query;
    }

    protected String createGetLabelQuery(String content) {
        content = content.replaceAll("\\s+", "");
        String query = "SELECT DISTINCT ?o WHERE {<" +
                content + "> <" + RDFEnum.RELATION_HASLABEL.getString() + "> ?o .\n" +
                "FILTER (regex(str(?s), \"^http://dbpedia.org/ontology\", \"i\") \n" +
                "|| regex(str(?s), \"http://dbpedia.org/resource\",\"i\") ) .\n" +
                "}";

        return query;

    }


    protected List<Pair<String, String>> queryByLabel(String sparqlQuery, String string) {
        org.apache.jena.query.Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);

        List<Pair<String, String>> out = new ArrayList<>();
        ResultSet rs = qexec.execSelect();
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            RDFNode range = qs.get("?s");
            String r = range.toString();
            RDFNode domain = qs.get("?o");
            String d = null;
            if (domain != null)
                d = domain.toString();
            if (d == null)
                d = string;
            out.add(new ImmutablePair<>(r, d));
        }
        return out;
    }

    protected abstract List<String> queryForLabel(String sparqlQuery, String resourceURI);


    protected void rank(List<Pair<String, String>> candidates, String originalQueryLabel) {
        final Map<Pair<String, String>, Double> scores = new HashMap<>();
        for (Pair<String, String> p : candidates) {
            String label = p.getValue();
            double s = stringMetric.compare(label, originalQueryLabel);
            scores.put(p, s);
        }

        Collections.sort(candidates, (o1, o2) -> {
            Double s1 = scores.get(o1);
            Double s2 = scores.get(o2);
            return s2.compareTo(s1);
        });
    }


    public static String escape(String string) {

        StringBuffer bufOutput = new StringBuffer(string);
        for (int i = 0; i < bufOutput.length(); i++) {

            Object replacement = SPARQL_ESCAPE_SEARCH_REPLACEMENTS.get("" + bufOutput.charAt(i));
            if (replacement != null) {
                bufOutput.deleteCharAt(i);
                bufOutput.insert(i, replacement);
                // advance past the replacement
                i += (replacement.toString().length() - 1);
            }

        }
        return bufOutput.toString();
    }
}
