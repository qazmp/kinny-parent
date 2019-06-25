package com.kinny.pojo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

public class TbSeller implements Serializable {
    @NotBlank(message = "商家登录名不能为空")
    private String sellerId;

    @NotBlank(message = "商家公司名称不能为空")
    private String name;
    @NotBlank(message = "商家店铺名称不能为空")
    private String nickName;
    @NotBlank(message = "商家账号密码不能为空")
    private String password;
    //@Email(message = "必须是email")
    private String email;
    //@NotBlank(message = "商家公司手机不能为空")
    private String mobile;
    @NotBlank(message = "商家公司电话不能为空")
    private String telephone;

    private String status;
    @NotBlank(message = "商家公司详细地址不能为空")
    private String addressDetail;
    @NotBlank(message = "商家联系人姓名不能为空")
    private String linkmanName;
    @NotBlank(message = "商家联系人QQ不能为空")
    private String linkmanQq;
    @NotBlank(message = "商家联系人手机不能为空")
    private String linkmanMobile;
    @Email(message = "商家联系人email格式不正确")
    private String linkmanEmail;
    @NotBlank(message = "商家营业执照号不能为空")
    private String licenseNumber;
    @NotBlank(message = "商家税务登记证号不能为空")
    private String taxNumber;
    @NotBlank(message = "商家组织机构代码不能为空")
    private String orgNumber;
    private Long address;
    //@NotBlank(message = "商家公司LOGO图不能为空")
    private String logoPic;
    //@NotBlank(message = "商家公司简介不能为空")
    private String brief;

    private Date createTime;
    @NotBlank(message = "商家法定代表人不能为空")
    private String legalPerson;
    @NotBlank(message = "商家法定代表人身份证不能为空")
    private String legalPersonCardId;
    @NotBlank(message = "商家开户行账号名称不能为空")
    private String bankUser;
    @NotBlank(message = "商家开户行不能为空")
    private String bankName;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail == null ? null : addressDetail.trim();
    }

    public String getLinkmanName() {
        return linkmanName;
    }

    public void setLinkmanName(String linkmanName) {
        this.linkmanName = linkmanName == null ? null : linkmanName.trim();
    }

    public String getLinkmanQq() {
        return linkmanQq;
    }

    public void setLinkmanQq(String linkmanQq) {
        this.linkmanQq = linkmanQq == null ? null : linkmanQq.trim();
    }

    public String getLinkmanMobile() {
        return linkmanMobile;
    }

    public void setLinkmanMobile(String linkmanMobile) {
        this.linkmanMobile = linkmanMobile == null ? null : linkmanMobile.trim();
    }

    public String getLinkmanEmail() {
        return linkmanEmail;
    }

    public void setLinkmanEmail(String linkmanEmail) {
        this.linkmanEmail = linkmanEmail == null ? null : linkmanEmail.trim();
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber == null ? null : licenseNumber.trim();
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber == null ? null : taxNumber.trim();
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setOrgNumber(String orgNumber) {
        this.orgNumber = orgNumber == null ? null : orgNumber.trim();
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public String getLogoPic() {
        return logoPic;
    }

    public void setLogoPic(String logoPic) {
        this.logoPic = logoPic == null ? null : logoPic.trim();
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson == null ? null : legalPerson.trim();
    }

    public String getLegalPersonCardId() {
        return legalPersonCardId;
    }

    public void setLegalPersonCardId(String legalPersonCardId) {
        this.legalPersonCardId = legalPersonCardId == null ? null : legalPersonCardId.trim();
    }

    public String getBankUser() {
        return bankUser;
    }

    public void setBankUser(String bankUser) {
        this.bankUser = bankUser == null ? null : bankUser.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }
}