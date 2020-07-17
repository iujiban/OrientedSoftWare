public class Main {
    public static void main(String[] args) {
        //Model m = new Model(); // observer
        try {
            Life l = new Life(); // observable
            BigBang b = new BigBang(l);
            b.start(30, 256 * 4);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
