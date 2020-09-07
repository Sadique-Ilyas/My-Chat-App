package com.example.chatapp.Model;

import com.google.firebase.database.Exclude;

public class User {
    public String isSeen;
    private String name, email, phoneNumber, profilePicUri, status;
    private String documentId, search;

    public User() {
    }

    public User(String name, String email, String phoneNumber, String profilePicUri, String status, String search, String isSeen) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicUri = profilePicUri;
        this.status = status;
        this.search = search;
        this.isSeen = isSeen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
