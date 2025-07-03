package app.todo.taskmanagement.ui.view;

import app.todo.base.ui.component.ViewToolbar;
import app.todo.taskmanagement.domain.Person;
import app.todo.taskmanagement.domain.Task;
import app.todo.taskmanagement.service.PersonService;
import app.todo.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import app.todo.base.ui.view.MainLayout;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route(value ="task-list", layout = MainLayout.class)
@PageTitle("Lista de Tareas")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Lista de Tareas")
@PermitAll 
public class TaskListView extends VerticalLayout {

        private final TaskService taskService;
        private final PersonService personService;
        private Clock clock;
        private final ComboBox<Person> personSelector;

        final TextField description;
        final DatePicker dueDate;
        final Button createBtn;
        final Grid<Task> taskGrid;

        public TaskListView(TaskService taskService, Clock clock, PersonService personService) {
                this.taskService = taskService;
                this.personService = personService;
                this.clock = clock;
                

                description = new TextField();
                description.setPlaceholder("Que quieres hacer?");
                description.setAriaLabel("Descripcion de tarea");
                description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
                description.setMinWidth("20em");

                dueDate = new DatePicker();
                dueDate.setPlaceholder("Fecha de vto.");
                dueDate.setAriaLabel("Fecha de vencimiento");

                personSelector = new ComboBox<>();
                personSelector.setPlaceholder("Usuario");
                personSelector.setItems(personService.listAll());
                personSelector.setItemLabelGenerator(person -> person.getNombre() + " " + person.getApellido());
                
                createBtn = new Button("Crear tarea", event -> createTask());
                createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(clock.getZone()).withLocale(getLocale());
                var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

                taskGrid = new Grid<>();
                taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
                 

                taskGrid.addComponentColumn(task -> {
                        Checkbox checkbox = new Checkbox(task.isDone());
                        checkbox.addValueChangeListener(event -> {
                        task.setDone(event.getValue());
                        taskService.updateTask(task); 
                        });
                        return checkbox;
                }).setHeader("Terminada");

                taskGrid.addColumn(Task::getDescription).setHeader("Descripcion");
                taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never")).setHeader("Fecha de vencimiento   ");
                taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Fecha de creacion");
                taskGrid.addComponentColumn(task -> {
                Button deleteButton = new Button(new Icon(VaadinIcon.TRASH), event -> {

                        Dialog dialog = new Dialog();

                        dialog.setHeaderTitle(
                                         String.format("Borrar tarea: \"%s\"?", task.getDescription()));
                                        dialog.add("¿Estas seguro de querer borrar esta tarea?");

                                Button deleteDlgButton = new Button("Borrar", (e) -> {
                                        taskService.deleteTask(task.getId());
                                        taskGrid.getDataProvider().refreshAll();
                                        dialog.close();
                                });
                                deleteDlgButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                                                ButtonVariant.LUMO_ERROR);
                                deleteDlgButton.getStyle().set("margin-right", "auto");
                                dialog.getFooter().add(deleteDlgButton);

                                Button cancelButton = new Button("Cancelar", (e) -> dialog.close());
                                cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                                dialog.getFooter().add(cancelButton);

                                dialog.open();
                        });
                        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                        deleteButton.setAriaLabel("Borrar");
                        deleteButton.setTooltipText("Close the dialog");
                         deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                        return deleteButton;
                }).setHeader("Borrar");

                taskGrid.setSizeFull();

                setSizeFull();
                addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);
                add(new ViewToolbar("Lista de Tareas", ViewToolbar.group(description, dueDate, personSelector, createBtn)));
                add(taskGrid);
        }

        private void createTask() {
        Task nueva = new Task();
        nueva.setDescription(description.getValue());
        nueva.setDueDate(dueDate.getValue());
        nueva.setCreationDate(clock.instant());

        Person selectedPerson = personSelector.getValue();
                if (selectedPerson == null) {
                Notification.show("Debes seleccionar una persona", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
        }

    nueva.setPerson(selectedPerson);
    taskService.saveTask(nueva);
    taskGrid.getDataProvider().refreshAll();
    description.clear();
    dueDate.clear();
    personSelector.clear();

    Notification notification = Notification.show("Tarea añadida", 3000, Notification.Position.BOTTOM_END);
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
}

}
