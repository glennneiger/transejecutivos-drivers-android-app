package com.development.transejecutivosdrivers.holders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.RecoverpassActivity;
import com.development.transejecutivosdrivers.models.Service;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceMainHolder extends RecyclerView.ViewHolder {
    public Service service;

    TextView txtview_start_date;
    TextView txtview_service_start_time;
    TextView txtview_reference;
    TextView txtview_source;
    TextView txtview_destiny;
    TextView txtview_company;
    TextView txtview_event;
    TextView txtview_pax_cant;
    View txtview_pax_cant_container;
    TextView txtview_observations;
    View txtview_observations_container;

    public View card_view_services_list;

    final Context context;

    public ServiceMainHolder(View itemView, Context context) {
        super(itemView);

        card_view_services_list = itemView.findViewById(R.id.card_view_services_list);

        txtview_start_date = (TextView) itemView.findViewById(R.id.txtview_start_date);
        txtview_service_start_time = (TextView) itemView.findViewById(R.id.txtview_service_start_time);
        txtview_reference = (TextView) itemView.findViewById(R.id.txtview_reference);
        txtview_destiny = (TextView) itemView.findViewById(R.id.txtview_destiny);
        txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
        txtview_company = (TextView) itemView.findViewById(R.id.txtview_company);
        txtview_event = (TextView) itemView.findViewById(R.id.txtview_event);
        txtview_pax_cant = (TextView) itemView.findViewById(R.id.txtview_pax_cant);
        txtview_pax_cant_container = itemView.findViewById(R.id.txtview_pax_cant_container);
        txtview_observations = (TextView) itemView.findViewById(R.id.txtview_observations);
        txtview_observations_container = itemView.findViewById(R.id.txtview_observations_container);

        this.context = context;
    }

    /**
     *
     * @param service
     */
    public void setService(Service service) {
        this.service = service;
        txtview_start_date.setText(service.getStartDateNice());
        txtview_service_start_time.setText(service.getServiceStartTime());
        txtview_reference.setText(service.getReference());
        txtview_destiny.setText(" " + service.getDestiny());
        txtview_source.setText(" " + service.getSource());
        txtview_company.setText(service.getCompany());
        txtview_event.setText(service.getEvent());

        if (TextUtils.isEmpty(service.getEvent())) {
            txtview_event.setVisibility(View.GONE);
        }

        txtview_pax_cant_container.setVisibility(View.GONE);
        if (service.getPaxCant() > 1) {
            txtview_pax_cant_container.setVisibility(View.VISIBLE);
            txtview_pax_cant.setText(service.getPaxCant() + " Pasajeros");
        }

        txtview_observations_container.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(service.getObservations())) {
            txtview_observations_container.setVisibility(View.VISIBLE);
            txtview_observations.setText(" " + service.getObservations());
        }
    }

    public View getItemView() {
        return this.itemView;
    }
}
