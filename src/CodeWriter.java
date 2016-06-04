import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.apache.http.util.TextUtils;

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

    private boolean isAddRootView;
    private boolean isViewHolder;
    private String rootViewStr;

    protected CodeWriter(PsiFile psiFile, PsiClass clazz, List<ViewPart> viewPartList, boolean isViewHolder, boolean isAddRootView, String rootViewStr) {
        super(clazz.getProject(), "");
        this.psiFile = psiFile;
        mProject = clazz.getProject();
        mClass = clazz;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        this.viewPartList = viewPartList;
        this.isAddRootView = isAddRootView;
        this.isViewHolder = isViewHolder;
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
     */
    private void addInitViewAfterOnCreate() {
        String initViewStatement = "initView();";
        PsiMethod createMethod = mClass.findMethodsByName("onCreate", false)[0];
        for (PsiStatement statement : createMethod.getBody().getStatements()) {
            if (statement.getText().equals(initViewStatement)) {
                return;
            }
        }
        createMethod.getBody().add(mFactory.createStatementFromText(initViewStatement, mClass));
    }

    @Override
    protected void run() throws Throwable {
        int fieldCount = 0;
        PsiMethod initViewMethod = getInitView();
        StringBuilder methodBuild = new StringBuilder("private void initView() {");
        for (ViewPart viewPart : viewPartList) {
            if (!viewPart.isSelected() || fieldExist(viewPart)) {
                continue;
            }
            mClass.add(mFactory.createFieldFromText(viewPart.getDeclareString(false, false), mClass));
            if (initViewMethod != null) {
                initViewMethod.getBody().add(mFactory.createStatementFromText(viewPart.getFindViewString(), mClass));
            } else {
                if (isViewHolder) {
                    methodBuild.append(viewPart.getFindViewStringForViewHolder("convertView"));
                } else if (isAddRootView && !TextUtils.isEmpty(rootViewStr)) {
                    methodBuild.append(viewPart.getFindViewStringWithRootView(rootViewStr));
                } else {
                    methodBuild.append(viewPart.getFindViewString());
                }
                fieldCount++;
            }
        }
        methodBuild.append("}");
        if (fieldCount > 0) {
            mClass.add(mFactory.createMethodFromText(methodBuild.toString(), mClass));
        }
        addInitViewAfterOnCreate();

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(psiFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }
}
