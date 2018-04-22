package no.maddin.noobchain;

import org.junit.Test;

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
}
