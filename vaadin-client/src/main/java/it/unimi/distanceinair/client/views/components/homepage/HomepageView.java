package it.unimi.distanceinair.client.views.components.homepage;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import it.unimi.distanceinair.client.domain.exception.WrongPasswordException;
import it.unimi.distanceinair.client.domain.model.ArrivalAirportModel;
import it.unimi.distanceinair.client.domain.model.DepartureAirportModel;
import it.unimi.distanceinair.client.service.ServerApis;
import it.unimi.distanceinair.client.util.ViewsUtils;
import it.unimi.distanceinair.client.views.components.singleviews.ArrivalView;
import it.unimi.distanceinair.client.views.components.singleviews.DepartureView;
import it.unimi.distanceinair.client.views.components.utilities.NothingFound;
import it.unimi.distanceinair.client.views.components.utilities.YourResults;
import it.unimi.distanceinair.client.views.main.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;

@PageTitle("Search flights")
@Route(value = "search", layout = MainLayout.class)
@RolesAllowed("USER")
public class HomepageView extends VerticalLayout {

    private final TextField flightCode;

    private ArrivalView arrivalView;
    private DepartureView departureView;
    private final Select<String> select;
    private final HorizontalLayout flexLayout;
   // private PasswordField passwordField;
    @Autowired
   ServerApis serverApis;

    public HomepageView() {
        ViewsUtils.forceRefreshToken();
        this.getElement().setAttribute("theme", Lumo.DARK);
      //  passwordField = new PasswordField("Password");
       // passwordField.setRequired(true);
       // passwordField.setValue("u9u6GT&j!10^");
        flightCode = new TextField("Flight code");
        flightCode.setRequired(true);
        flightCode.setPattern("(?<![\\da-z,A-Z])(?!\\d{2})([a-z,A-Z\\d]{2})\\s?(\\d{2,4})(?!\\d)");

        Button getData = new Button("Get flight data",  new Icon(VaadinIcon.SEARCH));
        getData.setIconAfterText(true);

        flexLayout = new HorizontalLayout();
        getData.getStyle().set("margin-top","35px");
        getData.getStyle().set("margin-bottom","35px");
        flexLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        select = new Select<>();
        select.setRequiredIndicatorVisible(true);
        select.setLabel("Flight type");
        select.setItems("Arrival", "Departure");
        select.setPlaceholder("Select type");
        flexLayout.getStyle().set("flex-wrap", "wrap");
        flexLayout.getStyle().set("display","flex");
        flexLayout.setSpacing(true);

        NothingFound nothingFound = new NothingFound(false);
        nothingFound.setVisible(false);
        YourResults yourResults = new YourResults();
        yourResults.setVisible(false);
        getData.addClickListener(e -> {
            if(!flightCode.getValue().matches("(?<![\\da-z,A-Z])(?!\\d{2})([a-z,A-Z\\d]{2})\\s?(\\d{2,4})(?!\\d)")) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Flight code field only accepts low letters and numbers.");
                notification.setDuration(5000);
                notification.open();
            } else if( flightCode.getValue().isBlank() || select.getValue() == null) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Fill required fields");
                notification.setDuration(5000);
                notification.open();
            } else {
            try {
                if(select.getValue().equals("Arrival")) {
                    ArrivalAirportModel arrival;
                    try {
                        ViewsUtils.forceRefreshToken();
                        arrival = serverApis.getArrival(flightCode.getValue());
                        Notification.show("Retrieved successfully");
                        arrivalView = new ArrivalView(arrival, serverApis, false);
                        arrivalView.getElement().setAttribute("theme", Lumo.DARK);
                        flexLayout.add(arrivalView);
                        setHorizontalComponentAlignment(Alignment.AUTO, arrivalView);
                        nothingFound.setVisible(false);
                        yourResults.setVisible(true);
                    } catch (WrongPasswordException ex) {
                        Notification notification = new Notification();
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setText(ex.getMessage());
                        notification.setDuration(5000);
                        notification.open();
                        nothingFound.setVisible(true);
                        yourResults.setVisible(false);
                    }
                } else if(select.getValue().equals("Departure")) {
                    DepartureAirportModel departure;
                    try {
                        ViewsUtils.forceRefreshToken();
                        departure = serverApis.getDeparture(flightCode.getValue());
                        Notification.show("Retrieved successfully");
                        departureView = new DepartureView(departure, serverApis, false);
                        departureView.getElement().setAttribute("theme", Lumo.DARK);
                        flexLayout.add(departureView);
                        setHorizontalComponentAlignment(Alignment.AUTO, departureView);
                        nothingFound.setVisible(false);
                        yourResults.setVisible(true);
                    } catch (WrongPasswordException ex) {
                        Notification notification = new Notification();
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setText(ex.getMessage());
                        notification.setDuration(5000);
                        notification.open();
                        nothingFound.setVisible(true);
                        yourResults.setVisible(false);
                    }
                }
            } catch (SoapFaultClientException | IOException ex) {
                Notification.show(ex.getMessage());
                nothingFound.setVisible(true);
                yourResults.setVisible(false);
            }
        }});
        getData.addClickShortcut(Key.ENTER);

        HorizontalLayout horizontalLayout = new HorizontalLayout();


        horizontalLayout.add(flightCode, select);
        horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, flightCode, select);
        setHorizontalComponentAlignment(Alignment.CENTER, getData, horizontalLayout, yourResults, nothingFound, flexLayout);
        add(horizontalLayout, getData, yourResults, nothingFound, flexLayout);
    }
}
