package uk.ac.shef.dcs.sti.io;

import uk.ac.shef.dcs.sti.core.model.TCellAnnotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SWC18OutputCellAnnotation {
    private int columnIndex;
    private int rowIndex;
    private String cellText;
    private List<String[]> candidates;

    public SWC18OutputCellAnnotation(int rowIndex, int columnIndex, String cellText){
        candidates=new ArrayList<>();
        this.rowIndex=rowIndex;
        this.columnIndex=columnIndex;
        this.cellText=cellText;
    }

    public void add(TCellAnnotation candidate){
        String[] values = new String[3];

        values[0] = candidate.getAnnotation().getId();
        values[1] = candidate.getAnnotation().getLabel();
        values[2] = String.valueOf(candidate.getFinalScore());

        candidates.add(values);
    }

    public String get(){
        return "Not yet implemented";
    }
}
