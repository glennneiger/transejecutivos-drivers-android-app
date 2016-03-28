package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.ServiceMenuAdapter;
import com.development.transejecutivosdrivers.holders.ServiceHolder;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.ServiceMenu;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceFragment extends FragmentBase implements AdapterView.OnItemClickListener{
    View fragmentContainer;
    View progressBar;
    GridView gridView;
    ServiceMenuAdapter adaptador;

    public ServiceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_fragment, container, false);

        fragmentContainer = view.findViewById(R.id.service_container);
        progressBar = view.findViewById(R.id.service_progress);

        //gridView = (GridView) view.findViewById(R.id.gridView_service_menu);
        //adaptador = new ServiceMenuAdapter(getActivity());
        //gridView.setAdapter(adaptador);

        //gridView.setOnItemClickListener(this);

        getService();

        return view;
    }

    public void getService() {
        //showProgress(true, fragmentContainer, progressBar);
        Service service = new Service();
        service.setIdService(1);
        service.setStartDate(1 + "/03/2016 8:0" + 1);
        service.setDestiny("Bogotá cll " + 1);
        service.setSource("Cali cra " + 1);
        service.setPaxCant(5);
        service.setPax("");
        service.setObservations("There's no observations");
        service.setReference("U56" + 1);
        service.setStatus("orden");

        Passenger passenger = new Passenger();
        passenger.setIdPassenger(1);
        passenger.setName("Armando");
        passenger.setLastName("Estban Quito");
        passenger.setCode("XXX");
        passenger.setEmail("xxx@hotmail.com, jjj@hotmail.com");
        passenger.setPhone("22223343 333333333");
        passenger.setFly("AV56");
        passenger.setAeroline("Avianca");

        ServiceHolder serviceHolder = new ServiceHolder(view, getActivity());
        serviceHolder.setService(service);
        serviceHolder.setPassenger(passenger);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServiceMenu item = (ServiceMenu) parent.getItemAtPosition(position);
        switch (item.getId()) {
            case 0:

                break;

            case 1:

                break;

            default:
                break;
        }

    }
}
