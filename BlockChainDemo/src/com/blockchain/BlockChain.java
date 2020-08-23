package com.blockchain;

import java.util.*;

public class BlockChain {
    //这里暂时只用一个List就OK
    public static ArrayList<Block> blockchain=new ArrayList<>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();//交易信息
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;//最小交易额
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String args[]){

        walletA = new Wallet("A");//我们将RSA公私钥用ID代替
        walletB = new Wallet("B");
        Wallet coinbase=new Wallet("coinbase");

        System.out.println("创建创世交易，第一笔coin将会奖励给WalletA");
        genesisTransaction = new Transaction(coinbase.WalletID,walletA.WalletID,100f, null);//创世区块的钱转给A
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(walletA.WalletID,genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //在UTXO列表存放第一笔交易

        System.out.println("创建并开始挖创世区块...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //模拟挖矿
        Block block1 = new Block(genesis.hash);
        addBlock(block1);
        Block block2 = new Block(block1.hash);
        addBlock(block2);
        Block block3 = new Block(block2.hash);
        addBlock(block3);
        Block block4 = new Block(block3.hash);
        addBlock(block4);

        //模拟转账
        System.out.println("\nWalletA的余额: " + walletA.getBalance());
        System.out.println("\nWalletA试图在区块1上转账40coins给 WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB,40f));
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        System.out.println("\nWalletA试图在区块2上转账200coins给WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB,200f));
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        System.out.println("\nWalletB试图在区块3上转账20coins给WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA,20f));
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        System.out.println("\nWalletA试图在区块4上转账11coins给WalletB...");
        block4.addTransaction(walletA.sendFunds(walletB,11f));
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());


        int i=0;
        for(Block b:blockchain){
            System.out.println("                           [---[---[区块" + i + "]---]---]");
            i++;
            System.out.println("[摘要]");
            System.out.println("  └---当前节点hash："+b.hash);
            System.out.println("  └---上一个节点hash"+b.preHash);
            System.out.println("  └---merkleRoot"+b.merkleRoot);
            System.out.println("  └---时间戳："+b.timeStamp);
            System.out.println("  └---nonce："+b.nonce);

            System.out.println("\n[交易]");
            for(Transaction t:b.transactions){
                System.out.println("  └---交易id："+t.transactionId);
                System.out.println("  └---交易发起方："+t.WalletIDA);
                System.out.println("  └---交易接收方："+t.WalletIDB);
                System.out.println("  └---交易额："+t.value);
                System.out.println("  └---交易时间戳："+t.timeStamp);
                System.out.println("  └---[UTX0]");
                System.out.println("        └---[输入列表]");
                if(t.inputs!=null){
                    for(TransactionInput in:t.inputs){
                        System.out.println("                -----------------------------------------------------------------------------");
                        System.out.println("                └---上笔输出id："+in.transactionOutputId);
                        System.out.println("                └---输入id："+in.UTXO.id);
                        System.out.println("                └---输入接收方："+in.UTXO.WalletIDB);
                        System.out.println("                └---父交易号id："+in.UTXO.parentTransactionId);
                        System.out.println("                └---输入数额（金额）："+in.UTXO.value);
                    }
                }
                System.out.println("        └---[输出列表]");
                if(t.outputs!=null){
                    for(TransactionOutput out:t.outputs){
                        System.out.println("                -----------------------------------------------------------------------------");
                        System.out.println("                └---输出id："+out.id);
                        System.out.println("                └---输出接收方："+out.WalletIDB);
                        System.out.println("                └---父交易号id："+out.parentTransactionId);
                        System.out.println("                └---输出数额（金额）："+out.value);
                    }
                }
            }
            System.out.println("├-------------------------------------------------------------------------------------------------┤");
        }


    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
