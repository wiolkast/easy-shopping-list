package pl.mobileturtle.easyshoppinglist.data;

public class ShoppingListData {
    private final int id;
    private final String productName;
    private final int type;

    public ShoppingListData(int id, String productName, int type){
        this.id = id;
        this.productName = productName;
        this.type = type;
    }

    public int getId(){
        return this.id;
    }

    public String getProductName(){
        return this.productName;
    }

    public int getType(){
        return this.type;
    }
}
