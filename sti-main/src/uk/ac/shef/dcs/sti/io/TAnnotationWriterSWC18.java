package uk.ac.shef.dcs.sti.io;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.reasoner.rulesys.builtins.Print;
import uk.ac.shef.dcs.kbsearch.model.Clazz;
import uk.ac.shef.dcs.sti.core.model.*;
import uk.ac.shef.dcs.sti.util.TripleGenerator;

import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

public class TAnnotationWriterSWC18 extends TAnnotationWriter {
    /*
    Class to generate output in compliance with the competition requirements for the Semantic Web Conference 18 (2019)
    class, entity and property recognition challenges on tabular data (http://www.cs.ox.ac.uk/isg/challenges/sem-tab/).
    This requires a CSV output for each task that includes  lines: (Table_ID, Column_ID, DBpedia Class/entity/property)
    Current approach is to take highest scoring candidates of each type. For entity matching, this may not lead to the
    best matches, as we are not asserting the column class on the entities.
     */

    public TAnnotationWriterSWC18(TripleGenerator tripleGenerator, String outFolder) {
        super(tripleGenerator);

        File entities = new File(outFolder + "\\entities.csv");
        File relations = new File (outFolder + "\\relations.csv");
        File colClass = new File (outFolder + "\\colClass.csv");

        // Delete files if they exist here
        entities.delete();
        relations.delete();
        colClass.delete();

    }

    public void writeCSV(String filename, List<List<String>> datalines) throws IOException {
        FileWriter pw = new FileWriter(filename,true);
        Iterator it_line = datalines.iterator();
        while(it_line.hasNext()){
            List<String> this_line = (List<String>) it_line.next();
            Iterator it_fields = this_line.iterator();
            while(it_fields.hasNext()) {
                String s = it_fields.next().toString();
                pw.append(s);
                if (it_fields.hasNext())
                    pw.append(",");
                else
                    pw.append("\n");
            }
        }
        pw.flush();
        pw.close();
    }

    protected void writeCellKeyFile(Table table, TAnnotation table_annotation, String cell_key) throws FileNotFoundException {

        String path_prefix = FilenameUtils.getFullPath(cell_key);

        List<JSONOutputCellAnnotation> jsonCells = new ArrayList<>();
        List<List<String>> cellCandidateEntityAndClass = new ArrayList<>();
        for (int r = 0; r < table.getNumRows(); r++) {
            for (int c = 0; c < table.getNumCols(); c++) {
                JSONOutputCellAnnotation jc = new JSONOutputCellAnnotation(r, c, table.getContentCell(r,c).getText());
                TCellAnnotation[] cans = table_annotation.getContentCellAnnotations(r, c);
                List<String> entityClass = new ArrayList<>();
                entityClass.add(FilenameUtils.getBaseName(table.getSourceId())); // get the table id
                entityClass.add(Integer.toString(r));
                entityClass.add(Integer.toString(c));
                if (cans.length==0) {
                    entityClass.add(" ");
                }
                else {
                     List <Clazz> ann_types = cans[0].getAnnotation().getTypes();
                     ann_types.add(new Clazz(" "," "));
                     entityClass.add(ann_types.get(0).getId());
                }
                cellCandidateEntityAndClass.add(entityClass);
                jsonCells.add(jc); // we don't actually use this
            }
        }

        try {
            this.writeCSV(path_prefix + "entities.csv", cellCandidateEntityAndClass);
        }
        catch (IOException io_except){
            //Not sure what to do here
        }
    }

    protected void writeRelationKeyFile(TAnnotation table_annotation, String relation_key) throws FileNotFoundException {
        String path_prefix = FilenameUtils.getFullPath(relation_key);
        String table_id = FilenameUtils.getBaseName(relation_key); // need to split out the extensions
        List<JSONOutputRelationAnnotation> jrs = new ArrayList<>();
        List<List<String>> columnrelations = new ArrayList<>();

        for (Map.Entry<RelationColumns, java.util.List<TColumnColumnRelationAnnotation>> e :
                table_annotation.getColumncolumnRelations().entrySet()) {
            List<String> relation = new ArrayList<>();
            relation.add(table_id);
            relation.add((Integer.toString(e.getKey().getSubjectCol())));
            relation.add((Integer.toString(e.getKey().getObjectCol())));
            java.util.List<TColumnColumnRelationAnnotation> candidates = e.getValue();
            Collections.sort(candidates);
            relation.add(candidates.get(0).getRelationURI());
            columnrelations.add(relation);
        }

        try{
            this.writeCSV(path_prefix + "relations.csv", columnrelations);
        }
        catch (IOException io_except){
            //dunno what to do here
        }
    }

    protected void writeHeaderKeyFile(Table table, TAnnotation table_annotation, String header_key) throws FileNotFoundException {
        // PrintWriter p = new PrintWriter(header_key);

        List<JSONOutputColumnAnnotation> jsonColumns = new ArrayList<>();
        List<List<String>> columnLabels = new ArrayList<>();
        String path_prefix = FilenameUtils.getFullPath(header_key);

        for (int c = 0; c < table.getNumCols(); c++) {
            TColumnHeaderAnnotation[] anns = table_annotation.getHeaderAnnotation(c);
            JSONOutputColumnAnnotation jc = new JSONOutputColumnAnnotation(c, table.getColumnHeader(c).getHeaderText());
            List <String> columnLine = new ArrayList<>();
            columnLine.add(FilenameUtils.getBaseName(table.getSourceId()));
            columnLine.add(Integer.toString(c));
            String topCandidate = "";
            if (table_annotation.getHeaderAnnotation(c).length > 0){
                topCandidate = table_annotation.getWinningHeaderAnnotations(c).get(0).toString();
            }
            columnLine.add(topCandidate);
            columnLabels.add(columnLine);
        }

        try{
            this.writeCSV(path_prefix + "colclass.csv",columnLabels);
        }
        catch (IOException io_except) {
            // Not sure what to put here
        }
    }
}
