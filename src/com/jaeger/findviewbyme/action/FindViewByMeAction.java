package com.jaeger.findviewbyme.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.jaeger.findviewbyme.model.PropertiesKey;
import com.jaeger.findviewbyme.model.ViewPart;
import com.jaeger.findviewbyme.util.ActionUtil;
import com.jaeger.findviewbyme.util.CodeWriter;
import com.jaeger.findviewbyme.util.Utils;
import com.jaeger.findviewbyme.util.ViewSaxHandler;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;

/**
 * Created by Jaeger
 * 15/11/25
 */
public class FindViewByMeAction extends BaseGenerateAction {
    private boolean isAddRootView;
    private boolean isViewHolder;
    private boolean isTarget26;
    private String rootViewStr;

    private ViewSaxHandler viewSaxHandler;
    private FindViewDialog findViewDialog;
    private List<ViewPart> viewParts;

    private DefaultTableModel tableModel;

    private PsiFile psiFile;
    private Editor editor;


    public FindViewByMeAction() {
        super(null);
    }

    public FindViewByMeAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    /**
     * 启动时触发
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        isAddRootView = false;
        isViewHolder = false;
        isTarget26 = false;
        viewSaxHandler = new ViewSaxHandler();
        if (findViewDialog == null) {
            findViewDialog = new FindViewDialog();
        }
        getViewList(anActionEvent);
        ActionUtil.switchAddM(viewParts, PropertiesComponent.getInstance().getBoolean(PropertiesKey.SAVE_ADD_M_ACTION, false));
        isTarget26 = PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_TARGET_26, false);
        updateTable();
        findViewDialog.setTitle("FindViewByMe");
        findViewDialog.btnCopyCode.setText("OK");
        findViewDialog.setOnClickListener(onClickListener);
        findViewDialog.pack();
        findViewDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(anActionEvent.getProject()));
        findViewDialog.setVisible(true);
    }

    /**
     * get views list
     *
     * @param event 触发事件
     */
    private void getViewList(AnActionEvent event) {
        psiFile = event.getData(LangDataKeys.PSI_FILE);
        editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, psiFile);
        if (psiFile == null || editor == null) {
            return;
        }
        String contentStr = psiFile.getText();
        if (layout != null) {
            contentStr = layout.getText();
        }
        if (psiFile.getParent() != null) {
            String javaPath = psiFile.getContainingDirectory().toString().replace("PsiDirectory:", "");
            String javaPathKey = "src" + File.separator + "main" + File.separator + "java";
            int indexOf = javaPath.indexOf(javaPathKey);
            String layoutPath = "";
            if (indexOf != -1) {
                layoutPath = javaPath.substring(0, indexOf) + "src" + File.separator + "main" + File.separator + "res" + File.separator + "layout";
            }
            viewSaxHandler.setLayoutPath(layoutPath);
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
            new CodeWriter(psiFile, getTargetClass(editor, psiFile), viewParts, isViewHolder,isTarget26, isAddRootView, rootViewStr, editor).execute();
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
            generateCode();
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
        public void onSwitchIsTarget26(boolean target26) {
            isTarget26 = target26;
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
        rootViewStr = findViewDialog.getRootView();
        findViewDialog.setTextCode(ActionUtil.generateCode(viewParts, isViewHolder,isTarget26, isAddRootView, rootViewStr));
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
                FindViewByMeAction.this.generateCode();
            }
        }
    };

}
