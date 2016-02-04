import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Jaeger
 * 16/2/4.
 */
@State(name = "ConfigComponent", storages = {@Storage(file = "$WORKSPACE_FILE$"
)})
public class ConfigComponent implements ApplicationComponent, Configurable ,JDOMExternalizable, PersistentStateComponent<ConfigComponent> {
    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "配置界面";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return new FindViewByMeConfig().getRootComponent();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }

    @Nls
    @Override
    public String getDisplayName() {
        return "FindViewByMe Setting";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "时候实话实说";
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {

    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {

    }

    public void loadState(ConfigComponent state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public ConfigComponent getState() {
        return this;
    }
}
