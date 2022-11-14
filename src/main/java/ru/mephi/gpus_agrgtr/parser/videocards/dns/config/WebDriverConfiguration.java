package ru.mephi.gpus_agrgtr.parser.videocards.dns.config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfiguration {
    private static final String NAME_DRIVER = "webdriver.edge.driver";
    private static final String PATH_DRIVER = "src/main/java/ru/mephi/gpus_agrgtr/parser/videocards/dns/resources/msedgedriver1.exe";
    @Bean
    public WebDriver webDriverBean(){
        System.setProperty(NAME_DRIVER,PATH_DRIVER);
        EdgeOptions edgeOptions = new EdgeOptions()
                .addArguments("--headless")
                .addArguments("--disable-dev-shm-usage")
                .addArguments("--start-maximized");
        edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        return new EdgeDriver( edgeOptions);
    }
}
