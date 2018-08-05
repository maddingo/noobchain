package no.maddin.noobchain;

import lombok.Getter;
import lombok.Setter;

public class TransactionInput {

    @Getter
    private final String transactionOutputId; //Reference to TransactionOutputs -> transactionId

    @Getter
    @Setter
    private TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
