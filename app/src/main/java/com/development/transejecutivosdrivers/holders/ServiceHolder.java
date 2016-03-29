package com.development.transejecutivosdrivers.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceHolder extends RecyclerView.ViewHolder {
    Service service;
    Passenger passenger;

    TextView txtview_start_date;
    TextView txtview_reference;
    TextView txtview_source;
    TextView txtview_destiny;
    TextView txtview_pax_cant;
    TextView txtview_pax;
    TextView txtview_status;
    TextView txtview_observations;

    TextView txtview_passenger_name;
    TextView txtview_passenger_phone;
    TextView txtview_passenger_email;
    TextView txtview_passenger_fly;

    Button button_accept;
    Button button_decline;

    Context context;

    public ServiceHolder(View itemView, Context context) {
        super(itemView);
        txtview_start_date = (TextView) itemView.findViewById(R.id.txtview_start_date);
        txtview_reference = (TextView) itemView.findViewById(R.id.txtview_reference);
        txtview_destiny = (TextView) itemView.findViewById(R.id.txtview_destiny);
        txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
        txtview_pax_cant = (TextView) itemView.findViewById(R.id.txtview_pax_cant);
        txtview_pax = (TextView) itemView.findViewById(R.id.txtview_pax);
        txtview_status = (TextView) itemView.findViewById(R.id.txtview_status);
        txtview_observations = (TextView) itemView.findViewById(R.id.txtview_observations);

        txtview_passenger_name = (TextView) itemView.findViewById(R.id.txtview_passenger_name);
        txtview_passenger_phone = (TextView) itemView.findViewById(R.id.txtview_passenger_phone);
        txtview_passenger_email = (TextView) itemView.findViewById(R.id.txtview_passenger_email);
        txtview_passenger_fly = (TextView) itemView.findViewById(R.id.txtview_passenger_fly);

        button_accept = (Button) itemView.findViewById(R.id.button_accept);
        button_decline = (Button) itemView.findViewById(R.id.button_decline);

        this.context = context;
    }

    public void setService(Service service) {
        this.service = service;
        txtview_start_date.setText(service.getStartDate());
        txtview_reference.setText(service.getReference());
        txtview_destiny.setText("Destino: " + service.getDestiny());
        txtview_source.setText("Origen: " + service.getSource());
        txtview_pax_cant.setText(service.getPaxCant() + " Pasajero(s)");

        if (!TextUtils.isEmpty(service.getPax())) {
            txtview_pax.setText("Pasajeros: " + service.getPax());
        }

        txtview_observations.setText("Observaciones: " + service.getObservations());
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
        txtview_passenger_name.setText(passenger.getName() + " " + passenger.getLastName());
        txtview_passenger_phone.setText(passenger.getPhone());
        txtview_passenger_email.setText(passenger.getEmail());

        if (!TextUtils.isEmpty(passenger.getFly()) && !TextUtils.isEmpty(passenger.getAeroline())) {
            txtview_passenger_fly.setText(Html.fromHtml("Vuelo: <a href=\"" + this.context.getResources().getString(R.string.url_fly) + passenger.getFly() + "\">" + passenger.getFly() + ", " + passenger.getAeroline() + "</a>"));
        }

        hideElements();
    }

    public void hideElements() {
        if (!TextUtils.isEmpty(service.getCd())) {
            button_accept.setVisibility(View.GONE);
        }

        Date date = new Date();
        String now = date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " " + date.getHours() + ":" + date.getMinutes();
        long millisToAdd = 86_400_000;
        String startDate = sumTimeToDate(service.getStartDate(), millisToAdd);

        if (validateDates(startDate, now)) {
            button_accept.setVisibility(View.GONE);
        }
    }

    public boolean validateDates(String d1, String d2) {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Date date1 = formatter.parse(d1);
            Date date2 = formatter.parse(d2);

            if (date1.compareTo(date2) < 0) {
                //date2 is Greater than date1
                return true;
            }
            return false;
        }
        catch (ParseException e1){
            e1.printStackTrace();
            return false;
        }
    }

    public String sumTimeToDate(String date, long time) {
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date d = format.parse(date);
            d.setTime(d.getTime() + time);

            return "" + d;
        }
        catch (ParseException e1){
            e1.printStackTrace();
            return "";
        }
    }
}