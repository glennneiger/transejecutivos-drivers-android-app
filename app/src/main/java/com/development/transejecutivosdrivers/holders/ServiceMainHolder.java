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
import com.development.transejecutivosdrivers.models.Subcompany;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceMainHolder extends RecyclerView.ViewHolder {
    public Service service;

    TextView txtview_service_start_time;
    TextView txtview_reference;
    TextView txtview_source;
    TextView txtview_company;
    TextView txtview_pax_cant;
    View txtview_pax_cant_container;
    TextView txtview_observations;
    View txtview_observations_container;
    View event_container;
    TextView txtview_event;

    View subcompany_container;
    TextView txtview_subcompany;

    public View card_view_services_list;

    final Context context;

    public ServiceMainHolder(View itemView, Context context) {
        super(itemView);

        card_view_services_list = itemView.findViewById(R.id.card_view_services_list);

        txtview_service_start_time = (TextView) itemView.findViewById(R.id.txtview_service_start_time);
        txtview_reference = (TextView) itemView.findViewById(R.id.txtview_reference);
        txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
        txtview_company = (TextView) itemView.findViewById(R.id.txtview_company);
        txtview_event = (TextView) itemView.findViewById(R.id.txtview_event);
        txtview_pax_cant = (TextView) itemView.findViewById(R.id.txtview_pax_cant);
        txtview_pax_cant_container = itemView.findViewById(R.id.txtview_pax_cant_container);
        txtview_observations = (TextView) itemView.findViewById(R.id.txtview_observations);
        txtview_observations_container = itemView.findViewById(R.id.txtview_observations_container);
        event_container = itemView.findViewById(R.id.event_container);
        subcompany_container = itemView.findViewById(R.id.subcompany_container);
        txtview_subcompany = (TextView) itemView.findViewById(R.id.txtview_subcompany);

        this.context = context;
    }

    /**
     *
     * @param service
     */
    public void setService(Service service) {
        this.service = service;
        txtview_service_start_time.setText(service.getServiceStartTime());
        txtview_reference.setText(service.getReference());
        txtview_source.setText(" " + service.getSource());
        txtview_company.setText(service.getCompany());
        txtview_event.setText(service.getEvent());

        if (TextUtils.isEmpty(service.getEvent())) {
            event_container.setVisibility(View.GONE);
        }

        if (service.getSubcompany() != null) {
            Subcompany subcompany = service.getSubcompany();
            txtview_subcompany.setText(subcompany.getName());
            subcompany_container.setVisibility(View.VISIBLE);
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
