import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.PopupView;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.sun.demo.jvmti.hprof.Tracker;

return new Table.ColumnGenerator<Tracker>() {

    @Override
    public Component generateCell(Tracker tracker) {
        PopupView popupView = componentsFactory.createComponent(PopupView.class)
        popupView.setMinimizedValue(tracker.description == null ? "" : tracker.description)

        Label label = componentsFactory.createComponent(Label.class)
        label.setValue(tracker.description)
        popupView.setPopupContent(label)

        return popupView
    }

}

