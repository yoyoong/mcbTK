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
        String arg8 = "20";
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
    public void test_mcbView() throws Exception {
        Main main = new Main();
        String arg0 = "mcbView";
        String arg1 = "-input";
        String arg2 = "COAD.txt.gz";
        String arg3 = "-region";
        String arg4 = "chr1:800083-869347";
        String arg5 = "-sampleID";
        String arg6 = "";
        // String arg6 = "TCGA-3L-AA1B-01A-11D-A36Y-05 TCGA-4N-A93T-01A-11D-A36Y-05 TCGA-4T-AA8H-01A-11D-A40X-05 TCGA-5M-AAT4-01A-11D-A40X-05 TCGA-5M-AAT5-01A-21D-A40X-05 TCGA-5M-AAT6-01A-11D-A40X-05 TCGA-5M-AATA-01A-31D-A40X-05 TCGA-5M-AATE-01A-11D-A40X-05 TCGA-A6-2671-11A-01D-1551-05 TCGA-A6-2671-01A-01D-1407-05 TCGA-A6-2675-11A-01D-1721-05 TCGA-A6-2675-01A-02D-1721-05 TCGA-A6-2677-01A-01D-A27A-05 TCGA-A6-2679-11A-01D-1551-05 TCGA-A6-2679-01A-02D-1407-05 TCGA-A6-2680-11A-01D-1551-05 TCGA-A6-2680-01A-01D-1407-05 TCGA-A6-2681-11A-01D-1551-05 TCGA-A6-2681-01A-01D-1407-05 TCGA-A6-2682-11A-01D-1551-05 TCGA-A6-2682-01A-01D-1407-05 TCGA-A6-2684-11A-01D-1551-05 TCGA-A6-2684-01A-01D-A27A-05 TCGA-A6-2685-11A-01D-1551-05 TCGA-A6-2685-01A-01D-1407-05";
        String arg7 = "-nSample";
        String arg8 = "20";
        String arg9 = "-outFormat";
        String arg10 = "png";
        String[] args = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10};

        System.out.println("Work direqtory: " + System.getProperty("user.dir"));
        String argsStr = "";
        for (int i = 0; i < args.length; i++) {
            argsStr += args[i] + " ";
        }
        System.out.println(argsStr);

        main.main(args);
    }

    @Test
    public void test_MCBDiscovery() throws Exception {
        Main main = new Main();
        String arg0 = "MCBDiscovery";
        String arg1 = "-input";
        String arg2 = "COAD.txt.gz";
        String arg3 = "-bed";
        String arg4 = "COAD_DMR.bed";
        String arg5 = "-sampleID";
        String arg6 = "";
        // String arg6 = "TCGA-3L-AA1B-01A-11D-A36Y-05 TCGA-4N-A93T-01A-11D-A36Y-05 TCGA-4T-AA8H-01A-11D-A40X-05 TCGA-5M-AAT4-01A-11D-A40X-05 TCGA-5M-AAT5-01A-21D-A40X-05 TCGA-5M-AAT6-01A-11D-A40X-05 TCGA-5M-AATA-01A-31D-A40X-05 TCGA-5M-AATE-01A-11D-A40X-05 TCGA-A6-2671-11A-01D-1551-05 TCGA-A6-2671-01A-01D-1407-05 TCGA-A6-2675-11A-01D-1721-05 TCGA-A6-2675-01A-02D-1721-05 TCGA-A6-2677-01A-01D-A27A-05 TCGA-A6-2679-11A-01D-1551-05 TCGA-A6-2679-01A-02D-1407-05 TCGA-A6-2680-11A-01D-1551-05 TCGA-A6-2680-01A-01D-1407-05 TCGA-A6-2681-11A-01D-1551-05 TCGA-A6-2681-01A-01D-1407-05 TCGA-A6-2682-11A-01D-1551-05 TCGA-A6-2682-01A-01D-1407-05 TCGA-A6-2684-11A-01D-1551-05 TCGA-A6-2684-01A-01D-A27A-05 TCGA-A6-2685-11A-01D-1551-05 TCGA-A6-2685-01A-01D-1407-05";
        String arg7 = "-nSample";
        String arg8 = "20";
        String arg9 = "-R";
        String arg10 = "0.8";
        String arg11 = "-pvalue";
        String arg12 = "0.05";
        String arg13 = "-window";
        String arg14 = "3";
        String arg15 = "-distance";
        String arg16 = "1000";

        //String[] args = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16};
        String[] args = {arg0, arg1, arg2, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16};

        System.out.println("Work direqtory: " + System.getProperty("user.dir"));
        String argsStr = "";
        for (int i = 0; i < args.length; i++) {
            argsStr += args[i] + " ";
        }
        System.out.println(argsStr);

        main.main(args);
    }
}