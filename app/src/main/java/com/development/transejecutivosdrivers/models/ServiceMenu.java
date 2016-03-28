package com.development.transejecutivosdrivers.models;

import com.development.transejecutivosdrivers.R;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceMenu {
    private String name;
    private int idDrawable;
    private int id;

    public ServiceMenu(int id, String name, int idDrawable) {
        this.id = id;
        this.name = name;
        this.idDrawable = idDrawable;
    }

    public String getName() {
        return name;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public int getId() {
        return id;
    }

    public static DashboardMenu[] ITEMS = {
            new DashboardMenu(0, "Conducir", R.drawable.profile),
            new DashboardMenu(1, "Llamar", R.drawable.services),
    };

    /**
     * Obtiene item basado en su identificador
     *
     * @param id identificador
     * @return Coche
     */
    public static DashboardMenu getItem(int id) {
        for (DashboardMenu item : ITEMS) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}
