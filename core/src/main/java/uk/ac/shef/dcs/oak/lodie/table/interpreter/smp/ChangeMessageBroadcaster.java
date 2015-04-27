package uk.ac.shef.dcs.oak.lodie.table.interpreter.smp;

import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.impl.SparseObjectMatrix2D;
import uk.ac.shef.dcs.oak.lodie.table.rep.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ChangeMessageBroadcaster {

    public ObjectMatrix2D computeChangeMessages(LTableAnnotation tableAnnotation, LTable table) {
        ObjectMatrix2D messages = new SparseObjectMatrix2D(table.getNumRows(), table.getNumCols());
        //messages by column header
        for (int col = 0; col < table.getNumCols(); col++) {
            List<HeaderAnnotation> bestHeaderAnnotations = tableAnnotation.getBestHeaderAnnotations(col);
            if (bestHeaderAnnotations.size() == 0)
                continue;

            for (int row = 0; row < table.getNumRows(); row++) {
                List<CellAnnotation> cellAnnotations = tableAnnotation.getBestContentCellAnnotations(row, col);
                if (cellAnnotations.size() == 0)
                    continue;

                List<String> headerAnnotationStrings = new ArrayList<String>();
                for (HeaderAnnotation ha : bestHeaderAnnotations)
                    headerAnnotationStrings.add(ha.getAnnotation_url());
                boolean sendChange = false;
                for (CellAnnotation best : cellAnnotations) { //this cell can have multiple annotations with the same highest score
                    //we need to check everyone of them. if any one's type does not overlap with the header annotations, it need changing
                    List<String> copy = new ArrayList<String>(headerAnnotationStrings);
                    copy.retainAll(best.getAnnotation().getTypeIds());
                    if (copy.size() == 0) {
                        sendChange = true;
                        break;
                    }
                }
                if (sendChange) {
                    ChangeMessage m = new ChangeMessage();
                    for (HeaderAnnotation ha : bestHeaderAnnotations) {
                        m.setConfidence(ha.getFinalScore());
                        m.addLabel(ha.getAnnotation_url());
                        updateMessageForCell(messages, row, col, m);
                    }
                }
            }
        }

        //messages by relations
        Map<Key_SubjectCol_ObjectCol, List<HeaderBinaryRelationAnnotation>> relations = tableAnnotation.getRelationAnnotations_across_columns();
        for (Map.Entry<Key_SubjectCol_ObjectCol, List<HeaderBinaryRelationAnnotation>> e : relations.entrySet()) {
            Key_SubjectCol_ObjectCol subobj_col_ids = e.getKey();
            List<HeaderBinaryRelationAnnotation> relationAnnotations = e.getValue();
            Collections.sort(relationAnnotations);
            double maxScore_of_relation_across_columns = relationAnnotations.get(0).getFinalScore();
            List<String> highestScoringRelationStrings = new ArrayList<String>();
            for (HeaderBinaryRelationAnnotation hba : relationAnnotations) {
                if (hba.getFinalScore() == maxScore_of_relation_across_columns)
                    highestScoringRelationStrings.add(hba.getAnnotation_label());
            }

            Map<Integer, List<CellBinaryRelationAnnotation>>
                    relationAnnotations_per_row = tableAnnotation.getRelationAnnotations_per_row().get(subobj_col_ids);

            List<Integer> rows_annotated_with_relations = new ArrayList<Integer>(relationAnnotations_per_row.keySet());
            for (int row = 0; row < tableAnnotation.getRows(); row++) {
                boolean hasMatch = false;
                if (rows_annotated_with_relations.contains(row)) {
                    List<CellBinaryRelationAnnotation> relations_on_row = relationAnnotations_per_row.get(row);

                    if (relations_on_row.size() != 0) {
                        Collections.sort(relations_on_row);
                        double maxScore = relations_on_row.get(0).getScore();
                        for (CellBinaryRelationAnnotation cra : relations_on_row) {
                            if (cra.getScore() == maxScore && highestScoringRelationStrings.contains(cra.getAnnotation_url())) {
                                hasMatch = true;
                                break;
                            }
                        }
                    }
                }

                if (!hasMatch) {
                    ChangeMessageFromColumnsRelation forSubjectCell = new ChangeMessageFromColumnsRelation();
                    forSubjectCell.setLabels(highestScoringRelationStrings);
                    forSubjectCell.setConfidence(maxScore_of_relation_across_columns);
                    forSubjectCell.setFlag_subOrObj(0);
                    updateMessageForCell(messages, row, subobj_col_ids.getSubjectCol(), forSubjectCell);

                    ChangeMessageFromColumnsRelation forObjectCell = new ChangeMessageFromColumnsRelation();
                    forObjectCell.setLabels(highestScoringRelationStrings);
                    forObjectCell.setConfidence(maxScore_of_relation_across_columns);
                    forObjectCell.setFlag_subOrObj(1);
                    updateMessageForCell(messages, row, subobj_col_ids.getObjectCol(), forObjectCell);
                }
            }
        }
        return messages;
    }

    private void updateMessageForCell(ObjectMatrix2D messages, int row, int col, ChangeMessage m) {
        Object container = messages.get(row, col);
        List<ChangeMessage> messages_at_cell = null;
        if (container == null) {
            messages_at_cell = new ArrayList<ChangeMessage>();
        } else {
            messages_at_cell = (List<ChangeMessage>) container;
        }

        messages_at_cell.add(m);
        messages.set(row, col, messages_at_cell);
    }


}
