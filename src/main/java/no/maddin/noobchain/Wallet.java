package no.maddin.noobchain;


import lombok.Getter;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    @Getter
    private final PrivateKey privateKey;

    @Getter
    private final PublicKey publicKey;

    public Wallet() {
        Object[] keys = generateKeyPair();
        this.privateKey = (PrivateKey) keys[0];
        this.publicKey = (PublicKey) keys[1];
    }

    private static Key[] generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            return new Key[] {keyPair.getPrivate(), keyPair.getPublic()};
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
