package org.hyperledger.fabric.samples.assettransfer;

public class InvalidOrderException extends Throwable {
    public InvalidOrderException(String errorMessage) {
        super(errorMessage);
    }
}
