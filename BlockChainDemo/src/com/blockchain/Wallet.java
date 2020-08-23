package com.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public String WalletID;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public Wallet(String wID) {
        WalletID=wID;
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: BlockChain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.WalletIDB.equals(this.WalletID)){
                UTXOs.put(UTXO.id,UTXO); //将其添加到我们的未用交易列表中
                total += UTXO.value;
            }
        }
        return total;
    }

    //value是转账钱数
    public Transaction sendFunds(Wallet B,float value) {
        if(getBalance() < value) {
            System.out.println("#余额不足. 交易终止.");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(this.WalletID,B.WalletID,value,inputs);//this是发送者，B是接收者

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}
