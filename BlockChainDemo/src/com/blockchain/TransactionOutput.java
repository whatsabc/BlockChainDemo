package com.blockchain;


public class TransactionOutput {
    public String id;
    public float value; //the amount of coins they own
    public String parentTransactionId; //the id of the transaction this output was created in
    public String WalletIDB;

    //Constructor
    public TransactionOutput(String WalletIDB,float value, String parentTransactionId) {
        this.WalletIDB=WalletIDB;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(WalletIDB+value+parentTransactionId);
    }
}
