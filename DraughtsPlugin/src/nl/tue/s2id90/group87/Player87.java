package nl.tue.s2id90.group87;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

public class Player87 extends DraughtsPlayer {

    private int bestValue = 0;
    private int maxSearchDepth;
    private boolean stopped;

    public Player87(int maxSearchDepth) {
        super("thor.png");
        this.maxSearchDepth = maxSearchDepth;
    }

    @Override
    public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s); // the root of the search tree

        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);

            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove = node.getBestMove();

            // print the results for debugging reasons
            System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(), maxSearchDepth, bestMove, bestValue
            );
        } catch (AIStoppedException ex) {
            /* nothing to do */
        }

        if (bestMove == null) {
            System.err.println("no valid move found!");
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

        // Just count pieces
        for (int piece : pieces) {
            switch (piece) {
                case DraughtsState.WHITEPIECE:
                    value = value + 1;
                    break;
                case DraughtsState.WHITEKING:
                    value = value + 3;
                    break;
                case DraughtsState.BLACKPIECE:
                    value = value - 1;
                    break;
                case DraughtsState.BLACKKING:
                    value = value - 3;
                    break;
            }
        }

        return value;
    }
}