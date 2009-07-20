package uk.ac.ebi.orchem.isomorphism;

/*
 *  $Author$
 *  $Date$
 *  $$
 *
 *  Copyright (C) 2009  Mark Rijnbeek, EMBL EBI - OrChem project
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
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to facilitate the {@link uk.ac.ebi.orchem.isomorphism.VF2State#addPair } and
 * {@link uk.ac.ebi.orchem.isomorphism.VF2State#undoAddPair }operations.<br>
 * This prevents us from having to (expensively) clone a VF2 state into
 * each recursion leven. By working with the same VF2 state and doing/undoing
 * addPair operations, performance is significatnly improved.
 * <BR>
 * @author: markr@ebi.ac.uk
 */
class VF2UndoBean {

    boolean undo_in_query_at_queryNodeIdx = false;
    int undo_query_in_len = 0;
    int undo_query_both_len = 0;

    boolean undo_out_query_at_queryNodeIdx = false;
    int undo_query_out_len = 0;

    boolean undo_in_target_at_targetNodeIdx = false;
    int undo_targ_in_len = 0;
    int undo_targ_both_len = 0;

    boolean undo_out_target_at_targetNodeIdx = false;
    int undo_targ_out_len = 0;

    Integer undo_core_query_at_queryNodeIdx = null;
    Integer undo_core_target_at_targetNodeIdx = null;

    List<Integer> in_query_to_zero = new ArrayList<Integer>();
    List<Integer> out_query_to_zero = new ArrayList<Integer>();
    List<Integer> in_target_to_zero = new ArrayList<Integer>();
    List<Integer> out_target_to_zero = new ArrayList<Integer>();
}
