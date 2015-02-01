package io.github.hidroh.materialistic;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.assertj.android.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestItemManager;

@Config(emulateSdk = 18, reportSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ListFragmentViewHolderEdgeTest {
    private ActivityController<FragmentActivity> controller;
    private RecyclerView.ViewHolder holder;
    private FragmentActivity activity;
    public ItemManager.ResponseListener<ItemManager.Item> listener;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(FragmentActivity.class)
                .create().start().resume().visible();
        activity = controller.get();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, ListFragment.instantiate(activity, new TestItemManager() {

                    @Override
                    public void getTopStories(ResponseListener<Item[]> listener) {
                        listener.onResponse(new Item[]{new TestItem() {}});
                    }

                    @Override
                    public void getItem(String itemId, ResponseListener<Item> listener) {
                        ListFragmentViewHolderEdgeTest.this.listener = listener;
                    }
                }))
                .commit();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        holder = recyclerView.getAdapter().createViewHolder(recyclerView, 0);
        recyclerView.getAdapter().bindViewHolder(holder, 0);
    }

    @Test
    public void testNullResponse() {
        listener.onResponse(null);
        Assertions.assertThat((TextView) holder.itemView.findViewById(android.R.id.text2))
                .hasText(activity.getString(R.string.loading_text));
    }

    @Test
    public void testErrorResponse() {
        listener.onError(null);
        Assertions.assertThat((TextView) holder.itemView.findViewById(android.R.id.text2))
                .hasText(activity.getString(R.string.loading_text));
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }
}
