package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.hyas.hpf.database.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
//@AllArgsConstructor
public class User extends BaseEntity implements Serializable {

	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.sfid = ident;  // 法人id
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getSfid();
	}

	@Override
	public void setLogicalDeleteFlag(boolean is_del) {}

	private Timestamp createddate;
	private String name;
	private Timestamp systemmodstamp;
	private String contactid;
	private String country;
	private String sfid;
	private String username;
	private String middlename;
	private String sendername;
	private Timestamp lastlogindate;
	private String lastname;
	private Boolean userpermissionssupportuser;
	private Boolean userpreferencesdisablefollowersemail;
	private String portalrole;
	private String street;
	private Boolean userpermissionsinteractionuser;
	private String profileid;
	private Boolean receivesadmininfoemails;
	private String smallphotourl;
	private String aboutme;
	private String outofofficemessage;
	private Boolean userpermissionsmobileuser;
	private Boolean forecastenabled;
	private String itemslct__c;
	private String senderemail;
	private Boolean userpreferencesactivityreminderspopup;
	private Boolean userpreferencesdiscommentafterlikeemail;
	private Boolean userpreferencesdismentionscommentemail;
	private String accountid;
	private String companyname;
	private String managerid;
	private Timestamp lastvieweddate;
	private Boolean userpreferencesdisablechangecommentemail;
	private String bannerphotourl;
	private Boolean userpreferencesdisableendorsementemail;
	private Timestamp offlinepdatrialexpirationdate;
	private String timezonesidkey;
	private Boolean userpreferencesdisablelikeemail;
	private Boolean emailpreferencesautobccstayintouch;
	private Boolean isportalenabled;
	private String stayintouchsubject;
	private String city;
	private String localesidkey;
	private Boolean userpreferencesdisablebookmarkemail;
	private String federationidentifier;
	private Double latitude;
	private String fullphotourl;
	private String mobilephone;
	private Boolean userpermissionsavantgouser;
	private Boolean userpreferencescreatelexappswtshown;
	private String division;
	private String languagelocalekey;
	private Boolean userpreferencesdisableallfeedsemail;
	private Timestamp lastmodifieddate;
	private String callcenterid;
	private String phone;
	private Boolean userpreferencesdisableprofilepostemail;
	private Boolean userpreferencesdisablelatercommentemail;
	private Boolean userpermissionscallcenterautologin;
	private Double longitude;
	private Timestamp lastpasswordchangedate;
	private Timestamp offlinetrialexpirationdate;
	private Boolean isactive;
	private String smallbannerphotourl;
	private String alias;
	private String emailencodingkey;
	private String badgetext;
	private Boolean userpreferencesdisablemessageemail;
	private String lastmodifiedbyid;
	private String state;
	private Boolean userpreferencesdisablefilesharenotificationsforapi;
	private String suffix;
	private String mediumphotourl;
	private String department;
	private String defaultgroupnotificationfrequency;
	private String stayintouchnote;
	private Boolean receivesinfoemails;
	private Boolean userpermissionsofflineuser;
	private Boolean emailpreferencesstayintouchreminder;
	private Boolean userpreferencescachediagnostics;
	private String stayintouchsignature;
	private Boolean emailpreferencesautobcc;
	private String communitynickname;
	private Boolean userpreferencesdisprofpostcommentemail;
	private String employeenumber;
	private String geocodeaccuracy;
	private String signature;
	private Boolean userpermissionssfcontentuser;
	private String postalcode;
	private String title;
	private Boolean userpreferencesdisablementionspostemail;
	private Boolean userpreferencesapexpagesdevelopermode;
	private Boolean userpreferencesdisablesharepostemail;
	private String digestfrequency;
	private Boolean userpermissionsmarketinguser;
	private Boolean isprofilephotoactive;
	private Boolean isextindicatorvisible;
	private String createdbyid;
	private String firstname;
	private String email;
	private Timestamp lastreferenceddate;
	private String fax;
	private String mediumbannerphotourl;
	private String extension;
	private String delegatedapproverid;
	private Boolean userpreferenceshidebiggerphotocallout;
	private Boolean userpreferenceshideenduseronboardingassistantmodal;
	private String usertype;
	private Boolean userpreferenceshascelebrationbadge;
	private Boolean userpreferenceshidesecondchatteronboardingsplash;
	private Boolean userpreferenceshidecsngetchattermobiletask;
	private Boolean userpreferencesfavoritesshowtopfavorites;
	private Boolean userpreferencesfavoriteswtshown;
	private Boolean userpreferenceseventreminderscheckboxdefault;
	private Boolean userpreferencespathassistantcollapsed;
	private Boolean userpreferencesenableautosubforfeeds;
	private Boolean userpreferencespreviewlightning;
	private Boolean userpreferenceshides1browserui;
	private Boolean userpreferencesrecordhomereservedwtshown;
	private Boolean userpreferenceshidecsndesktoptask;
	private Boolean userpreferencesexcludemailappattachments;
	private Boolean userpreferencesglobalnavbarwtshown;
	private Boolean userpreferenceshidesfxwelcomemat;
	private Boolean userpreferenceshidelightningmigrationmodal;
	private Boolean userpreferenceslightningexperiencepreferred;
	private Boolean userpreferenceshidechatteronboardingsplash;
	private Boolean userpreferencesglobalnavgridmenuwtshown;
	private String userroleid;
	private Boolean userpreferencespreviewcustomtheme;
	private Boolean userpreferencesremindersoundoff;
	private Boolean userpreferencesrecordhomesectioncollapsewtshown;

	@JsonIgnore
	private Integer id;
	@JsonIgnore
	private String _hc_lastop;
	@JsonIgnore
	private String _hc_err;
}
