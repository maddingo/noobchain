package no.maddin.noobchain;

import lombok.Getter;

import java.security.PublicKey;
import java.util.*;

public class NoobChain {

    public static final float MINIMUM_TRANSACTION = 0.1f;

    @Getter
    private final LinkedList<Block> blockchain = new LinkedList<>();

    private final int difficulty;

    /**
     * ugly way of avoiding to calculate all balances of the wallets from the transactions.
     */
    public static final HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); //list of all unspent transactions.

    public NoobChain(int difficulty) {
        this.difficulty = difficulty;
    }

    @Deprecated
    public boolean isValidNoTransactions() {

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
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
            previousBlock = currentBlock;
        }
        return true;
    }

    public boolean isChainValid() {
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        Transaction genesisTransaction = blockchain.getFirst().getTransactions().get(0);
        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        //loop through blockchain to check hashes
        ListIterator<Block> blockchainIterator = blockchain.listIterator(0);
        Block previousBlock = blockchainIterator.hasNext() ? blockchainIterator.next() : null;
        while (blockchainIterator.hasNext()) {

            Block currentBlock = blockchainIterator.next();
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            int transactionCount = 0;
            for(Transaction currentTransaction : currentBlock.getTransactions()) {

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + transactionCount + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + transactionCount + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputs()) {
                    TransactionOutput tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + transactionCount + ") is Missing");
                        return false;
                    }

                    if(input.getUTXO().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + transactionCount + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for(TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if( currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + transactionCount + ") output recipient is not who it should be");
                    return false;
                }
                if( currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + transactionCount + ") output 'change' is not sender.");
                    return false;
                }

                transactionCount++;
                previousBlock = currentBlock;
            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }


    public void addBlock(Block block) {
        block.mineBlock(difficulty);
        blockchain.add(block);
    }

    @Deprecated
    public void addBlockString(String data) {
        Block newBlock = new Block(blockchain.getLast().getHash());
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    @Deprecated
    public void addGenesisBlock(String data) {
        if (blockchain.isEmpty()) {
            blockchain.add(new Block("0"));
        } else {
            throw new IllegalStateException("Block Chain should be empty when creating the genesis block");
        }
    }

    public void createGenesisBlock(PublicKey initialReceiver, float initialTransactionValue) {
        Wallet coinbase = new Wallet(); // a wallet that nobody owns, source of the initial transaction
        //create genesis transaction, which sends 100 NoobCoin to the initial receiver
        Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), initialReceiver, initialTransactionValue, null) {
            @Override
            protected String calculateHash(long sequence, PublicKey sender, PublicKey recipient, float value) {
                return "0";
            }
        };
        genesisTransaction.generateSignature(coinbase.getPrivateKey());	 //manually sign the genesis transaction
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId())); //manually add the Transactions Output

        UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
    }
}
