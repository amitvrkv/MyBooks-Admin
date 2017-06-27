package com.mybooks.mybooks_admin;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    TextView filterInProcess, filterPlaced, filterOutForDel, filterDelivered, filterCancelled;

    RecyclerView recyclerView;

    FirebaseRecyclerAdapter<OrderModelClass, OrderViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        filterInProcess = (TextView) findViewById(R.id.orderFilInProcess);
        filterInProcess.setOnClickListener(this);
        filterPlaced = (TextView) findViewById(R.id.orderFilOrderPlaced);
        filterPlaced.setOnClickListener(this);
        filterOutForDel = (TextView) findViewById(R.id.orderFilOutForDelivery);
        filterOutForDel.setOnClickListener(this);
        filterDelivered = (TextView) findViewById(R.id.orderFilDelivered);
        filterDelivered.setOnClickListener(this);
        filterCancelled = (TextView) findViewById(R.id.orderFilCancelledOrder);
        filterCancelled.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.orderRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderModelClass, OrderViewHolder>(
                OrderModelClass.class,
                R.layout.order_view,
                OrderViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, OrderModelClass model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setOrderBy(model.getFrom());
                viewHolder.setDate(model.getDate());
                viewHolder.setDiscount(model.getDiscount());
                viewHolder.setTotalAmt(model.getGrandtotal());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setDeliveryAdd(model.getDeliveryaddress());
                viewHolder.setComment(model.getComment());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.orderFilOrderPlaced:
                setFilter("Order placed");
                break;

            case R.id.orderFilInProcess:
                setFilter("In process");
                break;

            case R.id.orderFilOutForDelivery:
                setFilter("Out for delivery");
                break;

            case R.id.orderFilDelivered:
                setFilter("Delivered");
                break;

            case R.id.orderFilCancelledOrder:
                setFilter("Order cancelled");
                break;
        }

    }

    public void setFilter(String status) {
        Query databaseReference = null;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").orderByChild("status").equalTo(status);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderModelClass, OrderViewHolder>(
                OrderModelClass.class,
                R.layout.order_view,
                OrderViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, OrderModelClass model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setOrderBy(model.getFrom());
                viewHolder.setDate(model.getDate());
                viewHolder.setDiscount(model.getDiscount());
                viewHolder.setTotalAmt(model.getGrandtotal());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setDeliveryAdd(model.getDeliveryaddress());
                viewHolder.setComment(model.getComment());
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        String orderId;
        String comment;

        public OrderViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            TextView cancelOrder = (TextView) itemView.findViewById(R.id.rvCancelOrder);
            cancelOrder.setOnClickListener(this);

            TextView orderPlaced = (TextView) itemView.findViewById(R.id.rvOrderPlaced);
            orderPlaced.setOnClickListener(this);

            TextView inProcess = (TextView) itemView.findViewById(R.id.rvInProcess);
            inProcess.setOnClickListener(this);

            TextView outForDel = (TextView) itemView.findViewById(R.id.rvOutForDelivery);
            outForDel.setOnClickListener(this);

            TextView delivered = (TextView) itemView.findViewById(R.id.rvDelivered);
            delivered.setOnClickListener(this);

            TextView delete = (TextView) itemView.findViewById(R.id.rvDeleteOrder);
            delete.setOnClickListener(this);
        }

        public void setOrderId(String id) {
            TextView mOrderId = (TextView) view.findViewById(R.id.rvOrderId);
            mOrderId.setText("Order Id: " + id);
            this.orderId = id;
        }

        public void setOrderBy(String orderBy) {
            TextView mOrderBy = (TextView) view.findViewById(R.id.rvOrderBy);
            mOrderBy.setText("Ordered by: " + orderBy);
        }

        public void setDate(String date) {
            TextView mDate = (TextView) view.findViewById(R.id.rvDate);
            mDate.setText("Date & Time: " + date);
        }

        public void setDiscount(String discount) {
            TextView mDiscount = (TextView) view.findViewById(R.id.rvDiscount);
            mDiscount.setText("Discount: " + discount);
        }

        public void setTotalAmt(String TotalAmt) {
            TextView mTotalAmt = (TextView) view.findViewById(R.id.rvAmt);
            mTotalAmt.setText("Total Amount: " + TotalAmt);
        }

        public void setStatus(String Status) {
            TextView mStatus = (TextView) view.findViewById(R.id.rvStatus);
            mStatus.setText("Status: " + Status);
        }

        public void setDeliveryAdd(String DeliveryAdd) {
            TextView mDeliveryAdd = (TextView) view.findViewById(R.id.rvDeliveryAddress);
            mDeliveryAdd.setText(DeliveryAdd);
        }

        public void setComment(String comment) {
            TextView mComment = (TextView) view.findViewById(R.id.rvComments);
            mComment.setText(comment);
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rvCancelOrder:
                    changeStatus("Order cancelled");
                    break;

                case R.id.rvOrderPlaced:
                    changeStatus("Order placed");
                    break;

                case R.id.rvInProcess:
                    changeStatus("In process");
                    break;

                case R.id.rvOutForDelivery:
                    changeStatus("Out for delivery");
                    break;

                case R.id.rvDelivered:
                    changeStatus("Delivered");
                    break;

                case R.id.rvDeleteOrder:

                    break;
            }
        }

        public void changeStatus(String status) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(orderId);
            databaseReference.child("comment").setValue(comment + "\nMy Books: " + status);
            databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    } else {

                    }
                }
            });
        }
    }
}
