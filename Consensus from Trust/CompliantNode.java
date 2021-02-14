import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/* CompliantNode refers to a node that follows the rules (not malicious) */
public class CompliantNode implements Node {

    private double p_graph;
    private double p_malicious;
    private double p_txDistribution;
    private int numRounds;

    private boolean[] followees, maliciousList;

    private Set<Transaction> pendingTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    /** {@code followees[i]} is true if and only if this node follows node {@code i} */
    public void setFollowees(boolean[] followees) {
        this.followees = followees;
        this.maliciousList = new boolean[followees.length];
    }

    /** initialize proposal list of transactions */
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    /**
     * @return proposals to send to my followers. REMEMBER: After final round, behavior of
     *         {@code getProposals} changes and it should return the transactions upon which
     *         consensus has been reached.
     */
    public Set<Transaction> sendToFollowers() {
        Set<Transaction> transToSend = new HashSet<>(this.pendingTransactions);
        this.pendingTransactions.clear();
        return transToSend;
    }

    /** receive candidates from other nodes. */
    public void receiveFromFollowees(Set<Candidate> candidates) {
        Set<Integer> senders = new HashSet<>();

        for (Candidate candidate : candidates) {
            senders.add(candidate.sender);
        }

        for (int i = 0; i < followees.length; i++) {
            if (followees[i] && !senders.contains(i)) { // if i is a followee but not a sender, flag i as malicious
                maliciousList[i] = true;
            }
        }

        for (Candidate candidate : candidates) {
            if (!maliciousList[candidate.sender]) { // if this sender has NOT been flagged as malicious, add tx to list of pendingTransactions
                this.pendingTransactions.add(candidate.tx);
            }
        }

        // Time complexity: O(3N) ~ O(N)

    }
}
