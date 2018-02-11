package com.development.transejecutivosdrivers.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.Service;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceHolder extends RecyclerView.ViewHolder {
    Service service;

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
    View event_container;
    View accept_container;

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
        event_container = itemView.findViewById(R.id.event_container);
        txtview_event = (TextView) itemView.findViewById(R.id.txtview_event);

        txtview_fly = (TextView) itemView.findViewById(R.id.txtview_fly);
        txtview_fly_container = itemView.findViewById(R.id.txtview_fly_container);

        accept_container = itemView.findViewById(R.id.accept_container);
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
            event_container.setVisibility(View.GONE);
        }

        txtview_pax_cant_container.setVisibility(View.GONE);
        if (service.getPaxCant() > 1) {
            txtview_pax_cant_container.setVisibility(View.VISIBLE);
            txtview_pax_cant.setText(service.getPaxCant() + " Pasajeros");
        }

        if (!TextUtils.isEmpty(service.getPax())) {
            if (txtview_pax != null) {
                txtview_pax.setText("Pasajeros: " + service.getPax());
            }
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

    public void hideElements() {
        if (service.getOld() == 1) {
            accept_container.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(service.getCd())) {
            accept_container.setVisibility(View.GONE);
        }
    }
}
