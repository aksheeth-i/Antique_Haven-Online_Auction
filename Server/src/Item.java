
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Item implements Serializable {
    int itemNum;
    String name;
    String description;
    String iconPic;
    String itemPic;
    int startingBid;
    int highBid;
    int currBid;
    String currOwner;
    ArrayList<String> history;
    boolean sold;

    public Item(int x, String n, String d, int s, int hb,String icon,String item, int currBid)
    {
        itemNum=x;
        name=n;
        description=d;
        startingBid=s;
        highBid=hb;
        this.currBid=currBid;
        iconPic=icon;
        itemPic=item;
        currOwner = "";
        history = new ArrayList<>();
        sold=false;

    }
}
