package com.balloonoffice.balloonapp.Model;

/**
 * Created by bluenightz on 2/4/16 AD.
 */
public class Csv_item_model {
    private String storeCode;
    private String productCode;
    private String stockBefore;
    private String stockIn;
    private String stockOut;
    private String stockAfter;

    /** json model
     *
     *
    [{
        "storeCode":"",
                "productCode":"",
                "stockBefore":"",
                "stockIn":"",
                "stockOut":"",
                "stockAfter":""
    }]

     */

    public Csv_item_model(){

    }

    public String getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(String stockAfter) {
        this.stockAfter = stockAfter;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(String stockBefore) {
        this.stockBefore = stockBefore;
    }

    public String getStockIn() {
        return stockIn;
    }

    public void setStockIn(String stockIn) {
        this.stockIn = stockIn;
    }

    public String getStockOut() {
        return stockOut;
    }

    public void setStockOut(String stockOut) {
        this.stockOut = stockOut;
    }

}
