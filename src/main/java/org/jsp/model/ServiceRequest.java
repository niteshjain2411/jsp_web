package org.jsp.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ServiceRequest {
    private String id;
    private String fullName;
    private String dob;
    private String gender;
    private String fatherName;
    private String phone;
    private String panNumber;
    private String address;
    private String occupation;
    private Integer monthlyIncome;
    private String appliedBefore;
    private String helpWanted;
    private String afterHelpPlan;
    private String documentFileName;

    private List<FamilyMember> familyMembers;
    private String status;
    private String remarks;

    private Date createdOn;
    private Date lastUpdatedOn;

    @Data
    public static class FamilyMember {
        private String name;
        private Integer age;
        private String relation;
        private String occupation;
    }
}