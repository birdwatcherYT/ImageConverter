package img;
public class MyColor {    
    public static int A(int color){
        return color>>24;
    }
    public static int R(int color){
        return color>>16&0xff;
    }
    public static int G(int color){
        return color>>8&0xff;
    }
    public static int B(int color){
        return color&0xff;
    }
    public static int RGB(int r,int g,int b){
         return 0xff000000 | r <<16 | g <<8 | b;
    }
    public static int ARGB(int a,int r,int g,int b){
         return a<<24 | r <<16 | g <<8 | b;
    }
}
