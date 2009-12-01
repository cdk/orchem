/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *
 */
package uk.ac.ebi.orchem.web;

import java.util.List;

/**
 * Bean to store the results of a compound search in the database.
 * @author markr@ebi.ac.uk
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
    private String strictStereoYN;

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

    public void setStrictStereoYN(String strictStereoYN) {
        this.strictStereoYN = strictStereoYN;
    }

    public String getStrictStereoYN() {
        return strictStereoYN;
    }
}
