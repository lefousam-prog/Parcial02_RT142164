package sv.edu.udb.parcial02_rt142164.data;

public class Person {




    private String names;
    private String surnames;
    private String carnet;
    private String telephone;
    private String age;
    private String auth;

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    private String iduser;



    public Person(String names, String surnames, String carnet, String telephone, String age, String auth, String iduser){
        this.names = names;
        this.surnames = surnames;
        this.carnet = carnet;
        this.telephone = telephone;
        this.age = age;
        this.auth = auth;
        this.iduser = iduser;

    }

    public Person() {

    }



    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
