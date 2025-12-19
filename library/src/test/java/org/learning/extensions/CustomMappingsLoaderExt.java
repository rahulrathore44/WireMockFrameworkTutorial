package org.learning.extensions;

import com.github.tomakehurst.wiremock.extension.MappingsLoaderExtension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CustomMappingsLoaderExt implements MappingsLoaderExtension {

    private final String rootDir;

    public CustomMappingsLoaderExt(String rootDir) {
        this.rootDir = rootDir;
    }


    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void loadMappingsInto(StubMappings stubMappings) {
        var fileExt = new String[]{"json"};
        // Filter out all the JSON files from the root dir
        var mappings = FileUtils.iterateFiles(new File(rootDir), fileExt, false);

        // read the content and load them using stubMappings
        mappings.forEachRemaining((file -> {
            try {
                var jsonContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                // Add/load them using the stubMappings object
                var mapping = StubMapping.buildFrom(jsonContent);
                stubMappings.addMapping(mapping);
            } catch (Exception e) {
                System.out.println("Unable to load the file content: " + file.getName());
            }
        }));

    }
}
