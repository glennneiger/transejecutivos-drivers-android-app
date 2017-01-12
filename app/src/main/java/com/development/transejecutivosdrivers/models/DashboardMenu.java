package com.development.transejecutivosdrivers.models;
import com.development.transejecutivosdrivers.R;

public class DashboardMenu {
    private String name;
    private int idDrawable;
    private int id;

    public DashboardMenu(int id, String name, int idDrawable) {
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
            new DashboardMenu(0, "Mi Perfil", R.drawable.profile),
            new DashboardMenu(1, "Ver Servicios", R.drawable.services),
            new DashboardMenu(2, "Buscar Servicio", R.drawable.search),
            new DashboardMenu(3, "Cerrar Sesión", R.drawable.logout),
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
