/**
 * Class:
 * Created by joeluban on 11/13/16.
 */
public class Generator {
    public static void generatePerfect(int size) {
        for (int i = 0; i < size; i++) {
            System.out.format("S\t%d\t%d\n",i, 1);
        }
    }

    public static void main(String[] args) {
        generatePerfect(1000000);
    }
}
