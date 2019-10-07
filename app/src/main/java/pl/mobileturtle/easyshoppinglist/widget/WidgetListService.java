package pl.mobileturtle.easyshoppinglist.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import pl.mobileturtle.easyshoppinglist.R;
import pl.mobileturtle.easyshoppinglist.data.ShoppingListData;
import pl.mobileturtle.easyshoppinglist.data.ShoppingListDatabase;

public class WidgetListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext());
    }

    private class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
        private Context context;
        private List<ShoppingListData> data;

        private WidgetRemoteViewsFactory(Context applicationContext){
            this.context = applicationContext;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            ShoppingListDatabase db = ShoppingListDatabase.getInstance(context);
            data = db.shoppingListDao().loadWidgetNeedProducts();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            if (data != null){
                return data.size();
            } else {
                return 0;
            }
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(data == null || data.size() == 0) return null;

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            views.setTextViewText(R.id.product_name, data.get(position).getProductName());
            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.container, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
