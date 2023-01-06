package com;

import com.args.RArgs;
import com.args.StatArgs;
import com.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class R {
    public static final Logger log = LoggerFactory.getLogger(R.class);

    RArgs args = new RArgs();
    Util util = new Util();

    public void r(RArgs rArgs) throws Exception {
        log.info("command.r start!");
        args = rArgs;

//        CpgFile cpgFile = new CpgFile(args.getCpgPath());
//        MHapFile mHapFileN = new MHapFile(args.getMhapPathN());
//        MHapFile mHapFileT = new MHapFile(args.getMhapPathT());

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        log.info("command.r end!");
    }

    private boolean checkArgs() {

        return true;
    }

}
