package no.maddin.noobchain;

import org.junit.Test;

import java.security.Security;

import static org.junit.Assert.assertTrue;

public class NoobChainTest {
    /**
     * <ol>
     * <li>Creating Your First Blockchain with Java. Part 1: https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
     * </li>
     * <li>Part 2: https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
     * </li>
     * </ol>
     */
    @Test
    public void verifyChain() {
        //add our blocks to the blockchain ArrayList:

        NoobChain chain = new NoobChain(2);
        chain.addGenesisBlock("Hi im the first block");

        chain.addBlockString("Yo im the second block");

        chain.addBlockString("Hey im the third block");

        assertTrue(chain.isValidNoTransactions());
    }

    @Test
    public void verifySignatures() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        transaction.generateSignature(walletA.getPrivateKey());
        assertTrue(transaction.verifiySignature());
    }

    @Test
    public void verifyTransactions() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncy castle as a Security Provider

        NoobChain chain = new NoobChain(2);

        //Create wallets:
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        chain.createGenesisBlock(walletA.getPublicKey(), 100f);
        Block genesisBlock = chain.getBlockchain().getFirst();

        //testing
        Block block1 = new Block(genesisBlock.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");

        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        chain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        chain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertTrue(chain.isChainValid());

    }


}
