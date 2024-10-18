package server;


public class UserData {
    private String username;
    private String password;
    private String email;
    private String authToken;


    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean setPassword(String newPassword) {
        this.password = newPassword;
        return true;
    }

    public boolean setEmail(String newEmail) {
        this.email = newEmail;
        return true;
    }

    public boolean setAuthToken(String authToken) {
        this.authToken = authToken;
        return true;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

}
