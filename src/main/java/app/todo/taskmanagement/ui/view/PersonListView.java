package app.todo.taskmanagement.ui.view;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import app.todo.taskmanagement.domain.Person;
import app.todo.taskmanagement.service.PersonService;
import jakarta.annotation.security.PermitAll;

@Route("person-list")
@PageTitle("Lista de usuarios")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Lista de usuarios")
@PermitAll
public class PersonListView extends Main {
    private Person personaActual = new Person();
    private Binder<Person> binder = new BeanValidationBinder<>(Person.class);

    private TextField apellido = new TextField("Apellido");
    private TextField nombre = new TextField("Nombre");
    private TextField dni = new TextField("DNI");
    private TextField edad= new TextField("Edad");

    private Button edit = new Button("Nuevo");
    private Button save = new Button("Guardar");

    private Grid<Person> personGrid;
    private PersonService personService;

    public PersonListView(PersonService personService) {
        this.personService = personService;

        nombre.setWidthFull();
        dni.setWidthFull();
        apellido.setWidthFull();
        edad.setWidthFull();    

        binder.bindInstanceFields(this);

        edit.addClickListener(e -> {
            personaActual = new Person();
            binder.readBean(personaActual);
        });

        save.addClickListener(e -> {
            try {
                binder.writeBean(personaActual);
                personService.savePerson(personaActual);
                personaActual = new Person();
                binder.readBean(personaActual);
                personGrid.setItems(personService.listAll());  
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addClassName("contact-form");

        H1 title = new H1("Administración de Usuarios");
        title.getStyle().set("text-align", "center").set("width", "100%");

        HorizontalLayout buttons = new HorizontalLayout(edit, save);
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);
        buttons.setWidthFull();


        VerticalLayout content = new VerticalLayout();
        content.add(apellido, nombre,edad, dni, buttons);

        personGrid = new Grid<>();
        personGrid.setItems(personService.listAll());
        personGrid.addColumn(Person::getId).setHeader("ID");
        personGrid.addColumn(Person::getApellido).setHeader("Apellido");
        personGrid.addColumn(Person::getNombre).setHeader("Nombre");
        personGrid.addColumn(Person::getDni).setHeader("DNI");
        personGrid.addColumn(Person::getEdad).setHeader("Edad");

        personGrid.addComponentColumn(person -> {
            Button delete = new Button("Eliminar");

            delete.addClickListener(event -> {
                personService.deletePerson(person.getId());
                personGrid.setItems(personService.listAll());
            });
            return delete;
        }).setHeader("Acciones");

        content.add(personGrid);
        add(title, content);
    }
}