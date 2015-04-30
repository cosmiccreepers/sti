package uk.ac.shef.dcs.oak.lodie.test;

import com.google.api.client.http.HttpResponseException;
import org.apache.any23.util.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.baseline.*;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.content.KBSearcher_Freebase;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.interpret.*;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.io.LTableAnnotationWriter;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.maincol.MainColumnFinder;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.selector.LTableContentRow_Sampler_nonEmpty;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.stopping.EntropyConvergence;
import uk.ac.shef.dcs.oak.lodie.table.rep.LTable;
import uk.ac.shef.dcs.oak.lodie.table.rep.LTableAnnotation;
import uk.ac.shef.dcs.oak.lodie.table.validator.TabValGeneric;
import uk.ac.shef.dcs.oak.lodie.table.xtractor.TableHODetectorByHTMLTag;
import uk.ac.shef.dcs.oak.lodie.table.xtractor.TableNormalizerFrequentRowLength;
import uk.ac.shef.dcs.oak.lodie.table.xtractor.TableObjCreatorIMDB;
import uk.ac.shef.dcs.oak.lodie.table.xtractor.TableXtractorIMDB;
import uk.ac.shef.dcs.oak.websearch.bing.v2.MultiKeyStringSplitter;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 25/02/14
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class TestTableInterpretation_IMDB_Baseline {
    private static Logger log = Logger.getLogger(TestTableInterpretation_IMDB_Baseline.class.getName());

    public static int[] IGNORE_COLUMNS = new int[]{0, 2, 3};

    public static void main(String[] args) throws IOException {
        String inFolder = args[0];
        String outFolder = args[1];
        String propertyFile = args[2]; //"D:\\Work\\lodiecrawler\\src\\main\\java/freebase.properties"
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertyFile));
        String cacheFolder = args[3];  //String cacheFolder = "D:\\Work\\lodiedata\\tableminer_cache\\solrindex_cache\\zookeeper\\solr";
        String nlpResources = args[4]; //"D:\\Work\\lodie\\resources\\nlp_resources";
        int start = Integer.valueOf(args[5]);
        boolean relationLearning = Boolean.valueOf(args[6]);
        List<Integer> missed_files = new ArrayList<Integer>();
        if (args.length == 8) {
            String in_missed = args[7];
            for (String line : uk.ac.shef.dcs.oak.util.FileUtils.readList(in_missed, false)) {
                missed_files.add(Integer.valueOf(line.split(",")[0].trim()));
            }
        }

        //cache target location

        File configFile = new File(cacheFolder + File.separator + "solr.xml");
        CoreContainer container = new CoreContainer(cacheFolder,
                configFile);
        SolrServer server = new EmbeddedSolrServer(container, "collection1");

        //object to fetch things from KB
        KBSearcher_Freebase freebaseMatcher = new KBSearcher_Freebase(propertyFile, server, true);
        List<String> stopWords = uk.ac.shef.dcs.oak.util.FileUtils.readList(nlpResources + "/stoplist.txt", true);
        //object to find main subject column
        MainColumnFinder main_col_finder = new MainColumnFinder(
                new LTableContentRow_Sampler_nonEmpty(),
                EntropyConvergence.class.getName(),
                new String[]{"0.0", "1", "0.01"},
                server,
                nlpResources, false, stopWords,
                MultiKeyStringSplitter.split(properties.getProperty("BING_API_KEYS")));


        //stop words and stop properties (freebase) are used for disambiguation
        //List<String> stopProperties = FileUtils.readList("D:\\Work\\lodie\\resources\\nlp_resources/stopproperties_freebase.txt", true);

        //object to score columns, and disambiguate entities
        Base_NameMatch_Disambiguator disambiguator = new Base_NameMatch_Disambiguator();

        Base_NameMatch_ColumnLearner column_learner = new Base_NameMatch_ColumnLearner(
                freebaseMatcher,
                disambiguator
        );

        //object to interpret relations between columns
        Baseline_BinaryRelationInterpreter interpreter_relation = new Baseline_BinaryRelationInterpreter(
                new RelationTextMatch_Scorer(0.0, stopWords)
        );


        ColumnInterpreter_relDepend interpreter_with_knownRelations = new ColumnInterpreter_relDepend_exclude_entity_col(
                IGNORE_COLUMNS
        );
        Base_NameMatch_MainInterpreter interpreter = new Base_NameMatch_MainInterpreter(
                main_col_finder,
                column_learner,
                interpreter_relation, interpreter_with_knownRelations,
                IGNORE_COLUMNS, new int[0]);

        LTableAnnotationWriter writer = new LTableAnnotationWriter(
                new TripleGenerator("http://www.freebase.com", "http://lodie.dcs.shef.ac.uk"));


        TableXtractorIMDB xtractor = new TableXtractorIMDB(new TableNormalizerFrequentRowLength(true),
                new TableHODetectorByHTMLTag(),
                new TableObjCreatorIMDB(),
                new TabValGeneric());
        int count = 0;
        List<File> all = Arrays.asList(new File(inFolder).listFiles());
        Collections.sort(all);
        System.out.println(all.size());
        for (File f : all) {
            count++;
            if (count - 1 < start)
                continue;
            if (missed_files.size() != 0 && !missed_files.contains(count))
                continue;


            boolean complete = false;
            String inFile = f.toString();
            try {
                String fileContent = FileUtils.readFileContent(new File(inFile));
                List<LTable> tables = xtractor.extract(fileContent, inFile);

                if (tables.size() == 0)
                    continue;

                LTable table = tables.get(0);

                String sourceTableFile = inFile;
                if (sourceTableFile.startsWith("\"") && sourceTableFile.endsWith("\""))
                    sourceTableFile = sourceTableFile.substring(1, sourceTableFile.length() - 1).trim();
                System.out.println(count + "_" + sourceTableFile + " " + new Date());
                log.info(">>>" + count + "_" + sourceTableFile);

                complete = process(interpreter, table, sourceTableFile, writer, outFolder, relationLearning);
                //server.commit();
                if (TableMinerConstants.COMMIT_SOLR_PER_FILE)
                    server.commit();
                if (!complete) {
                    System.out.println("\t\t\t missed: " + count + "_" + sourceTableFile);
                    PrintWriter missedWriter = null;
                    try {
                        missedWriter = new PrintWriter(new FileWriter("ti_imdb_missed_base.csv", true));
                        missedWriter.println(count + "," + inFile);
                        missedWriter.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                //gs annotator

            } catch (Exception e) {
                e.printStackTrace();
                PrintWriter missedWriter = null;
                try {
                    missedWriter = new PrintWriter(new FileWriter("ti_imdb_missed_base.csv", true));
                    missedWriter.println(count + "," + inFile);
                    missedWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                e.printStackTrace();
                server.shutdown();
                System.exit(1);
            }

        }
        server.shutdown();
    }


    public static boolean process(Base_NameMatch_MainInterpreter interpreter, LTable table, String sourceTableFile, LTableAnnotationWriter writer,
                                  String outFolder, boolean relationLearning) throws FileNotFoundException {
        String outFilename = sourceTableFile.replaceAll("\\\\", "/");
        try {
            LTableAnnotation annotations = interpreter.start(table, relationLearning);

            int startIndex = outFilename.lastIndexOf("/");
            if (startIndex != -1) {
                outFilename = outFilename.substring(startIndex + 1).trim();
            }
            writer.writeHTML(table, annotations, outFolder + "/" + outFilename + ".html");

        } catch (Exception ste) {
            if (ste instanceof SocketTimeoutException || ste instanceof HttpResponseException) {
                ste.printStackTrace();
                System.out.println("Remote server timed out, continue 10 seconds. Missed." + outFilename);
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
                return false;
            } else
                ste.printStackTrace();

        }
        return true;
    }
}
