package com.example.b10709053_hw2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Reservation> reservationList;
    private MySQLiteOpenHelper helper;
    private RecyclerView rvReservation;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(helper == null){
            helper = new MySQLiteOpenHelper(this);
        }

        rvReservation = (RecyclerView) findViewById(R.id.rvReservation);
        rvReservation.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter(this);
        rvReservation.setAdapter(reservationAdapter);

        //ItemTouchHelper連結RecycleView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(reservationAdapter));
        itemTouchHelper.attachToRecyclerView(rvReservation);
    }

    //RecyclerView實作
    private class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> implements ItemMoveSwipeListener{
        private Context context;
        private LayoutInflater layoutInflater;

        public ReservationAdapter(Context context){
            this.context=context;
            layoutInflater=LayoutInflater.from(context);
            reservationList = helper.getList();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tvName, tvNumber;

            public ViewHolder(View itemView){
                super(itemView);
                tvName=itemView.findViewById(R.id.tvName);
                tvNumber=itemView.findViewById(R.id.tvNumber);
            }

            public TextView getTvName(){
                return tvName;
            }

            public TextView getTvNumber(){
                return tvNumber;
            }
        }

        @Override
        public int getItemCount(){
            return reservationList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            View itemView = layoutInflater.inflate(
                    R.layout.recyclerview_item, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position){
            Reservation reservation = reservationList.get(position);
            viewHolder.getTvName().setText(String.valueOf(reservation.getName()));
            viewHolder.getTvNumber().setText(String.valueOf(reservation.getNumber()));
            switch (getPreferenceChange()){
                case "Red":
                    viewHolder.getTvNumber().setBackgroundColor(Color.parseColor("#FF0000"));
                    break;
                case "Blue":
                    viewHolder.getTvNumber().setBackgroundColor(Color.parseColor("#0000FF"));
                    break;
                case "Green":
                    viewHolder.getTvNumber().setBackgroundColor(Color.parseColor("#00FF00"));
                    break;
            }
        }

        //實作ItemMoveSwipeListener interface
        @Override
        public boolean onItemMove(int fromPosition, int toPosition){
            return false;
        }

        @Override
        public void onItemSwipe(final int position){
            new AlertDialog.Builder(context)
                    .setTitle("確認方塊")
                    .setMessage("確認要刪除嗎?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rvReservation.setAdapter(reservationAdapter);
                        }
                    })
                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int deleteId = helper.deleteById(reservationList.get(position).getID());
                            reservationList.remove(position);
                            rvReservation.setAdapter(reservationAdapter);
                        }
                    }).show();
        }
    }

    //swipe效果
    public interface ItemMoveSwipeListener{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemSwipe(int position);
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private ItemMoveSwipeListener itemMoveSwipeListener;

        public ItemTouchHelperCallback(ItemMoveSwipeListener itemMoveSwipeListener){
            this.itemMoveSwipeListener = itemMoveSwipeListener;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
            return itemMoveSwipeListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
            itemMoveSwipeListener.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

    //回到主頁面時更新List
    @Override
    protected void onStart(){
        super.onStart();
        rvReservation.setAdapter(new ReservationAdapter(this));
    }

    //加入Menu
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    //Menu被點擊後處理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add:
                Intent intentAdd = new Intent(this, Add.class);
                startActivity(intentAdd);
                break;
            case R.id.color:
                Intent intentColor = new Intent(this,Setting.class);
                startActivity(intentColor);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //取得sharedPreference
    private String getPreferenceChange(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String option = pref.getString("color_options","1");
        String[] optionText = getResources().getStringArray(R.array.color_options);
        return optionText[Integer.parseInt(option)];
    }

    //關閉資料庫
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (helper != null){
            helper.close();
        }
    }
}
