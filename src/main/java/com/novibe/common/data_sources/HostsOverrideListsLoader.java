package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    @Override
    protected Stream<BypassRoute> lineParser(String urlList) {
        return Pattern.compile("\\r?\\n").splitAsStream(urlList)
                .parallel()
                .map(String::strip)
                .filter(str -> !str.isBlank())
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !HostsBlockListsLoader.isBlock(line))
                .map(this::mapLine)
                .filter(route -> route != null);
    }

    @Override
    protected String listType() {
        return "Override";
    }

    private BypassRoute mapLine(String line) {
    int delimiter = line.indexOf(" ");
    if (delimiter == -1) return null;
    String ip = line.substring(0, delimiter);
    String website = line.substring(delimiter + 1).strip();
    if (website.isEmpty()) return null;
    return new BypassRoute(ip, website);
}

}
