package com.spider;
import com.spider.listener.ApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
@RestController
@ServletComponentScan(basePackageClasses = ApplicationListener.class)
@SpringBootApplication(scanBasePackages = {"com.spider.controller","com.spider.listener"})
public class ServerApplication  implements EmbeddedServletContainerCustomizer {
    private final int port = 7000;
    public static void main(String[] args) throws IOException {
        SpringApplication.run(ServerApplication.class, args);
    }

    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(port);
    }
}