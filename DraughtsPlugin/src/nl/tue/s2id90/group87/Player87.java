package nl.tue.s2id90.group87;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

public class Player87 extends DraughtsPlayer {
    
    private static final int MAX_SEARCH_DEPTH = 6;
    
    private static final int WEIGHT_PIECE = 1;
    private static final int WEIGHT_KING = 5;
    
    private static final int WEIGHT_COUNT = 10;
    private static final int WEIGHT_CENTER = 2;
    private static final int WEIGHT_FORMATION = 4;
    private static final int WEIGHT_TEMPI = 2;
    
    private static final boolean DEBUG = false;

    private int bestValue = 0;
    private boolean stopped;

    public Player87() {
        super("thor.png");
    }

    @Override
    public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s); // the root of the search tree

        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, MAX_SEARCH_DEPTH);

            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove = node.getBestMove();
            
            // print the results for debugging reasons
            if (DEBUG) {
                System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(), MAX_SEARCH_DEPTH, bestMove, bestValue
                );
            }
        } catch (AIStoppedException ex) {
            /* nothing to do */
        }

        if (bestMove == null) {
            if (DEBUG) {
                System.err.println("no valid move found!");
            }
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    }

    /**
     * This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to
     * getMove(s).
     */
    @Override
    public Integer getValue() {
        return bestValue;
    }

    /**
     * Tries to make alphabeta search stop. Search should be implemented such
     * that it throws an AIStoppedException when boolean stopped is set to true;
     *
     */
    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * Returns random valid move in state s, or null if no moves exist.
     */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Implementation of alphabeta that automatically chooses the white player
     * as maximizing player and the black player as minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     *
     */
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth) throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        
        if (depth == 0) {
            return evaluate(node.getState());
        }

        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth);
        } else {
            return alphaBetaMin(node, alpha, beta, depth);
        }
    }

    /**
     * Does an alphabeta computation with the given alpha and beta where the
     * player that is to move in node is the minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been
     * set to true.
     */
    int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth) throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }

        DraughtsState state = node.getState();
        List<Move> moves = state.getMoves();
        Move bestMove = null;
        int value = MAX_VALUE;

        for (Move move : moves) {
            state.doMove(move);
            value = Math.min(value, alphaBeta(new DraughtsNode(state), alpha, beta, depth - 1));
            state.undoMove(move);

            if (value < beta) {
                beta = value;
                bestMove = move;
            }

            if (alpha >= beta) {
                break;
            }
        }

        node.setBestMove(bestMove);
        return value;
    }

    /**
     * Does an alphabeta computation with the given alpha and beta where the
     * player that is to move in node is the maximizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been
     * set to true.
     */
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth) throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }

        DraughtsState state = node.getState();
        List<Move> moves = state.getMoves();
        Move bestMove = null;
        int value = MIN_VALUE;

        for (Move move : moves) {
            state.doMove(move);
            value = Math.max(value, alphaBeta(new DraughtsNode(state), alpha, beta, depth - 1));
            state.undoMove(move);

            if (value > alpha) {
                alpha = value;
                bestMove = move;
            }

            if (alpha >= beta) {
                break;
            }
        }

        node.setBestMove(bestMove);
        return value;
    }

    /**
     * A method that evaluates the given state.
     */
    int evaluate(DraughtsState state) {
        int[] pieces = state.getPieces();
        int value = 0;
        int valueCount = 0;
        int valueCenter = 0;
        int valueFormation = 0;
        int valueTempi = 0;
        
        // Count pieces
        for (int i = 1; i <= 50; i++) {
            switch (pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    valueCount = valueCount + WEIGHT_PIECE;
                    break;
                case DraughtsState.WHITEKING:
                    valueCount = valueCount + WEIGHT_KING;
                    break;
                case DraughtsState.BLACKPIECE:
                    valueCount = valueCount - WEIGHT_PIECE;
                    break;
                case DraughtsState.BLACKKING:
                    valueCount = valueCount - WEIGHT_KING;
                    break;
            }
        }
        
        // Prefer center
        // Kings are not evaluated because they control more squares
        for (int i = 1; i <= 50; i++) {
            switch (pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    if (i == 5 || i == 15 || i == 25 || i == 35 || i == 45
                        || i == 6 || i == 16 || i == 26 || i == 36 || i == 46) {
                        valueCenter = valueCenter - 1;
                    }
                    else {
                        valueCenter = valueCenter + 1;
                    }
                    break;
                case DraughtsState.BLACKPIECE:
                    if (i == 5 || i == 15 || i == 25 || i == 35 || i == 45
                        || i == 6 || i == 16 || i == 26 || i == 36 || i == 46) {
                        valueCenter = valueCenter + 1;
                    }
                    else {
                        valueCenter = valueCenter - 1;
                    }
                    break;
            }
        }
        
        // Make formations by clustering
        for (int i = 1; i <= 50; i++) {
            int row = (int) Math.ceil(i / 5.0);
            if (! (i == 5 || i == 15 || i == 25 || i == 35 || i == 45
                || i == 6 || i == 16 || i == 26 || i == 36 || i == 46)) {
                if (row == 2 || row == 4 || row == 6 || row == 8) {
                    if (pieces[i] == DraughtsState.WHITEPIECE) {
                        if (pieces[i-6] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i-5] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i+4] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i+5] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                    }
                    else if (pieces[i] == DraughtsState.BLACKPIECE) {
                        if (pieces[i-6] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i-5] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i+4] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i+5] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                    }
                }
                else if (row == 3 || row == 5 || row == 7 || row == 9) {
                    if (pieces[i] == DraughtsState.WHITEPIECE) {
                        if (pieces[i-7] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i-6] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i+5] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                        if (pieces[i+6] == DraughtsState.WHITEPIECE) {
                            valueFormation = valueFormation + 1;
                        }
                    }
                    else if (pieces[i] == DraughtsState.BLACKPIECE) {
                        if (pieces[i-7] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i-6] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i+5] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                        if (pieces[i+6] == DraughtsState.BLACKPIECE) {
                            valueFormation = valueFormation - 1;
                        }
                    }
                }
            }
        }
        
        // Try to move pieces more forward to get kings
        // Kings are not evaluated because they already are kings
        for (int i = 1; i <= 50; i++) {
            switch (pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    valueTempi = valueTempi + (11 - ((int) Math.ceil(i / 5.0)));
                    break;
                case DraughtsState.BLACKPIECE:
                    valueTempi = valueTempi - ((int) Math.ceil(i / 5.0));
                    break;
            }
        }
        
        // Add all subcounts together
        value = WEIGHT_COUNT * valueCount + WEIGHT_CENTER *  valueCenter + WEIGHT_FORMATION * valueFormation + WEIGHT_TEMPI * valueTempi;
        
        if (DEBUG) {
            System.err.format(
                "Count: %d, Center: %d, Formation: %d, Tempi: %d\n",
                WEIGHT_COUNT * valueCount, WEIGHT_CENTER *  valueCenter, WEIGHT_FORMATION * valueFormation, WEIGHT_TEMPI * valueTempi
            );
        }
        
        return value;
    }
}