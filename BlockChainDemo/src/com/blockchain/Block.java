package com.blockchain;


import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Block {
    public String hash;
    public String preHash;
    public String merkleRoot;
    public long timeStamp; //时间戳
    public int nonce;
    public ArrayList<Transaction> transactions=new ArrayList<>(); //交易列表

    public Block(String preHash){
        this.preHash=preHash;
        this.timeStamp=new Date().getTime();
        this.hash=calculateHash();
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        return StringUtil.applySha256(preHash + timeStamp + nonce + merkleRoot);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        long start = System.currentTimeMillis();
        String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        long end = System.currentTimeMillis();
        System.out.println("[-[该区块挖矿成功]--]: " + hash + "    难度系数：" + difficulty + " 用时：" + (end - start) + "ms");
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) return false;
        if ((!Objects.equals(preHash, "0"))) {
            if ((!transaction.processTransaction())) {
                System.out.println("#交易失败");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("交易成功，交易信息加入到区块交易列表");
        return true;
    }
}
