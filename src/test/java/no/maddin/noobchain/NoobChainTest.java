package no.maddin.noobchain;

import org.junit.Test;

import java.security.Security;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NoobChainTest {
   /**
    * Creating Your First Blockchain with Java. Part 1
    *
    * See https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
    */
   @Test
   public void verifyChain() {
      //add our blocks to the blockchain ArrayList:

      NoobChain chain = new NoobChain(2);
      chain.addGenesisBlock("Hi im the first block");

      chain.addBlock("Yo im the second block");

      chain.addBlock("Hey im the third block");

      assertThat(chain.isValid(), is(equalTo(true)));
   }

   @Test
   public void verifySignatures() {
       //Setup Bouncy castle as a Security Provider
       Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
       //Create the new wallets
       Wallet walletA = new Wallet();
       Wallet walletB = new Wallet();
       //Test public and private keys
//       System.out.println("Private and public keys:");
//       System.out.println(StringUtil.getStringFromKey(walletA.getPrivateKey()));
//       System.out.println(StringUtil.getStringFromKey(walletA.getPublicKey()));
       //Create a test transaction from WalletA to walletB
       Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
       transaction.generateSignature(walletA.getPrivateKey());
       //Verify the signature works and verify it from the public key
//       System.out.println("Is signature verified");
       assertThat(transaction.verifiySignature(), is(equalTo(true)));
//       System.out.println(transaction.verifiySignature());
   }
}
