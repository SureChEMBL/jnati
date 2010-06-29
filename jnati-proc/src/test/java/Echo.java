import java.io.IOException;

/**
 * @author sea36
 */
public class Echo {

    public static void main(String[] args) throws IOException {
        byte[] b = new byte[1024];
        for (int n = System.in.read(b); n != -1; n = System.in.read(b)) {
            System.out.write(b, 0, n);
        }
    }

}
