package com.jaeger.findviewbyme.util;

import com.jaeger.findviewbyme.model.ViewPart;
import org.xml.sax.SAXException;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaeger
 * 16/5/28.
 */
public class ActionUtil {
    private final static String[] HEADERS = {"selected", "type", "id", "name"};

    public static List<ViewPart> getViewPartList(ViewSaxHandler viewSaxHandler, String oriContact) {
        try {
            viewSaxHandler.createViewList(oriContact);
            return viewSaxHandler.getViewPartList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<ViewPart>();
    }

    public static void switchAddM(List<ViewPart> viewParts, boolean isAddM) {
        if (isAddM) {
            for (ViewPart viewPart : viewParts) {
                viewPart.addMForName();
            }
        } else {
            for (ViewPart viewPart : viewParts) {
                viewPart.resetName();
            }
        }
    }

    public static String generateCode(List<ViewPart> viewParts, boolean isViewHolder, boolean isTarget26, boolean isAddRootView, String rootView) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                stringBuilder.append(viewPart.getDeclareString(isViewHolder, true));
            }
        }
        stringBuilder.append("\n");
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {

                if (isViewHolder) {
                    stringBuilder.append(viewPart.getFindViewStringForViewHolder("convertView", isTarget26));
                } else if (isAddRootView && !TextUtils.isEmpty(rootView)) {
                    stringBuilder.append(viewPart.getFindViewStringWithRootView(rootView, isTarget26));
                } else {
                    stringBuilder.append(viewPart.getFindViewString(isTarget26));
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String generateCode(List<ViewPart> viewParts, boolean isViewHolder, boolean isTarget26, boolean isAddRootView, String rootView, boolean isKotlin, boolean isExtensions) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isKotlin) {
            for (ViewPart viewPart : viewParts) {

                stringBuilder.append(viewPart.getFindViewStringKt(isExtensions));
            }

        } else {
            for (ViewPart viewPart : viewParts) {
                if (viewPart.isSelected()) {
                    stringBuilder.append(viewPart.getDeclareString(isViewHolder, true));
                }
            }
            stringBuilder.append("\n");
            for (ViewPart viewPart : viewParts) {
                if (viewPart.isSelected()) {

                    if (isViewHolder) {
                        stringBuilder.append(viewPart.getFindViewStringForViewHolder("convertView", isTarget26));
                    } else if (isAddRootView && !TextUtils.isEmpty(rootView)) {
                        stringBuilder.append(viewPart.getFindViewStringWithRootView(rootView, isTarget26));
                    } else {
                        stringBuilder.append(viewPart.getFindViewString(isTarget26));
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public static DefaultTableModel getTableModel(List<ViewPart> viewParts, TableModelListener tableModelListener) {
        DefaultTableModel tableModel;
        int size = viewParts.size();
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


        tableModel = new DefaultTableModel(cellData, HEADERS) {
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
        tableModel.addTableModelListener(tableModelListener);
        return tableModel;
    }
}
