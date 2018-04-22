package no.maddin.noobchain;

import java.util.*;

public class NoobChain {

    private final LinkedList<Block> blockchain = new LinkedList<>();
    private final int difficulty;

    public NoobChain(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isValid() {

        if (blockchain.isEmpty()) {
            throw new IllegalArgumentException("Blockchain is empty");
        }

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        Iterator<Block> blockIterator = blockchain.iterator();
        Block previousBlock = blockIterator.next();
        while (blockIterator.hasNext()) {
            Block currentBlock = blockIterator.next();
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
            previousBlock = currentBlock;
        }
        return true;
    }

    public void addBlock(String data) {
        Block newBlock = new Block(data, blockchain.getLast().hash);
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public void addGenesisBlock(String data) {
        if (blockchain.isEmpty()) {
            blockchain.add(new Block(data, "0"));
        }
    }
}
