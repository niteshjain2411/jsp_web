package org.jsp.model;


import lombok.Data;

@Data
public class VolunteerRegistrationData {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private String city;
    private String[] selectedActivities;
    private String professionSkills;

    public String getSelectedActivitiesAsString() {
        if (selectedActivities == null || selectedActivities.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String activity : selectedActivities) {
            sb.append(activity).append(", ");
        }
        // Remove trailing comma and space
        return sb.substring(0, sb.length() - 2);
    }
}
