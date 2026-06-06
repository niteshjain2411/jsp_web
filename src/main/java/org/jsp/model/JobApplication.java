package org.jsp.model;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class JobApplication {
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String qualification;
    private String experience;
    private String noticePeriod;
    private String linkedInProfileLink;
    private String skillsSummary;
    private List<String> selectedDomains;
    private File attachedFile;
}
