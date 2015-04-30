package uk.ac.shef.dcs.oak.sti.table.interpreter.baseline;

import uk.ac.shef.dcs.oak.kbsearch.Entity;
import uk.ac.shef.dcs.oak.sti.table.rep.LTable;
import uk.ac.shef.dcs.oak.sti.table.rep.LTableContentCell;
import uk.ac.shef.dcs.oak.util.ObjObj;

import java.io.IOException;
import java.util.*;

/**
 */
public class Base_NameMatch_Disambiguator {

    public List<ObjObj<Entity, Map<String, Double>>> disambiguate(List<Entity> candidates, LTable table,
                                                                           int entity_row, int entity_column
    ) throws IOException {
        //do disambiguation scoring
        //log.info("\t>> Disambiguation-LEARN, position at (" + entity_row + "," + entity_column + ") candidates=" + candidates.size());
        System.out.println("\t>> Disambiguation-, position at [" + entity_row + "," + entity_column + "]: " + table.getContentCell(entity_row, entity_column) +
                " candidates=" + candidates.size());
        List<ObjObj<Entity, Map<String, Double>>> disambiguationScores = new ArrayList<ObjObj<Entity, Map<String, Double>>>();
        if (candidates.size() > 0) {
            List<Entity> candidatesCopy = new ArrayList<Entity>();
            for (Entity ec : candidates) {
                LTableContentCell tcc = table.getContentCell(entity_row, entity_column);
                if (tcc.getText() != null) {
                    if (ec.getName().equalsIgnoreCase(tcc.getText().trim()))
                        candidatesCopy.add(ec);
                }
            }

            if (candidatesCopy.size() > 0) {

                disambiguationScores.add(new ObjObj<Entity, Map<String, Double>>(
                        candidatesCopy.get(0), new HashMap<String, Double>()
                ));
            } else {
                disambiguationScores.add(new ObjObj<Entity, Map<String, Double>>(
                        candidates.get(0), new HashMap<String, Double>()
                ));
            }

        }
        return disambiguationScores;
    }

    public List<ObjObj<Entity, Map<String, Double>>> revise(List<ObjObj<Entity, Map<String, Double>>> entities_for_this_cell_and_scores,
                                                                     List<String> types) {

        Iterator<ObjObj<Entity, Map<String, Double>>> it = entities_for_this_cell_and_scores.iterator();
        List<ObjObj<Entity, Map<String, Double>>> original = new ArrayList<ObjObj<Entity, Map<String, Double>>>(
                entities_for_this_cell_and_scores
        );

        while (it.hasNext()) {
            ObjObj<Entity, Map<String, Double>> oo = it.next();
            List<String> entity_types = oo.getMainObject().getTypeIds();
            entity_types.retainAll(types);
            if (entity_types.size() == 0)
                it.remove();
            /*double pre_final = oo.getOtherObject().get("final");
            oo.getOtherObject().put("final", type_match_score + pre_final);*/
        }

        if (entities_for_this_cell_and_scores.size() == 0)
            return original;

        return entities_for_this_cell_and_scores;
        //To change body of created methods use File | Settings | File Templates.
    }
}
