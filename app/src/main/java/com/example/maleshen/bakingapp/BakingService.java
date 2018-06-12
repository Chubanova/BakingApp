package com.example.maleshen.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.maleshen.bakingapp.model.Receipt;

public class BakingService extends IntentService {
    public static final String ACTION_BAKING =
            "com.example.maleshen.bakingapp.action.baking";

    public BakingService(String name) {
        super(name);
    }

    public BakingService() {
        super(ACTION_BAKING);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BAKING.equals(action)) {
                final Receipt receipt = intent.getParcelableExtra("RECEIPT");
                handleBakingIngridiets(receipt);
            }
        }
    }

    public static void startActionReceiptIngridient(Context context, Receipt receipt) {
        Intent intent = new Intent(context, BakingService.class);
        intent.setAction(ACTION_BAKING);
        intent.putExtra("RECEIPT", receipt);

        context.startService(intent);
    }

    private void handleBakingIngridiets(Receipt receipt) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);
        BakingWidget.updateIngridient(this, appWidgetManager, appWidgetIds, receipt);

    }
}
