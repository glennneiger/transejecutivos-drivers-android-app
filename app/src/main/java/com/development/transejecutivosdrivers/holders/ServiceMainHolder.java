package com.development.transejecutivosdrivers.holders;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.Service;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceMainHolder extends RecyclerView.ViewHolder {
    Service service;

    TextView txtview_start_date;
    TextView txtview_reference;
    TextView txtview_source;
    TextView txtview_destiny;
    TextView txtview_pax_cant;
    TextView txtview_pax;
    TextView txtview_status;
    TextView txtview_observations;

    Context context;

    public ServiceMainHolder(View itemView, Context context) {
        super(itemView);
        txtview_start_date = (TextView) itemView.findViewById(R.id.txtview_start_date);
        txtview_reference = (TextView) itemView.findViewById(R.id.txtview_reference);
        txtview_destiny = (TextView) itemView.findViewById(R.id.txtview_destiny);
        txtview_source = (TextView) itemView.findViewById(R.id.txtview_source);
        txtview_pax_cant = (TextView) itemView.findViewById(R.id.txtview_pax_cant);
        txtview_pax = (TextView) itemView.findViewById(R.id.txtview_pax);
        txtview_status = (TextView) itemView.findViewById(R.id.txtview_status);
        txtview_observations = (TextView) itemView.findViewById(R.id.txtview_observations);

        this.context = context;
    }

    /**
     *
     * @param service
     */
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

        validateService();
    }

    public void validateService() {
        if (this.service.getStatus().equals("orden")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
            dialog.setMessage("La orden " + this.service.getReference() + ", se encuentra pendiente. Para poder aceptar nuevas ordenes debe completarla");
            dialog.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    public View getItemView() {
        return this.itemView;
    }
}
