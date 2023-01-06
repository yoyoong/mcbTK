package com;

import com.args.MCBDiscoveryArgs;
import com.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCBDiscovery {
    public static final Logger log = LoggerFactory.getLogger(MCBDiscovery.class);

    MCBDiscoveryArgs args = new MCBDiscoveryArgs();
    Util util = new Util();

    public void mcbDiscovery(MCBDiscoveryArgs mcbDiscoveryArgs) throws Exception {
        log.info("command.mcbDiscovery start!");
        args = mcbDiscoveryArgs;

//        CpgFile cpgFile = new CpgFile(args.getCpgPath());
//        MHapFile mHapFileN = new MHapFile(args.getMhapPathN());
//        MHapFile mHapFileT = new MHapFile(args.getMhapPathT());

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        log.info("command.mcbDiscovery end!");
    }

    private boolean checkArgs() {

        return true;
    }

}
