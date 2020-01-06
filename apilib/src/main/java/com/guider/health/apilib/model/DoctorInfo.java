package com.guider.health.apilib.model;

public class DoctorInfo {


    /**
     * id : 131
     * createTime : null
     * updateTime : 2019-07-04T07:44:56Z
     * accountId : 201
     * name : 哈力夫
     * gender : MAN
     * birthday : 1982-01-30
     * phone : 13811191508
     * mail : gaole612@163.com
     * professionalTitle : 主任医师
     * descDetail : 测试
     * hospital : 宣武医院
     * department : 0
     * doctorCcertificate : http://47.92.218.150:8082/files/2019-07-03/b1bd2468ab69d2e9f68b1a5f35ffe84d.jpeg
     * province : 24
     * city : 89
     * countie : 91
     * cardPositiveUrl : http://47.92.218.150:8082/files/2019-07-03/edbcd11d9e5286a093799881b9970531.jpg
     * cardNegativeUrl : http://47.92.218.150:8082/files/2019-07-03/add629510762b744a177e950b7fb96fd.jpg
     * userState : ACTIVE
     * remark : 备注
     * cardId : 11010219820613235X
     * addr : null
     * skill : null
     */

    private int id;
    private int accountId;
    private String name;
    private String gender;
    private String birthday;
    private String phone;
    private String mail;
    private String professionalTitle;
    private String descDetail;
    private String hospital;
    private String department;
    private String doctorCcertificate;
    private int province;
    private int city;
    private int countie;
    private String cardPositiveUrl;
    private String cardNegativeUrl;
    private String userState;
    private String remark;
    private String cardId;
    private Object addr;
    private Object skill;
    private String headUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public DoctorInfo setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public String getDescDetail() {
        return descDetail;
    }

    public void setDescDetail(String descDetail) {
        this.descDetail = descDetail;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctorCcertificate() {
        return doctorCcertificate;
    }

    public void setDoctorCcertificate(String doctorCcertificate) {
        this.doctorCcertificate = doctorCcertificate;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getCountie() {
        return countie;
    }

    public void setCountie(int countie) {
        this.countie = countie;
    }

    public String getCardPositiveUrl() {
        return cardPositiveUrl;
    }

    public void setCardPositiveUrl(String cardPositiveUrl) {
        this.cardPositiveUrl = cardPositiveUrl;
    }

    public String getCardNegativeUrl() {
        return cardNegativeUrl;
    }

    public void setCardNegativeUrl(String cardNegativeUrl) {
        this.cardNegativeUrl = cardNegativeUrl;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Object getAddr() {
        return addr;
    }

    public void setAddr(Object addr) {
        this.addr = addr;
    }

    public Object getSkill() {
        return skill;
    }

    public void setSkill(Object skill) {
        this.skill = skill;
    }
}
