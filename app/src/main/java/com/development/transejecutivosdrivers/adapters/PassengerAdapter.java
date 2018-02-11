package com.development.transejecutivosdrivers.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.City;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Source;

import java.util.ArrayList;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.ViewHolder> {
    ArrayList<Passenger> passengers = new ArrayList<>();
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtview_passenger_complete_name;
        public TextView txtview_source;
        public TextView txtview_passenger_phone1;
        public TextView txtview_passenger_phone2;
        public TextView txtview_passenger_email1;
        public TextView txtview_passenger_email2;

        Button button_call_passenger;
        Button button_sms_passenger;

        public ViewHolder(View view) {
            super(view);
            txtview_passenger_complete_name = (TextView) itemView.findViewById(R.id.txtview_passenger_complete_name);
            txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
            txtview_passenger_phone1 = (TextView) itemView.findViewById(R.id.txtview_passenger_phone1);
            txtview_passenger_phone2 = (TextView) itemView.findViewById(R.id.txtview_passenger_phone2);
            txtview_passenger_email1 = (TextView) itemView.findViewById(R.id.txtview_passenger_email1);
            txtview_passenger_email2 = (TextView) itemView.findViewById(R.id.txtview_passenger_email2);

            button_call_passenger = (Button) itemView.findViewById(R.id.button_call_passenger);
            button_sms_passenger = (Button) itemView.findViewById(R.id.button_sms_passenger);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PassengerAdapter(Context context, ArrayList<Passenger> passengers) {
        this.passengers = passengers;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PassengerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_card, parent, false);

        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Passenger passenger = this.passengers.get(position);
        final Source source = passenger.getSource();
        final City city = source.getCity();

        holder.txtview_passenger_complete_name.setText(passenger.getName() + ' ' + passenger.getLastname());
        holder.txtview_source.setText(city.getName() + ", " + source.getAddress());
        holder.txtview_passenger_phone1.setText(passenger.getPhone1());
        holder.txtview_passenger_phone2.setText(passenger.getPhone2());
        holder.txtview_passenger_email1.setText(passenger.getEmail1());
        holder.txtview_passenger_email2.setText(passenger.getEmail2());

        holder.button_sms_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passenger != null && !TextUtils.isEmpty(passenger.getPhone1())) {
                    String phone = passenger.getPhone1();
                    if (!TextUtils.isEmpty(phone)) {
                        phone = passenger.getPhone2();
                    }

                    if (!TextUtils.isEmpty(phone)) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                            context.startActivity(intent);
                        } catch (SecurityException ex) {

                        }
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.passenger_phone_empty_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.button_call_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passenger != null && !TextUtils.isEmpty(passenger.getPhone1())) {
                    String phone = passenger.getPhone1();
                    if (!TextUtils.isEmpty(phone)) {
                        phone = passenger.getPhone2();
                    }

                    if (!TextUtils.isEmpty(phone)) {
                        try {

                        } catch (SecurityException ex) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            context.startActivity(callIntent);
                        }
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.passenger_phone_empty_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.passengers.size();
    }
}