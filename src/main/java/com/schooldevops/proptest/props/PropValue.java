package com.schooldevops.proptest.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "schooldevops.prop-test")
public class PropValue {
    private String name;
    private String[] friends;
    private Map<String, Integer> cutline2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

    public Map<String, Integer> getCutline2() {
        return cutline2;
    }

    public void setCutline2(Map<String, Integer> cutline2) {
        this.cutline2 = cutline2;
    }
}
