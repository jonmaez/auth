package com.ahsanb.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ahsanb.auth.config.SwaggerConfig;



@SpringBootTest(classes = { AuthApplication.class, SwaggerConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("enabled")
public class SwaggerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${springfox.documentation.swagger.v2.path}")
    private String apiDocPath;

    @Test
    public void createSpringfoxSwaggerJson() throws Exception {
        // only publish swagger definition for inventories
        String outputDir = System.getProperty("io.springfox.staticdocs.outputDir", "./build/swagger");
        MvcResult mvcResult = mockMvc.perform(get(apiDocPath + "?group=users")
					                 .accept(MediaType.APPLICATION_JSON))
					                 .andExpect(status().isOk())
					                 .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String swaggerJson = response.getContentAsString();
        Files.createDirectories(Paths.get(outputDir));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
            writer.write(swaggerJson);
        }
    }

}
