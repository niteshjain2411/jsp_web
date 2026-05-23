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
}
