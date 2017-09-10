package it.sijmen.cluino.ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

public class StringListPanel extends FileListPanel {
    public StringListPanel(String columnTitle, String emptyText, Project project) {
        super(columnTitle, emptyText, project);
    }

    @Override
    protected void showEditorDialog(@Nullable String originalFile, Consumer<String> finished) {
        new StringEditorDialog(project, "Edit", originalFile, finished).show();
    }
}
