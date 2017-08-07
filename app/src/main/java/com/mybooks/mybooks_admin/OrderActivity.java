package com.mybooks.mybooks_admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    TextView filterInProcess, filterPlaced, filterOutForDel, filterDelivered, filterCancelled;

    RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    FirebaseRecyclerAdapter<OrderModelClass, OrderViewHolder> firebaseRecyclerAdapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

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

        mLayoutManager = new LinearLayoutManager(OrderActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0 ) {
                    Toast.makeText(getApplicationContext(), "No order found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),  dataSnapshot.getChildrenCount() + " order(s) found", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //progressDialog.show();
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

        progressDialog.show();

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

        mLayoutManager = new LinearLayoutManager(OrderActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0 ) {
                    Toast.makeText(getApplicationContext(), "No order found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),  dataSnapshot.getChildrenCount() + " order(s) found", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        String orderId;
        String comment;
        String orderBy;

        public OrderViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            TextView get_order_details = (TextView) itemView.findViewById(R.id.get_order_details);
            get_order_details.setOnClickListener(this);

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

            TextView addComment = (TextView) itemView.findViewById(R.id.rvAddCommentstoOrder);
            addComment.setOnClickListener(this);
        }

        public void setOrderId(String id) {
            TextView mOrderId = (TextView) view.findViewById(R.id.rvOrderId);
            mOrderId.setText("Order Id: " + id);
            this.orderId = id;
        }

        public void setOrderBy(String orderBy) {
            TextView mOrderBy = (TextView) view.findViewById(R.id.rvOrderBy);
            mOrderBy.setText("Ordered by: " + orderBy);
            this.orderBy = orderBy;
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
            if (comment == null)
                return;
            
            TextView mComment = (TextView) view.findViewById(R.id.rvComments);
            mComment.setText(comment.replace("EEEE", "Customer"));
            this.comment = comment;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.get_order_details:
                    Intent intent = new Intent(view.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("orderBy", orderBy);
                    view.getContext().startActivity(intent);
                    break;

                case R.id.rvCancelOrder:
                    changeStatus(v.getContext().getString(R.string.order_cancelled));
                    break;

                case R.id.rvOrderPlaced:
                    changeStatus(v.getContext().getString(R.string.order_placed));
                    break;

                case R.id.rvInProcess:
                    changeStatus(v.getContext().getString(R.string.order_inprocess));
                    break;

                case R.id.rvOutForDelivery:
                    changeStatus(v.getContext().getString(R.string.out_for_delivery));
                    break;

                case R.id.rvDelivered:
                    changeStatus(v.getContext().getString(R.string.delivered));
                    break;

                case R.id.rvDeleteOrder:

                    DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(orderId);
                    orderDatabaseReference.removeValue();

                    DatabaseReference detailsDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("OrderDetails")
                            .child(orderBy.replace(".", "*"))
                            .child(orderId);
                    detailsDatabaseReference.removeValue();

                    break;

                case R.id.rvAddCommentstoOrder:
                    changeStatus("comment");
                    break;
            }
        }

        public void changeStatus(final String status) {

            if (status.equals("comment")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                final EditText edittext = new EditText(view.getContext());
                alert.setTitle("Enter comment");
                alert.setView(edittext);
                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String reason = edittext.getText().toString();
                        if (TextUtils.isEmpty(reason))
                            return;

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(orderId);
                        databaseReference.child("comment").setValue(comment + "My Books: " + reason + "\n")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(view.getContext(), "Comment updated", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(view.getContext(), "Failed to update comment", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();

            } else if (status.equals(view.getContext().getString(R.string.order_cancelled))) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                final EditText edittext = new EditText(view.getContext());
                alert.setTitle("Enter reason");
                alert.setView(edittext);

                alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String reason = edittext.getText().toString();
                        if (TextUtils.isEmpty(reason))
                            return;

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(orderId);
                        databaseReference.child("comment").setValue(comment + "My Books: " + status + " (" + reason + ")\n");
                        databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(view.getContext(), "Status updated to: " + status, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(view.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();

            } else {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(orderId);
                //databaseReference.child("comment").setValue(comment + "My Books: " + status + "\n");
                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Status updated to: " + status, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(view.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
