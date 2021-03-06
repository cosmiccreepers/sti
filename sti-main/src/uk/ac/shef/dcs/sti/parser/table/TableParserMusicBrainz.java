package uk.ac.shef.dcs.sti.parser.table;

import org.apache.any23.extractor.html.DomUtils;
import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import uk.ac.shef.dcs.sti.STIException;
import uk.ac.shef.dcs.sti.core.model.TContext;
import uk.ac.shef.dcs.sti.core.model.Table;
import uk.ac.shef.dcs.sti.parser.table.creator.TableObjCreatorMusicBrainz;
import uk.ac.shef.dcs.sti.parser.table.hodetector.TableHODetector;
import uk.ac.shef.dcs.sti.parser.table.hodetector.TableHODetectorByHTMLTag;
import uk.ac.shef.dcs.sti.parser.table.normalizer.TableNormalizer;
import uk.ac.shef.dcs.sti.parser.table.creator.TableObjCreator;
import uk.ac.shef.dcs.sti.parser.table.context.TableContextExtractorMusicBrainz;
import uk.ac.shef.dcs.sti.parser.table.normalizer.TableNormalizerSimple;
import uk.ac.shef.dcs.sti.parser.table.validator.TableValidatorGeneric;
import uk.ac.shef.dcs.sti.parser.table.validator.TableValidator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.joox.JOOX.$;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 20/02/14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class TableParserMusicBrainz extends TableParser implements Browsable{

    public TableParserMusicBrainz(){
        super(new TableNormalizerSimple(),
                new TableHODetectorByHTMLTag(),
                new TableObjCreatorMusicBrainz(),
                new TableValidatorGeneric());
    }

    public TableParserMusicBrainz(TableNormalizer normalizer, TableHODetector detector, TableObjCreator creator, TableValidator... validators) {
        super(normalizer, detector, creator, validators);
    }

    @Override
    public List<Table> extract(String inFile, String sourceId) throws STIException {

        String input;
        try {
            input = FileUtils.readFileToString(new File(inFile));
        } catch (IOException e) {
            throw new STIException(e);
        }
        List<Table> rs = new ArrayList<>();
        parser = new TagSoupParser(new ByteArrayInputStream(input.getBytes()), sourceId,"UTF-8");
        Document doc = null;
        try {
            doc = parser.getDOM();
        } catch (IOException e) {
            return rs;
        }

        List<Node> tables = DomUtils.findAll(doc, "//TABLE[@class='tbl']");
        List<TContext> contexts = new ArrayList<>();
        try {
            contexts = new TableContextExtractorMusicBrainz().extract(new File(sourceId), doc);
        } catch (STIException e) {
            e.printStackTrace();
        }
        int tableCount = 0;
        for (Node n : tables) {
            tableCount++;

            TContext[] contexts_array = new TContext[contexts.size()];
            for (int i = 0; i < contexts.size(); i++)
                contexts_array[i] = contexts.get(i);
            Table table = extractTable(n, String.valueOf(tableCount),
                    sourceId, contexts_array);
            if (table != null)
                rs.add(table);

        }
        return rs;
    }

    @Override
    public List<String> extract(String inFile, String sourceId, String outputFolder) throws STIException {
        Document doc = createDocument(inFile, sourceId);

        List<Node> tables = DomUtils.findAll(doc, "//TABLE[@class='tbl']");
        List<String> xpaths = BrowsableHelper.createBrowsableElements(tables, doc);

        BrowsableHelper.output(inFile, outputFolder, doc);
        return xpaths;

    }


}
