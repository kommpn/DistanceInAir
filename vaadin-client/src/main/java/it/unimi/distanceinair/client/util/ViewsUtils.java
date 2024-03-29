package it.unimi.distanceinair.client.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ViewsUtils {

    public static String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token
                && token.getPrincipal() instanceof DefaultOidcUser oidcUser) {
        return oidcUser.getIdToken().getTokenValue();
      } else {
            forceRefreshToken();
            return null;
        }
    }
    public static void forceRefreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token
                && token.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            authentication.setAuthenticated(oidcUser.getExpiresAt().toEpochMilli() > new Date().toInstant().toEpochMilli());
        } else {
            authentication.setAuthenticated(false);
        }
    }
    public static String formatDate(String dateString) {
        dateString = dateString.replace("T", " ");
        Pattern pattern = Pattern.compile(":.[0-9]\\..*");
        Matcher matcher = pattern.matcher(dateString);
        String input = matcher.replaceAll("");
        String[] dateArray = input.split(" ");
        ArrayList<String> arrayList = Arrays.stream(dateArray[0].split("-")).collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(arrayList);
        return String.join("/", arrayList) + " " + dateArray[1];
    }


    public static boolean hasDepartedOrArrived(String dateString) {
        try {
            Date actualDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date oldDate = simpleDateFormat.parse(dateString);
            return actualDate.compareTo(oldDate) > 0;
        } catch (ParseException ex) {
            System.out.println("error");
        }
        return false;
    }
}
