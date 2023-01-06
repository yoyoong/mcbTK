package com;

import com.args.StatArgs;
import com.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stat {
    public static final Logger log = LoggerFactory.getLogger(Stat.class);

    StatArgs args = new StatArgs();
    Util util = new Util();

    public void stat(StatArgs statArgs) throws Exception {
        log.info("command.stat start!");
        args = statArgs;

//        CpgFile cpgFile = new CpgFile(args.getCpgPath());
//        MHapFile mHapFileN = new MHapFile(args.getMhapPathN());
//        MHapFile mHapFileT = new MHapFile(args.getMhapPathT());

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        log.info("command.stat end!");
    }

    private boolean checkArgs() {

        return true;
    }

}
