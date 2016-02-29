
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ExampleTableEditingUI extends UI {

  protected Table table;

  protected Button edit;
  protected Button save;
  protected Button cancel;

  protected List<Field> fields = new ArrayList<Field>();

  @WebServlet(value = "/test", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = ExampleTableEditingUI.class)
  public static class Servlet extends VaadinServlet {
  }


  @Override
  protected void init(VaadinRequest request) {

    IndexedContainer container = createContainer();
    table = createTable(container);

    VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.addComponent(table);
    mainLayout.addComponent(createButtons());
    mainLayout.setSizeFull();
    setContent(mainLayout);

    configureEditable(false);
  }

  /**
   * Create all the buttons for the UI
   *
   * @return
   */
  protected HorizontalLayout createButtons() {

    edit = new Button("Edit", new Button.ClickListener() {
      @Override
      public void buttonClick(Button.ClickEvent clickEvent) {
        configureEditable(true);
      }
    });

    save = new Button("Save", new Button.ClickListener() {
      @Override
      public void buttonClick(Button.ClickEvent clickEvent) {
        commit();
        configureEditable(false);
      }
    });

    cancel = new Button("Cancel", new Button.ClickListener() {
      @Override
      public void buttonClick(Button.ClickEvent clickEvent) {
        discard();
        configureEditable(false);
      }
    });

    cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
    save.setClickShortcut(ShortcutAction.KeyCode.ENTER);


    HorizontalLayout components = new HorizontalLayout(edit, save, cancel);
    components.setSpacing(true);
    components.setMargin(true);
    return components;
  }

  /**
   * Configure for edit (or not)
   * If we're editable
   * a) Make the table editable
   * b) Hide the Edit Button, and enable the Save/Cancel buttons
   * c) Set focus on the first field
   * else
   * a) Make the table *not* editable
   * b) Hide the save/cancel buttons, and show the edit button

   * @param editable
   */
  protected void configureEditable(boolean editable) {
    table.setSelectable(!editable);
    table.setEditable(editable);
    save.setVisible(editable);
    cancel.setVisible(editable);
    edit.setVisible(!editable);
    if (editable && !fields.isEmpty()) {
      fields.get(0).focus();
    }
  }


  /**
   * Commit all field edits.
   *
   * NB: Should handle validation problems here
   */
  protected void commit() {
    for (Field field : fields) {
      field.commit();
    }
  }

  /**
   * Discard any field edits
   */
  protected void discard() {
    for (Field field : fields) {
      field.discard();
    }
  }

  private Table createTable(IndexedContainer container) {
    final Table table = new Table();
    table.setTableFieldFactory(new DefaultFieldFactory() {
      @Override
      public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
        // If the itemId isn't the currently selected item in the table, don't generate a field
        // i.e. it's not editable
        if (!itemId.equals(table.getValue())) {
          return null;
        }

        // Let the default factory build the field : you can and probably should do far more
        // Logic here
        Field field = super.createField(container, itemId, propertyId, uiContext);


        // Make the field buffered - this lets us discard the value
        field.setBuffered(true);

        // Let's keep track of all of the attached fields
        field.addAttachListener(new AttachListener() {
          @Override
          public void attach(AttachEvent attachEvent) {
            fields.add((Field) attachEvent.getConnector());
          }
        });
        field.addDetachListener(new DetachListener() {
          @Override
          public void detach(DetachEvent event) {
            fields.remove((Field) event.getConnector());
          }
        });

        return field;
      }
    });

    // Double click on a row: make it editable
    table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
      @Override
      public void itemClick(ItemClickEvent itemClickEvent) {

        if (itemClickEvent.isDoubleClick() && !table.isEditable()) {
          table.setValue(itemClickEvent.getItemId());
          configureEditable(true);
        }
      }
    });
    table.setMultiSelect(false);
    table.setContainerDataSource(container);


    table.setSizeFull();
    table.setVisibleColumns(new Object[]{"name", "number"});
    table.setColumnExpandRatio("number", 1f);

    return table;
  }


  private IndexedContainer createContainer() {
    IndexedContainer container = new IndexedContainer();
    container.addContainerProperty("name", String.class, null);
    container.addContainerProperty("number", Integer.class, null);

    addItem(container, "Bob", 10);
    addItem(container, "Harry", 1);
    addItem(container, "Margaret", 0);
    addItem(container, "Glenda", 22);
    addItem(container, "Boris", 0);
    addItem(container, "Jessica", 24);
    return container;
  }

  private void addItem(IndexedContainer container, String name, int number) {
    Object itemId = container.addItem();
    container.getItem(itemId).getItemProperty("name").setValue(name);
    container.getItem(itemId).getItemProperty("number").setValue(number);
  }
}