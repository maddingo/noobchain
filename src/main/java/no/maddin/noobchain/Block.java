package no.maddin.noobchain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;

public class Block {

    @Getter
    private String hash;

    @Getter
    private String previousHash;
    private String merkleRoot;

    @Getter
    private final List<Transaction> transactions = new ArrayList<>();

    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    /**
     * Calculate new hash based on blocks contents
     */
    public String calculateHash() {
        return StringUtil.applySha256(previousHash, timeStamp, nonce, merkleRoot);
    }

    /**
     * Increases nonce value until hash target is reached.
     */
    public void mineBlock(int difficulty) {
        this.merkleRoot = StringUtil.getMerkleRoot(this.transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    /**
     * Add transactions to this block
     */
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) {
            return false;
        }
        if(!previousHash.equals("0")) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
