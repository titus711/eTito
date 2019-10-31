package Model;

public class SettingsUsersInfo {

    String email, image, address;


    public SettingsUsersInfo(){

    }

    public SettingsUsersInfo(String email, String image, String address) {
        this.email = email;
        this.image = image;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
