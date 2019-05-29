/*

package uk.ac.shef.dcs.websearch.bing.v7;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import uk.ac.shef.dcs.websearch.SearchResultParser;
import uk.ac.shef.dcs.websearch.WebSearch;

import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchAPI;
import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchManager;
import com.microsoft.azure.cognitiveservices.search.websearch.models.SearchResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class BingSearchV7 extends WebSearch {

    protected static final String BING_BASE_URL="bing.url";
    protected static final String BING_KEYS="bing.keys";
    protected SearchResultParser parser;

    protected List<String> accountKeyPool;
    protected Map<String, Date> obsoleteAccountKeys;
    protected String baseURL;

    protected BingWebSearchAPI client;

    public BingSearchV7(Properties properties) throws IOException {
        super(properties);

        obsoleteAccountKeys = new HashMap<>();
        this.accountKeyPool = new ArrayList<>();
        for (String k : StringUtils.split(
                this.properties.getProperty(BING_KEYS),','
        )) {
            k = k.trim();
            if(k.length()>0)
                accountKeyPool.add(k);
        }
        this.baseURL = this.properties.getProperty(BING_BASE_URL);
        this.client = BingWebSearchManager.authenticate(accountKeyPool.get(0));
    }

    public InputStream search(String query) throws IOException {
        query = "'" + query + "'";
        query = URLEncoder.encode(query, "UTF-8");
        // query = baseURL + query + "&$format=json&$top=20";

        for (String k : accountKeyPool) {
            if (obsoleteAccountKeys.get(k) != null)
                continue;

            try{
                SearchResponse webData = client.bingWebs().search()
                        .withQuery(query)
                        .withMarket("en-us")
                        .withCount(10)
                        .execute();
            } catch (IOException ioe) {
                System.err.println("> Bing search exception. Apps built on top may produce incorrect results.");
                if (ioe.getMessage().contains("Server returned HTTP response code: 401") || ioe.getMessage().contains("Server returned HTTP response code: 503")) {
                    keyInvalid = true;
                    ioe.printStackTrace();
                } else
                    throw ioe;
            }

            InputStream is = null;

            // Hangover from V2 maybe consider how this works?
            // if (keyInvalid) {
            //    obsoleteAccountKeys.put(k, new Date());
            //    continue;
            // }

            // build IS from the query results
            return is;
        }
        return null;
    }

    @Override
    public SearchResultParser getResultParser() {
        if(parser==null)
            parser=new BingSearchResultParser();
        return parser;
    }

    public static void main(String[] args) throws IOException, APIKeysDepletedException {

    }
}
*/