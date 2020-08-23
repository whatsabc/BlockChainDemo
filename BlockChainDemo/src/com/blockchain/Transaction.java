package com.blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    public String transactionId; //交易hash
    //public String transactionHash; //交易hash
    public float value;  //交易额
    //public byte[] signature; //签名数据
    public long timeStamp; //时间戳
    public String WalletIDA;//交易的A账户
    public String WalletIDB;//交易的A账户
    public ArrayList<TransactionInput> inputs; //交易输入
    public ArrayList<TransactionOutput> outputs=new ArrayList<>(); //交易输出

    private static int sequence = 0; //A rough count of how many transactions have been generated
    // Constructor:
    public Transaction(String A,String B,float value,ArrayList<TransactionInput> inputs) {
        this.value = value;
        this.inputs = inputs;
        this.WalletIDA=A;
        this.WalletIDB=B;
        this.timeStamp=new Date().getTime();
    }

    public boolean processTransaction() {
        //Gathers transaction inputs (Making sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }

        //检查交易数额是否低于最小交易额
        if(getInputsValue() < BlockChain.minimumTransaction) {
            System.out.println("交易额太小： " + getInputsValue());
            return false;
        }

        //Generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(WalletIDB,value,transactionId)); //被转走的coins，绑定在接收者钱包上
        outputs.add(new TransactionOutput(WalletIDA,leftOver,transactionId)); //剩余的coins，绑定在发送者钱包上

        //Add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            BlockChain.UTXOs.put(o.id , o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            BlockChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(WalletIDA + WalletIDB + Float.toString(value) + sequence);
    }
}
