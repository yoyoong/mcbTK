import junit.framework.TestCase;
import com.Main;
import org.junit.Test;

public class MainTest extends TestCase {
    @Test
    public void test_stat() throws Exception {
        Main main = new Main();
        String arg0 = "stat";
        String arg1 = "-input";
        String arg2 = "COAD.txt.gz";
        String arg3 = "-bed";
        String arg4 = "COAD_DMR.bed";
        String arg5 = "-sampleID";
        String arg6 = "";
        String arg7 = "-metrics";
        String arg8 = "mean median var";
        String[] args = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8};

        System.out.println("Work direqtory: " + System.getProperty("user.dir"));
        String argsStr = "";
        for (int i = 0; i < args.length; i++) {
            argsStr += args[i] + " ";
        }
        System.out.println(argsStr);

        main.main(args);
    }

    @Test
    public void test_R() throws Exception {
        Main main = new Main();
        String arg0 = "R";
        String arg1 = "-input";
        String arg2 = "COAD.txt.gz";
//        String arg3 = "-region";
//        String arg4 = "chr1:28734-29810";
        String arg3 = "-bed";
        String arg4 = "COAD_DMR.bed";
        String arg5 = "-sampleID";
        String arg6 = "";
        String arg7 = "-nSample";
        String arg8 = "335";
        String[] args = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8};

        System.out.println("Work direqtory: " + System.getProperty("user.dir"));
        String argsStr = "";
        for (int i = 0; i < args.length; i++) {
            argsStr += args[i] + " ";
        }
        System.out.println(argsStr);

        main.main(args);
    }
}