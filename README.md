# Bitcoin and Cryptocurrency Technologies
Princeton University's course on the conceptual foundations needed to engineer secure software that interacts with the Bitcoin network.

# Goal
Learn how to use Bitcoin and other cryptocurrency networks to improve my own personal and professional applications.

# Projects:

### Scrooge Coin
Design/Implement a file called TxHandler.java that implements the following API:
* handleTxs() should return a mutually valid transaction set of maximal size (one that can’t be enlarged simply by adding more transactions)
* It need not compute a set of maximum size (one for which there is no larger mutually valid transaction set)
* Based on the transactions it has chosen to accept, handleTxs() should also update its internal UTXOPool to reflect the current set of unspent transaction outputs, so that future calls to handleTxs() and isValidTx() are able to correctly process/validate transactions that claim outputs from transactions that were accepted in a previous call to handleTxs()

### Consensus from Trust
Design/Implement a distributed consensus algorithm given a graph of “trust” relationships between nodes. This is an alternative method of resisting sybil attacks and achieving consensus; it has the benefit of not “wasting” electricity like proof-of-work does.

### Blockchain
Design/Implement a node that’s part of a block-chain-based distributed consensus protocol.
