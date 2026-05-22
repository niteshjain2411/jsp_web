package org.jsp.model;


import lombok.Data;

import java.util.List;

@Data
public class VolunteerRegistrationData {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private String city;
    private List<String> selectedActivities;
    private String professionSkills;

    public String getSelectedActivitiesAsString() {
        if (selectedActivities == null || selectedActivities.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        selectedActivities.forEach(activity -> sb.append(activity).append(", "));
        // Remove trailing comma and space
        return sb.substring(0, sb.length() - 2);
    }
}
