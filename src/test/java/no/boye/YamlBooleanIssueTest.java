package no.boye;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class YamlBooleanIssueTest {
    private final boolean minimizeQuotes;

    public YamlBooleanIssueTest(boolean minimizeQuotes) {
        this.minimizeQuotes = minimizeQuotes;
    }

    @Parameterized.Parameters(name = "minimizeQuotes={0}")
    public static Iterable<?> data() {
        return Arrays.asList(true, false);
    }

    @Test
    public void yaml_boolean_string_value_should_marshal_correctly() throws Exception {
        EnvVar envVar = new EnvVarBuilder()
                .withName("DATABASE")
                .withValue("off")
                .build();

        Writer writer = new StringWriter();
        writeValue(writer, envVar);
        String valueString = writer.toString();
        System.out.println(valueString);
        Map<String, Object> genericEnvVar = readValue(valueString);

        assertThat(genericEnvVar)
                .containsEntry("value", "off");
    }

    private void writeValue(Writer writer, Object value) throws IOException {
        YAMLFactory yamlFactory = new YAMLFactory();
        if (minimizeQuotes) {
            yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        }
        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        mapper.writeValue(writer, value);
    }

    private Map<String, Object> readValue(String string) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        return mapper.readValue(string, typeRef);
    }
}
