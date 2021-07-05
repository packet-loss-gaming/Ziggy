package gg.packetloss.ziggy.intel.matcher.builtin;

import gg.packetloss.ziggy.intel.context.BlockActionContext;
import gg.packetloss.ziggy.intel.context.PlayerTrustContext;
import gg.packetloss.ziggy.intel.matcher.BlockInteractMatcher;

public class AnyBlockInteractActionMatcher implements BlockInteractMatcher {
    @Override
    public int getTrustAdjustmentInContext(BlockActionContext blockContext, PlayerTrustContext trustContext) {
        return 0;
    }
}
