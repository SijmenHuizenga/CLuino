package it.sijmen.cluino.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.Consumer;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import it.sijmen.cluino.Util;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FileListPanel {

    private static final int COL = 0;

    private final String columnTitle;
    private final String emptyText;

    private Project project;
    private ListTableModel<String> model;
    private TableView<String> table;

    private JPanel panel;

    private List<String> items;

    public FileListPanel(String columnTitle, String emptyText, Project project) {
        this.columnTitle = columnTitle;
        this.emptyText = emptyText;
        this.project = project;
        items = new ArrayList<>();
        buildPanel();
    }

    private void buildPanel() {
        ColumnInfo column = new PrimaryColumnInfo(columnTitle);

        model = new ListTableModel<>(new ColumnInfo[]{column}, items);
        table = new TableView<>(model);
        table.getEmptyText().setText(emptyText);

        table.setColumnSelectionAllowed(false);
        table.setShowGrid(false);
        table.setDragEnabled(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        table.removeEditor();

        this.panel = ToolbarDecorator.createDecorator(table)
                .setAddAction(addAction)
                .setRemoveAction(removeAction)
                .setEditAction(editAction)
                .setMinimumSize(new Dimension(50, 100))
                .setPreferredSize(new Dimension(350, 100))
                .setVisibleRowCount(3)
                .disableUpDownActions()
                .createPanel();
    }

    private AnActionButtonRunnable addAction = button -> showEditorDialog(null, file -> {
        items.add(file);
        model.fireTableCellUpdated(model.getRowCount()-1, COL);
    });

    private AnActionButtonRunnable editAction = button -> {
        int editingRow = table.getSelectedRow();
        if(editingRow == -1)
            return;

        showEditorDialog(table.getSelectedObject(), file -> {
            items.set(editingRow, file);
            model.fireTableCellUpdated(editingRow, COL);
        });
    };

    private AnActionButtonRunnable removeAction = button -> {
        final int[] selected = table.getSelectedRows();

        for(int i = selected.length-1; i >= 0; i--) {
            items.remove(selected[i]);
            model.fireTableDataChanged();
        }
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
            IdeFocusManager.getGlobalInstance().requestFocus(table, true);
        });
    };

    private void showEditorDialog(@Nullable String originalFile, Consumer<String> finished) {
        FileChooser.chooseFile(
                new FileChooserDescriptor(false, true, false, false, false, false),
                this.project, Util.getFileOrNull(originalFile),
                newFile -> {
                    if(newFile != null && newFile.exists())
                        finished.consume(newFile.getPath());
                }
        );
    }

    public void setItems(List<String> items){
        this.items = new ArrayList<>(items);
        this.model.setItems(this.items);
        this.model.fireTableDataChanged();
    }

    public List<String> getItems() {
        return this.items;
    }

    private class PrimaryColumnInfo extends ColumnInfo<String, String> {
        PrimaryColumnInfo(String title) {
            super(title);
        }

        @Nullable
        @Override
        public String valueOf(String s) {
            return s;
        }

        @Override
        public boolean isCellEditable(String s) {
            return false;
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}
