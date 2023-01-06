package com;

import com.args.MCBViewArgs;
import com.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCBView {
    public static final Logger log = LoggerFactory.getLogger(MCBView.class);

    MCBViewArgs args = new MCBViewArgs();
    Util util = new Util();

    public void mcbView(MCBViewArgs mcbViewArgs) throws Exception {
        log.info("command.mcbView start!");
        args = mcbViewArgs;

//        CpgFile cpgFile = new CpgFile(args.getCpgPath());
//        MHapFile mHapFileN = new MHapFile(args.getMhapPathN());
//        MHapFile mHapFileT = new MHapFile(args.getMhapPathT());

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        log.info("command.mcbView end!");
    }

    private boolean checkArgs() {

        return true;
    }

}
