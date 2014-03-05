package com.mctlab.ansight.common.delegate.context;

import com.mctlab.ansight.common.broadcast.BroadcastConfig;

public interface IDelegatable {

    public BroadcastConfig onCreateBroadcastConfig();
}
