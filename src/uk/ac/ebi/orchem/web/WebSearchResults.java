package uk.ac.ebi.orchem.web;

import java.util.List;

/**
 * Bean to store the results of a compound search in the database.
 * 
 */
public class WebSearchResults {
    private String structureSearchMethod;
    private String minTanCoeff;
    private String topN;
    private String structure;
    private List searchResults;
    private String debugMessage;
    private String smiles;
    private String smilesOrMol;

    private int pageNum;

    private final int RESULT_PAGE_SIZE=9;


    public void setStructureSearchMethod(String structureSearchMethod) {
        this.structureSearchMethod = structureSearchMethod;
    }

    public String getStructureSearchMethod() {
        return structureSearchMethod;
    }

    public void setMinTanCoeff(String minTanCoeff) {
        this.minTanCoeff = minTanCoeff;
    }

    public String getMinTanCoeff() {
        return minTanCoeff;
    }

    public void setTopN(String topN) {
        this.topN = topN;
    }

    public String getTopN() {
        return topN;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getStructure() {
        return structure;
    }

    public void setSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    public List getSearchResults() {
        return searchResults;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getNextPageNum() {
        return pageNum+1;
    }

    public int getPrevPageNum() {
        return pageNum-1;
    }

    public int getCurrDisplayStartIdx() {
        return ((pageNum-1)*RESULT_PAGE_SIZE)+1;
    }

    public int getCurrDisplayEndIdx() {
        return ((pageNum)*RESULT_PAGE_SIZE);
    }

    public boolean isLastPage() {
        if (pageNum*RESULT_PAGE_SIZE < searchResults.size())
            return false;
        else
            return true;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmilesOrMol(String smileOrMol) {
        this.smilesOrMol = smileOrMol;
    }

    public String getSmilesOrMol() {
        return smilesOrMol;
    }
}
