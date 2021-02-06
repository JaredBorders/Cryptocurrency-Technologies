import java.util.*;

public class TxHandler {

    public UTXOPool ledger;

    /**
     * Creates a public ledger whose current UTXOPool
     * (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using
     * the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.ledger = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        ArrayList<Transaction.Input> inputs = tx.getInputs();
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        UTXOPool pool = new UTXOPool();                         // Temporary UTXOPool used to track seen tx's

        /* (4) all of {@code tx}s output values are non-negative */
        double diff = 0;
        for (Transaction.Output output : outputs) {
            if (output.value < 0) return false;
            diff += output.value;
        }

        for (int i = 0; i < inputs.size(); i++) {

            /* Create UTXO for each input in tx */
            UTXO utxo = new UTXO(inputs.get(i).prevTxHash, inputs.get(i).outputIndex);

            /* (1) all outputs claimed by {@code tx} are in the current UTXO pool */
            if (!ledger.contains(utxo)) return false;

            /* (2) the signatures on each input of {@code tx} are valid */
            if (!Crypto.verifySignature(
                ledger.getTxOutput(utxo).address,               // Public Key from this tx's input's output
                tx.getRawDataToSign(i),                         // Message
                inputs.get(i).signature                         // Signature for this tx's input
            )) { return false; }

            /* (3) no UTXO is claimed multiple times by {@code tx}d */
            if (pool.contains(utxo)) return false;

            pool.addUTXO(utxo, ledger.getTxOutput(utxo));       // Add unseen utxo & tx.output to temporary UTXOPool

            /* (5.1) subtract each input.value from the sum of output.value(s) */
            diff -= ledger.getTxOutput(utxo).value;

        }

        /* (5.2) if after subtracting all input.value(s) from output.value(s), diff is positive, then output > input */
        return diff < 0;

    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        List<Transaction> acceptedTxs = new ArrayList<>();

        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                /* Remove each input in this tx from ledger of unspent transaction outputs */
                for (Transaction.Input input : tx.getInputs()) {
                    ledger.removeUTXO(new UTXO(input.prevTxHash, input.outputIndex));
                }

                /* Add each output in this tx to ledger of unspent transaction outputs */
                for (int i = 0; i < tx.numOutputs(); i++) {
                    ledger.addUTXO(new UTXO(tx.getHash(), i), tx.getOutput(i));
                }

                acceptedTxs.add(tx);
            }
        }

        return acceptedTxs.toArray(new Transaction[0]);
    }

}
