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

public class ElmTestRunnerTaskConfigurator extends AbstractTaskConfigurator implements TaskConfigurator {

    public static final String ELM_MAKE_LOCATION = "elmMakeLocation";
    public static final String ELM_TEST_LOCATION = "elmTestLocation";

    private DefaultI18nBean textProvider;

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context) {
        super.populateContextForCreate(context);
        context.put(ELM_MAKE_LOCATION, "/node_modules/.bin/elm-make");
        context.put(ELM_TEST_LOCATION, "./node_modules/.bin/elm-test");
        context.put("testOutputFile", "tests/elm-stuff/test-results.json");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        context.put(ELM_MAKE_LOCATION, taskDefinition.getConfiguration().get(ELM_MAKE_LOCATION));
        context.put(ELM_TEST_LOCATION, taskDefinition.getConfiguration().get(ELM_TEST_LOCATION));
        context.put("testOutputFile", taskDefinition.getConfiguration().get("testOutputFile"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        // Validate elm-make location config exists.
        final String elmMakeLocation = params.getString(ELM_MAKE_LOCATION);
        if (StringUtils.isEmpty(elmMakeLocation)) {
            errorCollection.addError(ELM_MAKE_LOCATION, textProvider.getText("config.elmMakeLocation.error"));
        }

        // Validate elm-test location config exists.
        final String elmTestLocation = params.getString(ELM_TEST_LOCATION);
        if (StringUtils.isEmpty(elmTestLocation)) {
            errorCollection.addError(ELM_TEST_LOCATION, textProvider.getText("config.elmTestLocation.error"));
        }

        // Validate test output file config exists.
        final String testOutputFile = params.getString("testOutputFile");
        if (StringUtils.isEmpty(testOutputFile)) {
            errorCollection.addError("testOutputFile", textProvider.getText("config.testoutputfile.error"));
        }
    }

    @Override
    @NotNull
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition taskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, taskDefinition);

        config.put(ELM_MAKE_LOCATION, params.getString(ELM_MAKE_LOCATION));
        config.put(ELM_TEST_LOCATION, params.getString(ELM_TEST_LOCATION));
        config.put("testOutputFile", params.getString("testOutputFile"));

        return config;
    }

}
