package no.maddin.noobchain;

import lombok.Getter;

import java.security.PublicKey;

public class TransactionOutput {

    @Getter
    private final String id;

    @Getter
    private final PublicKey recipient; //also known as the new owner of these coins.

    @Getter
    private final float value; //the amount of coins they own

    private final String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient)+Float.toString(value)+parentTransactionId);
    }

    /**
     * Check if coin belongs to you
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
