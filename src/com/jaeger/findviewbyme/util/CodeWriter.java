package com.jaeger.findviewbyme.util;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;
import com.jaeger.findviewbyme.model.ViewPart;
import org.jetbrains.annotations.Nullable;


import java.util.List;

/**
 * Created by pengwei on 16/5/20.
 */
public class CodeWriter extends WriteCommandAction.Simple {

    private List<ViewPart> viewPartList;
    protected Project mProject;
    protected PsiClass mClass;
    protected PsiElementFactory mFactory;
    private PsiFile psiFile;
    private Editor mEditor;

    private boolean isAddRootView;
    private boolean isViewHolder;
    private boolean isTarget26;
    private String rootViewStr;

    public CodeWriter(PsiFile psiFile, PsiClass clazz, List<ViewPart> viewPartList, boolean isViewHolder,boolean isTarget26, boolean isAddRootView, String rootViewStr, Editor editor) {
        super(clazz.getProject(), "");
        this.psiFile = psiFile;
        mProject = clazz.getProject();
        mClass = clazz;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        mEditor = editor;
        this.viewPartList = viewPartList;
        this.isAddRootView = isAddRootView;
        this.isViewHolder = isViewHolder;
        this.isTarget26 = isTarget26;
        this.rootViewStr = rootViewStr;
    }

    /**
     * judge field exists
     *
     * @param part
     * @return
     */
    private boolean fieldExist(ViewPart part) {
        PsiField[] fields = mClass.getAllFields();
        for (PsiField field : fields) {
            if (field.getName().equals(part.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get initView method
     *
     * @return
     */
    private PsiMethod getInitView() {
        PsiMethod[] methods = mClass.findMethodsByName("initView", true);
        for (PsiMethod method : methods) {
            if (method.getReturnType().equals(PsiType.VOID)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Add the initView() after onCreate()
     *
     * @param rootViewStr
     */
    private void addInitViewAfterOnCreate(@Nullable String rootViewStr) {
        String initViewStatement = getInitViewStatementAsString(rootViewStr);
        PsiMethod createMethod = mClass.findMethodsByName("onCreate", false)[0];
        for (PsiStatement statement : createMethod.getBody().getStatements()) {
            if (statement.getText().equals(initViewStatement)) {
                return;
            }
        }
        createMethod.getBody().add(mFactory.createStatementFromText(initViewStatement, mClass));
    }

    /**
     * Add the {@code initView} method after onCreateView()
     *
     * @param rootViewStr
     */
    private void addInitViewAfterOnCreateView(@Nullable String rootViewStr) {
        String initViewStatement = getInitViewStatementAsString(rootViewStr);
        PsiMethod createMethod = mClass.findMethodsByName("onCreateView", false)[0];
        for (PsiStatement statement : createMethod.getBody().getStatements()) {
            if (statement.getText().equals(initViewStatement)) {
                return;
            }
        }
        PsiStatement inflaterStatement = findInflaterStatement(createMethod.getBody().getStatements());
        createMethod.getBody().addAfter(mFactory.createStatementFromText(initViewStatement, mClass), inflaterStatement);
    }

    /**
     * Creates a string representing the initView method.
     * <p>
     * If {@code rootViewStr} is provided then it will generate a method with
     * {@code rootViewStr} as a param. A no-params method in case it's not provided.
     *
     * @param rootViewStr the name of root view
     * @return the method to append
     */
    private String getInitViewStatementAsString(@Nullable String rootViewStr) {
        String initViewStatement = "initView();";
        if (!TextUtils.isEmpty(rootViewStr)) {
            initViewStatement = "initView(" + rootViewStr + ");";
        }
        return initViewStatement;
    }

    private PsiStatement findInflaterStatement(PsiStatement[] psiStatements) {
        for (PsiStatement psiStatement : psiStatements) {
            if (psiStatement.getText().contains(".inflate(")) {
                return psiStatement;
            }
        }
        return null;
    }

    @Override
    protected void run() throws Throwable {
        int fieldCount = 0;
        PsiMethod initViewMethod = getInitView();
        StringBuilder methodBuild;
        if (isAddRootView && !TextUtils.isEmpty(rootViewStr)) {
            methodBuild = new StringBuilder("private void initView(View " + rootViewStr + ") {");
        } else {
            methodBuild = new StringBuilder("private void initView() {");
        }
        for (ViewPart viewPart : viewPartList) {
            if (!viewPart.isSelected() || fieldExist(viewPart)) {
                continue;
            }
            mClass.add(mFactory.createFieldFromText(viewPart.getDeclareString(false, false), mClass));
            if (initViewMethod != null) {
                initViewMethod.getBody().add(mFactory.createStatementFromText(viewPart.getFindViewString(isTarget26), mClass));
            } else {
                if (isViewHolder) {
                    methodBuild.append(viewPart.getFindViewStringForViewHolder("convertView",isTarget26));
                } else if (isAddRootView && !TextUtils.isEmpty(rootViewStr)) {
                    methodBuild.append(viewPart.getFindViewStringWithRootView(rootViewStr,isTarget26));
                } else {
                    methodBuild.append(viewPart.getFindViewString(isTarget26));
                }
                fieldCount++;
            }
        }
        methodBuild.append("}");
        if (fieldCount > 0) {
            mClass.add(mFactory.createMethodFromText(methodBuild.toString(), mClass));
        }
        addInit(rootViewStr);

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(psiFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    private void addInit(@Nullable String rootViewStr) {
        PsiClass activityClass = JavaPsiFacade.getInstance(mProject).findClass(
                "android.app.Activity", new EverythingGlobalScope(mProject));
        PsiClass fragmentClass = JavaPsiFacade.getInstance(mProject).findClass(
                "android.app.Fragment", new EverythingGlobalScope(mProject));
        PsiClass supportFragmentClass = JavaPsiFacade.getInstance(mProject).findClass(
                "android.support.v4.app.Fragment", new EverythingGlobalScope(mProject));

        // Check for Activity class
        if (activityClass != null && mClass.isInheritor(activityClass, true)) {
            addInitViewAfterOnCreate(rootViewStr);
            // Check for Fragment class
        } else if ((fragmentClass != null && mClass.isInheritor(fragmentClass, true)) || (supportFragmentClass != null && mClass.isInheritor(supportFragmentClass, true))) {
            addInitViewAfterOnCreateView(rootViewStr);
        } else {
            Utils.showInfoNotification(mEditor.getProject(), "Add " + getInitViewStatementAsString(rootViewStr) + " where relevant!");
        }
    }
}
