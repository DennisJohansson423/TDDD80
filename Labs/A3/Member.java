package com.example.a3;

import java.util.ArrayList;

/**
 * Creates the members list and gets the members from the groups
 * and gets the different information about every member.
 */
public class Member {

    private String epost;
    private String namn;
    private String svarade;

    public String getEpost() {
        return epost;
    }

    public String getNamn() {
        return namn;
    }

    public String getSvarade() {
        return svarade;
    }

    public static class GroupMembersResponse {
        private ArrayList<Member> medlemmar;

        public ArrayList<Member> getMedlemmar() {
            return medlemmar;
        }
    }
}