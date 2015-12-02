import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class FindViewDialog extends JDialog {
    private JPanel contentPane;
    public JButton btnCopyCode;
    public JButton btnClose;
    public JButton btnAddRootView;
    public JCheckBox chbAddRootView;
    public JTextField textRootView;
    public JTextArea textCode;
    public JCheckBox chbAddM;
    public JTable tableViews;
    public JButton btnSelectAll;
    public JButton btnSelectNone;
    public JButton btnNegativeSelect;
    private JCheckBox chbIsViewHolder;
    private onClickListener onClickListener;

    public FindViewDialog() {
        setContentPane(contentPane);
        setModal(true);
//        getRootPane().setDefaultButton(btnCopyCode);
        textRootView.setEnabled(false);
        btnAddRootView.setEnabled(false);

        btnCopyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onCopyCode();
                }
                onCancel();
            }
        });

        btnAddRootView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onAddRootView();
                }
            }
        });

        chbAddM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchAddM(chbAddM.isSelected());
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

        chbAddRootView.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isAdd = chbAddRootView.isSelected();
                if (onClickListener != null) {
                    onClickListener.onSwitchAddRootView(isAdd);
                }
                textRootView.setEnabled(isAdd);
                btnAddRootView.setEnabled(isAdd);
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

    }

    private void onCancel() {
        dispose();
        if (onClickListener!=null){
            onClickListener.onFinish();
        }
    }

    public void setTextCode(String codeStr) {
        textCode.setText(codeStr);
    }

    public interface onClickListener {
        void onAddRootView();

        void onCopyCode();

        void onSelectAll();

        void onSelectNone();

        void onNegativeSelect();

        void onSwitchAddRootView(boolean isAddRootView);

        void onSwitchAddM(boolean addM);

        void onSwitchIsViewHolder(boolean isViewHolder);

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
        return textRootView.getText();
    }
}
