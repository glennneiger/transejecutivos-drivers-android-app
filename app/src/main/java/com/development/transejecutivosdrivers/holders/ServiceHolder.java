package com.development.transejecutivosdrivers.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
    TextView txtview_service_start_time;
    TextView txtview_reference;
    TextView txtview_source;
    TextView txtview_destiny;
    TextView txtview_pax_cant;
    View txtview_pax_cant_container;
    TextView txtview_pax;
    View txtview_fly_container;
    TextView txtview_fly;
    TextView txtview_observations;
    View txtview_observations_container;

    TextView txtview_passenger_name;
    TextView txtview_passenger_phone;
    TextView txtview_passenger_email;
    TextView txtview_passenger_company;
    TextView txtview_event;

    Button button_accept;
    Button button_decline;

    Context context;

    public ServiceHolder(View itemView, Context context) {
        super(itemView);
        txtview_start_date = (TextView) itemView.findViewById(R.id.txtview_start_date);
        txtview_service_start_time = (TextView) itemView.findViewById(R.id.txtview_service_start_time);
        txtview_reference = (TextView) itemView.findViewById(R.id.txtview_reference);
        txtview_destiny = (TextView) itemView.findViewById(R.id.txtview_destiny);
        txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
        txtview_pax_cant = (TextView) itemView.findViewById(R.id.txtview_pax_cant);
        txtview_pax_cant_container = itemView.findViewById(R.id.txtview_pax_cant_container);
        txtview_pax = (TextView) itemView.findViewById(R.id.txtview_pax);
        txtview_observations = (TextView) itemView.findViewById(R.id.txtview_observations);
        txtview_observations_container = itemView.findViewById(R.id.txtview_observations_container);

        txtview_passenger_name = (TextView) itemView.findViewById(R.id.txtview_passenger_name);
        txtview_passenger_company = (TextView) itemView.findViewById(R.id.txtview_passenger_company);
        txtview_event = (TextView) itemView.findViewById(R.id.txtview_event);
        txtview_passenger_phone = (TextView) itemView.findViewById(R.id.txtview_passenger_phone);
        txtview_passenger_email = (TextView) itemView.findViewById(R.id.txtview_passenger_email);
        txtview_fly = (TextView) itemView.findViewById(R.id.txtview_fly);
        txtview_fly_container = itemView.findViewById(R.id.txtview_fly_container);

        button_accept = (Button) itemView.findViewById(R.id.button_accept);
        button_decline = (Button) itemView.findViewById(R.id.button_decline);

        this.context = context;
    }

    public void setService(Service service) {
        this.service = service;
        if (!TextUtils.isEmpty(service.getStartDateNice()) || service.getStartDateNice() != null) {
            txtview_start_date.setText(service.getStartDateNice());
            txtview_service_start_time.setText(service.getServiceStartTime());
        }

        txtview_reference.setText(service.getReference());
        txtview_destiny.setText(" " + service.getDestiny());
        txtview_source.setText(" " + service.getSource());
        txtview_event.setText(service.getEvent());

        if (TextUtils.isEmpty(service.getEvent())) {
            txtview_event.setVisibility(View.GONE);
        }

        txtview_pax_cant_container.setVisibility(View.GONE);
        if (service.getPaxCant() > 1) {
            txtview_pax_cant_container.setVisibility(View.VISIBLE);
            txtview_pax_cant.setText(service.getPaxCant() + " Pasajeros");
        }

        if (!TextUtils.isEmpty(service.getPax())) {
            txtview_pax.setText("Pasajeros: " + service.getPax());
        }

        txtview_fly_container.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(service.getFly()) && !TextUtils.isEmpty(service.getAeroline())) {
            txtview_fly_container.setVisibility(View.VISIBLE);
            txtview_fly.setText(Html.fromHtml("Vuelo: <a href=\"" + this.context.getResources().getString(R.string.url_fly) + service.getFly() + "\">" + service.getFly() + ", " + service.getAeroline() + "</a>"));
        }

        txtview_observations_container.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(service.getObservations())) {
            txtview_observations_container.setVisibility(View.VISIBLE);
            txtview_observations.setText(" " + service.getObservations());
        }
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
        txtview_passenger_name.setText(passenger.getName() + " " + passenger.getLastName());
        txtview_passenger_company.setText(passenger.getCompany());
        txtview_passenger_phone.setText(passenger.getPhone());
        txtview_passenger_email.setText(passenger.getEmail());

        hideElements();
    }

    public void hideElements() {
        if (service.getOld() == 1) {
            button_accept.setVisibility(View.GONE);
            button_decline.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(service.getCd())) {
            button_accept.setVisibility(View.GONE);
            button_decline.setVisibility(View.GONE);
        }
    }
}
