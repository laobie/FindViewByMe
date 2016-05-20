import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.sun.deploy.uitoolkit.impl.text.TextUIFactory;

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

    protected CodeWriter(PsiFile psiFile, PsiClass clazz, List<ViewPart> viewPartList) {
        super(clazz.getProject(), "");
        this.psiFile = psiFile;
        mProject = clazz.getProject();
        mClass = clazz;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        this.viewPartList = viewPartList;
    }

    @Override
    protected void run() throws Throwable {
        StringBuilder methodBuild = new StringBuilder("private void initView(){");
        for (ViewPart viewPart : viewPartList) {
            if (!viewPart.isSelected()) {
                continue;
            }
            methodBuild.append(viewPart.getFindViewString());
            mClass.add(mFactory.createFieldFromText(viewPart.getDeclareString(false), mClass));
        }
        methodBuild.append("}");
        mClass.add(mFactory.createMethodFromText(methodBuild.toString(), mClass));

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(psiFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }
}
