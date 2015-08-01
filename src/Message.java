import com.sun.org.apache.xpath.internal.SourceTree;

public class Message {

    private int typ;
    private String to, from, content;

    public static final int TYP_LOGIN  = 0;
    public static final int TYP_LIST   = 1;
    public static final int TYP_TO_ALL = 2;
    public static final int TYP_TO_ONE = 3;

    public Message(String s){

        String[] teile = s.split(":");
        if (teile.length<4)
            throw new MessageException("zu wenig Teile "+teile.length);

        try{
            typ = Integer.parseInt(teile[0]);
        }catch (Exception ex){
            throw new MessageException("Typ keine Zahl "+teile[0], ex);
        }

        to = teile[1];
        from = teile[2];
        content = teile[3];
        for (int i=4; i<teile.length; i++)
            content += ":"+teile[i];

    }

    public int getTyp() {
        return typ;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }
}
