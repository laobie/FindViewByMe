package com.jaeger.findviewbyme.action;

import com.intellij.ide.util.PropertiesComponent;
import com.jaeger.findviewbyme.model.PropertiesKey;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class FindViewDialog extends JDialog {
    private JPanel contentPane;
    public JButton btnCopyCode;
    public JButton btnClose;
    public JCheckBox chbAddRootView;
    public JTextField textRootView;
    public JTextArea textCode;
    public JCheckBox chbAddM;
    public JTable tableViews;
    public JButton btnSelectAll;
    public JButton btnSelectNone;
    public JButton btnNegativeSelect;
    private JCheckBox chbIsViewHolder;
    private JCheckBox chbIsTarget26;

    private JTextField editSearch;
    private JButton btnSearch;
    private JCheckBox chbIsKotlin;
    private JCheckBox chbIsExtensions;
    private onClickListener onClickListener;

    public FindViewDialog() {
        setContentPane(contentPane);
        setModal(true);
        textRootView.setEnabled(false);

        initStatus();

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSearch(getSerch());
                }

            }
        });
        btnCopyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onOK();
                }
                onCancel();
            }
        });

        textRootView.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (onClickListener != null) {
                    onClickListener.onUpdateRootView();
                }

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (onClickListener != null) {
                    onClickListener.onUpdateRootView();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (onClickListener != null) {
                    onClickListener.onUpdateRootView();
                }
            }
        });

        chbAddM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchAddM(chbAddM.isSelected());
                    PropertiesComponent.getInstance().setValue(PropertiesKey.SAVE_ADD_M_ACTION, chbAddM.isSelected());
                }
            }
        });

        chbIsViewHolder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchIsViewHolder(chbIsViewHolder.isSelected());
                }
            }
        });

        chbIsTarget26.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchIsTarget26(chbIsTarget26.isSelected());
                    PropertiesComponent.getInstance().setValue(PropertiesKey.IS_TARGET_26, chbIsTarget26.isSelected());
                }
            }
        });

        chbIsKotlin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchIsKotlin(chbIsKotlin.isSelected());
                    PropertiesComponent.getInstance().setValue(PropertiesKey.IS_KT, chbIsKotlin.isSelected());
                }
            }
        });
        chbIsExtensions.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchExtensions(chbIsExtensions.isSelected());
                    PropertiesComponent.getInstance().setValue(PropertiesKey.IS_KT_ETX, chbIsExtensions.isSelected());
                }
            }
        });

        chbAddRootView.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isAdd = chbAddRootView.isSelected();
                if (onClickListener != null) {
                    onClickListener.onSwitchAddRootView(isAdd);
                }
                textRootView.setEnabled(isAdd);
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindViewDialog.this.onCancel();
            }
        });
        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSelectAll();
                }
            }
        });

        btnSelectNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSelectNone();
                }
            }
        });

        btnNegativeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onNegativeSelect();
                }
            }
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   FindViewDialog.this.onCancel();
                                               }
                                           },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onOK();
                }
                FindViewDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void initStatus() {
        chbAddM.setSelected(PropertiesComponent.getInstance().getBoolean(PropertiesKey.SAVE_ADD_M_ACTION, false));
        chbIsTarget26.setSelected(PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_TARGET_26, false));
        chbIsKotlin.setSelected(PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_KT, false));
        chbIsExtensions.setSelected(PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_KT_ETX, false));
    }

    private void onCancel() {
        dispose();
        if (onClickListener != null) {
            onClickListener.onFinish();
        }
    }

    public void setTextCode(String codeStr) {
        textCode.setText(codeStr);
    }

    public void setSelect(int position) {
//        System.out.println("开始的位置"+position);
        tableViews.grabFocus();
        tableViews.changeSelection(position, 1, false, false);
    }

    public interface onClickListener {
        void onUpdateRootView();

        void onOK();

        void onSelectAll();

        void onSearch(String string);

        void onSelectNone();

        void onNegativeSelect();

        void onSwitchAddRootView(boolean isAddRootView);

        void onSwitchAddM(boolean addM);

        void onSwitchIsViewHolder(boolean isViewHolder);

        void onSwitchIsKotlin(boolean isKotlin);

        void onSwitchExtensions(boolean isExtensions);

        void onSwitchIsTarget26(boolean target26);


        void onFinish();
    }

    public void setOnClickListener(FindViewDialog.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setModel(DefaultTableModel model) {
        tableViews.setModel(model);
        tableViews.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    public String getRootView() {
        return textRootView.getText().trim();
    }

    public String getSerch() {
        return editSearch.getText().trim();
    }
}
