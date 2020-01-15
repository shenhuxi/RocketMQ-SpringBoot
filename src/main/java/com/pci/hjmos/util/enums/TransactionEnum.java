package com.pci.hjmos.enums;

public enum TransactionEnum {
    YES(1, "是"),
    NO(0, "否");

    private int value;

    public int getValue() {
        return this.value;
    }

    private String des;

    public String getDes() {
        return this.des;
    }


    TransactionEnum(int value, String des) {
        this.value = value;
        this.des = des;
    }
}
