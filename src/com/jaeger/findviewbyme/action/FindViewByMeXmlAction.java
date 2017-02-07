package com.jaeger.findviewbyme.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.jaeger.findviewbyme.util.ActionUtil;
import com.jaeger.findviewbyme.model.ViewPart;
import com.jaeger.findviewbyme.util.ViewSaxHandler;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

/**
 * Created by Jaeger
 * 15/11/25
 */
public class FindViewByMeXmlAction extends AnAction {
    private boolean isAddRootView;
    private boolean isViewHolder;

    private ViewSaxHandler viewSaxHandler;
    private FindViewDialog findViewDialog;
    private List<ViewPart> viewParts;

    private DefaultTableModel tableModel;

    /**
     * 启动时触发
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        isAddRootView = false;
        isViewHolder = false;
        viewSaxHandler = new ViewSaxHandler();
        if (findViewDialog == null) {
            findViewDialog = new FindViewDialog();
        }
        getViewList(anActionEvent);
        updateTable();
        findViewDialog.setTitle("FindViewByMe in XML");
        findViewDialog.setOnClickListener(onClickListener);
        findViewDialog.pack();
        findViewDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(anActionEvent.getProject()));
        findViewDialog.setVisible(true);
    }

    /**
     * 获取View列表
     *
     * @param event 触发事件
     */
    private void getViewList(AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return;
        }
        String contentStr = psiFile.getText();
        if (psiFile.getParent() != null) {
            viewSaxHandler.setLayoutPath(psiFile.getContainingDirectory().toString().replace("PsiDirectory:", ""));
            viewSaxHandler.setProject(event.getProject());
        }
        viewParts = ActionUtil.getViewPartList(viewSaxHandler, contentStr);

    }


    /**
     * FindViewByMe 对话框回调
     */
    private FindViewDialog.onClickListener onClickListener = new FindViewDialog.onClickListener() {
        @Override
        public void onUpdateRootView() {
            generateCode();
        }

        @Override
        public void onOK() {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(findViewDialog.textCode.getText());
            clip.setContents(tText, null);
        }

        @Override
        public void onSelectAll() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(true);
            }
            updateTable();
        }

        @Override
        public void onSelectNone() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(false);
            }
            updateTable();
        }

        @Override
        public void onNegativeSelect() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(!viewPart.isSelected());
            }
            updateTable();
        }

        @Override
        public void onSwitchAddRootView(boolean flag) {
            isAddRootView = flag;
        }

        @Override
        public void onSwitchAddM(boolean isAddM) {
            ActionUtil.switchAddM(viewParts, isAddM);
            updateTable();
        }

        @Override
        public void onSwitchIsViewHolder(boolean viewHolder) {
            isViewHolder = viewHolder;
            generateCode();
        }

        @Override
        public void onFinish() {
            viewParts = null;
            viewSaxHandler = null;
            findViewDialog = null;
        }
    };


    /**
     * 生成FindViewById代码
     */
    private void generateCode() {
        findViewDialog.setTextCode(ActionUtil.generateCode(viewParts, isViewHolder, isAddRootView, findViewDialog.getRootView()));
    }

    /**
     * 更新 View 表格
     */
    public void updateTable() {
        if (viewParts == null || viewParts.size() == 0) {
            return;
        }
        tableModel = ActionUtil.getTableModel(viewParts, tableModelListener);
        findViewDialog.setModel(tableModel);
        generateCode();
    }

    TableModelListener tableModelListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent event) {
            if (tableModel == null) {
                return;
            }
            int row = event.getFirstRow();
            int column = event.getColumn();
            if (column == 0) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(row, column);
                viewSaxHandler.getViewPartList().get(row).setSelected(isSelected);
                FindViewByMeXmlAction.this.generateCode();
            }
        }
    };

}
