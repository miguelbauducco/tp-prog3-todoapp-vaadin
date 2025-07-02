package app.todo.base.ui.view;

import app.todo.base.ui.component.ViewToolbar;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PermitAll 
public final class MainView extends Main {

    MainView() {
        addClassName(LumoUtility.Padding.MEDIUM);
        add(new ViewToolbar("Inicio"));
        add(new Div("Despliegue el menu de vistas"));
    }

    public static void showMainView() {
        UI.getCurrent().navigate(MainView.class);
    }
}
