package it.unimi.distanceinair.client;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Theme(value = "distanceinair", variant = Lumo.DARK)
@PWA(name = "My application", shortName = "MyApp", iconPath = "icons/icon.png" )
public class DistanceInAirClientApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(DistanceInAirClientApplication.class, args);
    }

}
