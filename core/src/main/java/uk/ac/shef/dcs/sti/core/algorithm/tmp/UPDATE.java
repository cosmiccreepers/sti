package uk.ac.shef.dcs.sti.core.algorithm.tmp;

import javafx.util.Pair;
import org.apache.log4j.Logger;
import uk.ac.shef.dcs.kbsearch.KBSearch;
import uk.ac.shef.dcs.kbsearch.KBSearchException;
import uk.ac.shef.dcs.kbsearch.freebase.FreebaseEnum;
import uk.ac.shef.dcs.kbsearch.rep.Attribute;
import uk.ac.shef.dcs.sti.STIException;
import uk.ac.shef.dcs.sti.core.scorer.ClazzScorer;
import uk.ac.shef.dcs.sti.nlp.NLPTools;
import uk.ac.shef.dcs.sti.core.sampler.TContentCellRanker;
import uk.ac.shef.dcs.sti.experiment.TableMinerConstants;
import uk.ac.shef.dcs.kbsearch.rep.Entity;
import uk.ac.shef.dcs.kbsearch.rep.Resource;
import uk.ac.shef.dcs.sti.core.model.*;
import uk.ac.shef.dcs.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**

 */
public class UPDATE {

    private static final Logger LOG = Logger.getLogger(UPDATE.class.getName());
    private TCellDisambiguator disambiguator;
    private KBSearch kbSearch;
    private ClazzScorer clazzScorer;
    private String nlpResourcesDir;
    private TContentCellRanker selector;
    private List<String> stopWords;

    public UPDATE(TContentCellRanker selector,
                  KBSearch kbSearch,
                  TCellDisambiguator disambiguator,
                  ClazzScorer clazzScorer,
                  List<String> stopwords,
                  String nlpResourcesDir) {
        this.selector = selector;
        this.kbSearch = kbSearch;
        this.disambiguator = disambiguator;
        this.clazzScorer = clazzScorer;
        this.nlpResourcesDir = nlpResourcesDir;
        this.stopWords = stopwords;
    }

    /**
     * start the UPDATE process
     *
     * @param interpretedColumnIndexes
     * @param table
     * @param currentAnnotation
     * @throws KBSearchException
     * @throws STIException
     */
    public void update(
            List<Integer> interpretedColumnIndexes,
            Table table,
            TAnnotation currentAnnotation
    ) throws KBSearchException, STIException {

        int currentIteration = 0;
        TAnnotation prevAnnotation = new TAnnotation(currentAnnotation.getRows(), currentAnnotation.getCols());
        TAnnotation.copy(currentAnnotation, prevAnnotation);
        List<String> domainRep;
        Set<String> allEntityIds = new HashSet<>();
        boolean converged;
        do {
            ///////////////// solution 2: both prev and current iterations' headers will have dc scores added
            LOG.info("\t>> UPDATE begins, iteration:" + currentIteration);
            allEntityIds.addAll(collectAllEntityCandidateIds(table, currentAnnotation));
            //current iteration annotation header scores does not contain dc scores

            //headers will have dc computeElementScores added
            domainRep = createDomainRep(table, currentAnnotation, interpretedColumnIndexes);
            //update clazz scores with dc scores
            updateClazzScoresByDC(currentAnnotation, domainRep, interpretedColumnIndexes);

            prevAnnotation = new TAnnotation(currentAnnotation.getRows(),
                    currentAnnotation.getCols());
            TAnnotation.copy(currentAnnotation, prevAnnotation);

            //scores will be reset, then recalculated. dc scores lost
            reviseColumnAndCellAnnotations(allEntityIds,
                    table, currentAnnotation, interpretedColumnIndexes);

            // NO NEED!!! DC computeElementScores already included when "revise_cell_disam..."
            //updateClazzScoresByDC(current_iteration_annotation, domain_representation, interpreted_columns);
            //both prev and current iterations' header annotations do not have dc scores
            converged = checkConvergence(prevAnnotation, currentAnnotation,
                    table.getNumRows(), interpretedColumnIndexes);
            if (!converged) {
                prevAnnotation = new TAnnotation(currentAnnotation.getRows(),
                        currentAnnotation.getCols());
                TAnnotation.copy(currentAnnotation,
                        prevAnnotation);
            }
            currentIteration++;
        } while (!converged && currentIteration < TableMinerConstants.UPDATE_PHASE_MAX_ITERATIONS);

        if (currentIteration >= TableMinerConstants.UPDATE_PHASE_MAX_ITERATIONS) {
            LOG.info("\t>> UPDATE CANNOT STABILIZED AFTER " + currentIteration + " ITERATIONS, Stopped");
            if (prevAnnotation != null) {
                currentAnnotation = new TAnnotation(prevAnnotation.getRows(),
                        prevAnnotation.getCols());
                TAnnotation.copy(prevAnnotation,
                        currentAnnotation);
            }
        } else
            LOG.info("\t>> UPDATE STABLIZED AFTER " + currentIteration + " ITERATIONS");

    }

