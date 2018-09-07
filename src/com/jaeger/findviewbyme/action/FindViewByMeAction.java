package com.jaeger.findviewbyme.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

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
import com.jaeger.findviewbyme.util.*;

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
    private int currentListSelect = 0;//当前map中的位置
    private String oldKeywrod = "";//上次搜索的关键字
    private Map<Integer, String> keywrodArr;//搜索出来匹配的 map key 为在总数据中所在的位置. value 为name
    private boolean isMatch = false;//当前是否匹配上
    private ArrayList<Integer> keys;

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
        public void onSearch(String string) {
            int i = selectWrod(string);
            findViewDialog.setSelect(i);
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
        public void onSwitchIsKotlin(boolean isKotlin) {

        }

        @Override
        public void onSwitchExtensions(boolean isExtensions) {

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
     * 搜索关键字的位置
     *
     * @param wrod
     * @return
     */
    public int selectWrod(String wrod) {

        //判断搜索的关键字和上一是否和上次搜索的一致
        if (oldKeywrod.equals(wrod) && isMatch) {

            keywrodArr.get(keys.get(currentListSelect));
            String value = keywrodArr.get(keys.get(currentListSelect));
            if (!TextUtils.isEmpty(value)) {
                currentListSelect++;
                if (currentListSelect >= keys.size()) {
                    currentListSelect = 0;
                }
//                System.out.println("keys size " + keys.size());
//                System.out.println("currentListSelect " + currentListSelect);

                oldKeywrod = wrod;
//                System.out.println("oldKeywrod.equals(wrod) && isMatch " + keys.get(currentListSelect));
//                System.out.println("值 =   " + value);
                return keys.get(currentListSelect);
            }
        } else {
            getSearchParts(wrod);
            if (keys != null && keys.size() > 0) {
//                System.out.println("getSearchParts " + keys.get(currentListSelect));
                return keys.get(currentListSelect);
            }
            return 0;
        }
        return 0;
    }

    /**
     * 根据关键字搜索
     *
     * @param wrod
     */
    public void getSearchParts(String wrod) {
        boolean temp = true;
        keys = null;
        keywrodArr = null;
        keywrodArr = new HashMap();
        keys = new ArrayList<Integer>();
        for (int i = 0; i < viewParts.size(); i++) {
            ViewPart viewPart = viewParts.get(i);
            int item = Utils.bruteFore(viewPart.getName(), wrod);
            if (item != -1) {
                //匹配上了
                isMatch = true;
                if (temp) {
                    temp = false;
                    oldKeywrod = wrod;
                }
                keys.add(i);
                keywrodArr.put(i, viewPart.getName());
            }
        }
    }
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
