public class account {
    public account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // compare this account to a provided one
    public boolean equals(account t) {
    	return (this.password.equals(t.getPassword()) && this.username.toLowerCase().equals(t.getUsername().toLowerCase()));
    }
}
