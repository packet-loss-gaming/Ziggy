package gg.packetloss.ziggy.intel.matcher;

import gg.packetloss.ziggy.intel.context.BlockActionContext;

public interface BlockInteractMatcher extends BlockActionMatcher {
    @Override
    public default boolean matches(BlockActionContext blockContext) {
        return blockContext.isInteract();
    }

    @Override
    public default EventClassification classifyEvent(BlockActionContext blockContext) {
        switch (blockContext.getTo().classify()) {
            case STRUCTURAL:
                return EventClassification.STRUCTURE_INTERACT;
            case ENVIRONMENTAL:
                return EventClassification.ENVIRONMENT_INTERACT;
            case UNDECIDED:
                return EventClassification.UNDECIDED_INTERACT;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
