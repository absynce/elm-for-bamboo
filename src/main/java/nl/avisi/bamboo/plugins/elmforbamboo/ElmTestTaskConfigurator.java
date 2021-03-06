package nl.avisi.bamboo.plugins.elmforbamboo;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.i18n.DefaultI18nBean;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ElmTestTaskConfigurator extends AbstractTaskConfigurator implements TaskConfigurator {

    private DefaultI18nBean textProvider;

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context) {
        super.populateContextForCreate(context);
        context.put("testOutputFile", "tests/elm-stuff/test-results.json");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        context.put("testOutputFile", taskDefinition.getConfiguration().get("testOutputFile"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        final String testOutputFile = params.getString("testOutputFile");
        if (StringUtils.isEmpty(testOutputFile)) {
            errorCollection.addError("testOutputFile", textProvider.getText("config.testoutputfile.error"));
        }
    }

    @Override
    @NotNull
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition taskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, taskDefinition);

        config.put("testOutputFile", params.getString("testOutputFile"));

        return config;
    }

}
