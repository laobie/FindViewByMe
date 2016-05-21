import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.NewVirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.logging.Log;
import org.apache.http.util.TextUtils;
import org.xml.sax.SAXException;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jaeger
 * 15/11/25
 */
public class FindViewByMeAction extends AnAction {
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
        findViewDialog.setTitle("FindViewByMe");
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
        try {
            viewSaxHandler.createViewList(contentStr);
            viewParts = viewSaxHandler.getViewPartList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * FindViewByMe 对话框回调
     */
    private FindViewDialog.onClickListener onClickListener = new FindViewDialog.onClickListener() {
        @Override
        public void onAddRootView() {
            generateCode();
        }

        @Override
        public void onCopyCode() {
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
            switchViewName(isAddM);
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
     * 切换控件名称
     *
     * @param isAddM 是否添加"m"
     */
    private void switchViewName(boolean isAddM) {
        if (isAddM) {
            for (ViewPart viewPart : viewParts) {
                viewPart.addM2Name();
            }
        } else {
            for (ViewPart viewPart : viewParts) {
                viewPart.resetName();
            }
        }
        updateTable();
    }

    /**
     * 生成FindViewById代码
     */
    private void generateCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                stringBuilder.append(viewPart.getDeclareString(isViewHolder));
            }
        }
        stringBuilder.append("\n");
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                if (isViewHolder) {
                    stringBuilder.append(viewPart.getFindViewStringForViewHolder("convertView"));
                } else if (isAddRootView && !TextUtils.isEmpty(findViewDialog.getRootView())) {
                    stringBuilder.append(viewPart.getFindViewStringWithRootView(findViewDialog.getRootView()));
                } else {
                    stringBuilder.append(viewPart.getFindViewString());
                }
            }
        }
        findViewDialog.setTextCode(stringBuilder.toString());
    }

    /**
     * 更新 View 表格
     */
    public void updateTable() {
        if (viewParts == null || viewParts.size() == 0) {
            return;
        }
        int size = viewParts.size();
        String[] headers = {"selected", "type", "id", "name"};
        Object[][] cellData = new Object[size][4];
        for (int i = 0; i < size; i++) {
            ViewPart viewPart = viewParts.get(i);
            for (int j = 0; j < 4; j++) {
                switch (j) {
                    case 0:
                        cellData[i][j] = viewPart.isSelected();
                        break;
                    case 1:
                        cellData[i][j] = viewPart.getType();
                        break;
                    case 2:
                        cellData[i][j] = viewPart.getId();
                        break;
                    case 3:
                        cellData[i][j] = viewPart.getName();
                        break;
                }
            }
        }
        tableModel = new DefaultTableModel(cellData, headers) {
            final Class[] typeArray = {Boolean.class, Object.class, Object.class, Object.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @SuppressWarnings("rawtypes")
            public Class getColumnClass(int column) {
                return typeArray[column];
            }
        };

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent event) {
                int row = event.getFirstRow();
                int column = event.getColumn();
                if (column == 0) {
                    Boolean isSelected = (Boolean) tableModel.getValueAt(row, column);
                    viewSaxHandler.getViewPartList().get(row).setSelected(isSelected);
                    FindViewByMeAction.this.generateCode();
                }
            }
        });
        findViewDialog.setModel(tableModel);
        generateCode();
    }

}
