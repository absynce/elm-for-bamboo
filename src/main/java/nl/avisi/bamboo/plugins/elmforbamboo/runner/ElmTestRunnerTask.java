package nl.avisi.bamboo.plugins.elmforbamboo.runner;

import com.atlassian.bamboo.process.BambooProcessHandler;
import com.atlassian.bamboo.process.ExternalProcessViaBatchBuilder;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.StringOutputHandler;

import nl.avisi.bamboo.plugins.elmforbamboo.ElmTestRunnerTaskConfigurator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ElmTestRunnerTask implements TaskType {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElmTestRunnerTask.class);

    @NotNull
    @Override
    public TaskResult execute(@NotNull final TaskContext taskContext) {
        final String elmMakeLocation = taskContext.getConfigurationMap().get(ElmTestRunnerTaskConfigurator.ELM_MAKE_LOCATION);
        final String elmTestLocation = taskContext.getConfigurationMap().get(ElmTestRunnerTaskConfigurator.ELM_TEST_LOCATION);
        final String testFilePattern = taskContext.getConfigurationMap().get("testOutputFile");

        final TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);

        final StringOutputHandler outputHandler = new StringOutputHandler();
        final ExternalProcess process =
                new ExternalProcessViaBatchBuilder()
                        .command(Arrays.asList(taskContext.getWorkingDirectory().toString() + elmTestLocation,
                                "--compiler=" + taskContext.getWorkingDirectory().toString() + elmMakeLocation,
                                "--report=json"),
                                taskContext.getWorkingDirectory())
                        .handler(new BambooProcessHandler(outputHandler, outputHandler))
                        .build();

        process.execute();

        final String outputFile = taskContext.getWorkingDirectory() + "/" + testFilePattern;
        try (final FileWriter writer = new FileWriter(outputFile)) {
            writer.append(outputHandler.getOutput());
        } catch (IOException e) {
            LOGGER.error("Could not write file {}", outputFile, e);
            taskContext.getBuildLogger().addBuildLogEntry("Could not write test results file '" + outputFile + "'");
            builder.failed().build();
        }

        return builder.checkReturnCode(process, 0).build();
    }
}
