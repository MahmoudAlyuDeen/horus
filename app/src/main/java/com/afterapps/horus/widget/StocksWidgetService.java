package com.afterapps.horus.widget;

/*
 * Created by mahmoudalyudeen on 4/25/17.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;

public class StocksWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StocksProvider(this, intent);
    }
}
