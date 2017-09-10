package it.sijmen.cluino.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StringEditorDialog extends DialogWrapper {

    private JBTextField textField;
    private final Consumer<String> finished;

    protected StringEditorDialog(@Nullable Project project, String title, String initialValue, Consumer<String> finished) {
        super(project);
        textField = new JBTextField(initialValue);
        this.finished = finished;
        init();
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return textField;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return textField;
    }

    @Override
    protected void doOKAction() {
        String text = textField.getText();
        if(text != null && !text.isEmpty())
            finished.consume(text);
        super.doOKAction();
    }
}
