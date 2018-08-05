package no.maddin.noobchain;


import lombok.Getter;

import java.util.List;

import java.security.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction {

    @Getter
    private final String transactionId; // this is also the hash of the transaction.

    @Getter
    private final PublicKey sender; // senders address/public key.

    @Getter
    private final PublicKey recipient; // Recipients address/public key.

    @Getter
    private final float value;

    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    @Getter
    private final List<TransactionInput> inputs;

    @Getter
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static AtomicLong sequencer = new AtomicLong(); // a rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value,  List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs != null ? inputs : java.util.Collections.emptyList();
        this.transactionId = calculateHash(sequencer.incrementAndGet(), sender, recipient, value);
    }

    /**
     * This Calculates the transaction hash (which will be used as its Id)
     */
    protected String calculateHash(long sequence, PublicKey sender, PublicKey recipient, float value) {
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value) + sequence
        );
    }

    /**
     * Signs all the data we don't wish to be tampered with.
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    /**
     * Verifies the data we signed hasn't been tampered with
     */
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value)	;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * Returns true if new transaction could be created.
     */
    public boolean processTransaction() {

        if(!verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.setUTXO(NoobChain.UTXOs.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if(getInputsValue() < NoobChain.MINIMUM_TRANSACTION) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        outputs.add(new TransactionOutput( this.recipient, value,transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.getId() , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.getUTXO() != null) {
                NoobChain.UTXOs.remove(i.getUTXO().getId());
            }
        }

        return true;
    }

    /**
     * @return sum of inputs(UTXOs) values
     */
    public float getInputsValue() {
        float total = 0f;
        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
            total += i.getUTXO().getValue();
        }
        return total;
    }

    /**
     * @return sum of outputs
     */
    public float getOutputsValue() {
        float total = 0f;
        for(TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }

}
