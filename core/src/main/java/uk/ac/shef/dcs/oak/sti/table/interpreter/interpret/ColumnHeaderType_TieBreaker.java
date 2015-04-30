package uk.ac.shef.dcs.oak.sti.table.interpreter.interpret;

import uk.ac.shef.dcs.oak.sti.table.rep.HeaderAnnotation;
import uk.ac.shef.dcs.oak.sti.table.rep.LTable;
import uk.ac.shef.dcs.oak.sti.table.rep.LTableAnnotation;

import java.util.*;

/**
 */
public class ColumnHeaderType_TieBreaker {

    public static List<String> consistent_domain_freebase(List<String> candidateTypes, LTableAnnotation tab_annotations, LTable table) {
        Set<String> domain_words = new HashSet<String>();
        for (int col = 0; col < table.getNumCols(); col++) {
            List<HeaderAnnotation> annotations = tab_annotations.getBestHeaderAnnotations(col);
            if (annotations == null || annotations.size() == 0)
                continue;
            for (HeaderAnnotation ha : annotations) {
                String type = ha.getAnnotation_url();
                int slash = type.lastIndexOf("/");
                if (slash != -1)
                    type = type.substring(0, slash).trim();

                for (String d : type.split("/")) {
                    if (d.trim().length() > 0)
                        domain_words.add(d.trim());
                }
            }
        }

        String best = null;
        int max_score = 0;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String otype : candidateTypes) {

            int slash = otype.lastIndexOf("/");
            String type = otype;
            if (slash != -1)
                type = type.substring(0, slash).trim();
            Set<String> candidate_type_domain_words = new HashSet<String>();
            for (String d : type.split("/")) {
                if (d.trim().length() > 0) candidate_type_domain_words.add(d.trim());
            }

            candidate_type_domain_words.retainAll(domain_words);
            map.put(otype, candidate_type_domain_words.size());
            if (candidate_type_domain_words.size() >= max_score) {
                max_score = candidate_type_domain_words.size();
            }
        }

        Iterator<String> it = candidateTypes.iterator();
        while (it.hasNext()) {
            String type = it.next();
            Integer score = map.get(type);
            if (score==null||score != max_score)
                it.remove();
        }
        return candidateTypes;
    }
}