    private Set<String> collectAllEntityCandidateIds(Table table, TAnnotation prevAnnotation) {
        Set<String> ids = new HashSet<>();
        for (int col = 0; col < table.getNumCols(); col++) {
            for (int row = 0; row < table.getNumRows(); row++) {
                TCellAnnotation[] cas = prevAnnotation.getContentCellAnnotations(row, col);
                if (cas == null)
                    continue;
                for (TCellAnnotation ca : cas) {
                    ids.add(ca.getAnnotation().getId());
                }
            }
        }
        return ids;
    }

    public List<String> createDomainRep(Table table, TAnnotation currentAnnotation, List<Integer> interpretedColumns) {
        List<String> domain = new ArrayList<>();
        for (int c : interpretedColumns) {
            for (int r = 0; r < table.getNumRows(); r++) {
                TCellAnnotation[] annotations = currentAnnotation.getContentCellAnnotations(r, c);
                if (annotations != null && annotations.length > 0) {
                    Entity ec = annotations[0].getAnnotation();
                    try {
                        domain.addAll(createEntityDomainRep(ec));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return domain;
    }

    private Collection<? extends String> createEntityDomainRep(Entity ec) throws IOException {
        List<String> domain = new ArrayList<>();
        for (Attribute fact : ec.getAttributes()) {
            if (fact.getRelationURI().equals(FreebaseEnum.RELATION_HASDESCRIPTION.getString())) {
                String[] sentences = NLPTools.getInstance(nlpResourcesDir).getSentenceSplitter().sentDetect(fact.getValue());
                String first = sentences.length > 0 ? sentences[0] : "";
                List<String> tokens = StringUtils.toBagOfWords(first, true, true, true);
                domain.addAll(tokens);
            }
        }
        domain.removeAll(stopWords);
        return domain;
    }


    private void updateClazzScoresByDC(TAnnotation currentAnnotation, List<String> domanRep,
                                       List<Integer> interpretedColumns) {
        for (int c : interpretedColumns) {
            List<TColumnHeaderAnnotation> headers = new ArrayList<>(
                    Arrays.asList(currentAnnotation.getHeaderAnnotation(c)));

            for (TColumnHeaderAnnotation ha : headers) {
                double dc = clazzScorer.computeDC(ha, domanRep);
                ha.setFinalScore(ha.getFinalScore() + dc);
            }

            Collections.sort(headers);
            currentAnnotation.setHeaderAnnotation(c, headers.toArray(new TColumnHeaderAnnotation[0]));
        }
    }

    private void reviseColumnAndCellAnnotations(
            Set<String> allEntityIds,
            Table table,
            TAnnotation currentAnnotation,
            List<Integer> interpretedColumns) throws KBSearchException {
        //now revise annotations on each of the interpreted columns
        for (int c : interpretedColumns) {
            //sample ranking
            List<List<Integer>> ranking = selector.select(table, c, currentAnnotation.getSubjectColumn());

            //get winning header annotation
            List<TColumnHeaderAnnotation> winningColumnClazzAnnotations =
                    currentAnnotation.getWinningHeaderAnnotations(c);
            Set<String> columnTypes = new HashSet<>();
            for (TColumnHeaderAnnotation ha : winningColumnClazzAnnotations)
                columnTypes.add(ha.getAnnotation().getId());

            List<Integer> updated = new ArrayList<>();
            for (int bi = 0; bi < ranking.size(); bi++) {
                List<Integer> rows = ranking.get(bi);
                TCell sample = table.getContentCell(rows.get(0), c);
                if (sample.getText().length() < 2) {
                    LOG.info("\t\t>>> short text cell skipped: " + rows + "," + c + " " + sample.getText());
                    continue;
                }

                //constrained disambiguation
                List<Pair<Entity, Map<String, Double>>>
                        entity_and_scoreMap =
                        disambiguate(allEntityIds,
                                sample,
                                table,
                                columnTypes,
                                rows, c);

                if (entity_and_scoreMap.size() > 0) {
                    TMPInterpreter.addCellAnnotation(table, currentAnnotation, rows, c,
                            entity_and_scoreMap);
                    updated.addAll(rows);
                }
            }

            TMPInterpreter.updateColumnClazz(updated, c, currentAnnotation, table, clazzScorer);
            LOG.info("\t>> update iteration complete (" + updated.size() + " rows)");
        }

    }


    private List<Pair<Entity, Map<String, Double>>> disambiguate(Set<String> ignoreEntityIds,
                                                                 TCell tcc,
                                                                 Table table,
                                                                 Set<String> constrainedClazz,
                                                                 List<Integer> rowBlock,
                                                                 int table_cell_col) throws KBSearchException {
        List<Pair<Entity, Map<String, Double>>> entity_and_scoreMap;
        List<Entity> candidates = kbSearch.findEntityCandidatesOfTypes(tcc.getText(),
                constrainedClazz.toArray(new String[0]));

        int ignore = 0;
        for (Resource ec : candidates) {
            if (ignoreEntityIds.contains(ec.getId()))
                ignore++;
        }
        if (candidates != null && candidates.size() != 0) {
            } else {
            candidates = kbSearch.findEntityCandidatesOfTypes(tcc.getText());
        }
        LOG.info("(Total candidates="+candidates.size()+", previously already processed=" + ignore+")");
        //now each candidate is given scores
        entity_and_scoreMap =
                disambiguator.constrainedDisambiguate
                        (candidates, table, rowBlock, table_cell_col, false);

        return entity_and_scoreMap;
    }


    private boolean checkConvergence(TAnnotation prev_iteration_annotation, TAnnotation table_annotation, int totalRows, List<Integer> interpreted_columns) {
        //check header annotations
        int header_converged_count = 0;
        boolean header_converged = false;
        for (int c : interpreted_columns) {
            List<TColumnHeaderAnnotation> header_annotations_prev_iteration = prev_iteration_annotation.getWinningHeaderAnnotations(c);
            List<TColumnHeaderAnnotation> header_annotations_current_iteration = table_annotation.getWinningHeaderAnnotations(c);
            if (header_annotations_current_iteration.size() == header_annotations_prev_iteration.size()) {
                header_annotations_current_iteration.retainAll(header_annotations_prev_iteration);
                if (header_annotations_current_iteration.size() == header_annotations_prev_iteration.size())
                    header_converged_count++;
                else
                    return false;
            } else
                return false;
        }
        if (header_converged_count == interpreted_columns.size()) {
            header_converged = true;
        }

        //check cell annotations
        boolean cell_converged = true;
        for (int c : interpreted_columns) {
            for (int row = 0; row < totalRows; row++) {
                List<TCellAnnotation> cell_prev_annotations = prev_iteration_annotation.getWinningContentCellAnnotation(row, c);
                List<TCellAnnotation> cell_current_annotations = table_annotation.getWinningContentCellAnnotation(row, c);
                if (cell_current_annotations.size() == cell_prev_annotations.size()) {
                    cell_current_annotations.retainAll(cell_prev_annotations);
                    if (cell_current_annotations.size() != cell_prev_annotations.size())
                        return false;
                }
            }
        }
        return header_converged && cell_converged;
    }

}