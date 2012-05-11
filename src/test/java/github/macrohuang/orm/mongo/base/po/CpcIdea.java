package github.macrohuang.orm.mongo.base.po;

import github.macrohuang.orm.mongo.annotation.MongoField;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CpcIdea {
	@MongoField(field = "_id")
	private Long id;
	
	@MongoField(field="ACCOUNTID")
	private Long accountId;
	
	@MongoField(field="AGENTID")
    private Long agentId;
	
	@MongoField(field = "IDEAID")
    private Long ideaId;
	
	@MongoField(field = "IDEA")
    private String idea;
	
	@MongoField(field = "IDEADESC1")
    private String ideaDesc1;
	
	@MongoField(field = "VISITURL")
    private String visitURL;
	
	@MongoField(field = "SHOWURL")
    private String showURL;
	
	@MongoField(field = "CPCGRPNAME")
    private String cpcGrpName;
	
	@MongoField(field = "CONSUME")
    private Integer consume;
	
	@MongoField(field = "CLICKCOUNT")
    private Integer clickCount;
	
	@MongoField(field = "POSITION")
    private Integer position;
	
	@MongoField(field = "PV")
    private Integer pv;
	
	@MongoField(field = "DATE")
    private String date;
	
	@MongoField(field="CTR")
    private Integer ctr;
	
	@MongoField(field="OLDPV")
    private Integer oldPV;
	
	@MongoField(field="cpcplanid")
    private Long cpcPlanId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Long getIdeaId() {
		return ideaId;
	}

	public void setIdeaId(Long ideaId) {
		this.ideaId = ideaId;
	}

	public String getIdea() {
		return idea;
	}

	public void setIdea(String idea) {
		this.idea = idea;
	}

	public String getIdeaDesc1() {
		return ideaDesc1;
	}

	public void setIdeaDesc1(String ideaDesc1) {
		this.ideaDesc1 = ideaDesc1;
	}

	public String getVisitURL() {
		return visitURL;
	}

	public void setVisitURL(String visitURL) {
		this.visitURL = visitURL;
	}

	public String getShowURL() {
		return showURL;
	}

	public void setShowURL(String showURL) {
		this.showURL = showURL;
	}

	public String getCpcGrpName() {
		return cpcGrpName;
	}

	public void setCpcGrpName(String cpcGrpName) {
		this.cpcGrpName = cpcGrpName;
	}

	public Integer getConsume() {
		return consume;
	}

	public void setConsume(Integer consume) {
		this.consume = consume;
	}

	public Integer getClickCount() {
		return clickCount;
	}

	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getPv() {
		return pv;
	}

	public void setPv(Integer pv) {
		this.pv = pv;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getCtr() {
		return ctr;
	}

	public void setCtr(Integer ctr) {
		this.ctr = ctr;
	}

	public Integer getOldPV() {
		return oldPV;
	}

	public void setOldPV(Integer oldPV) {
		this.oldPV = oldPV;
	}

	public Long getCpcPlanId() {
		return cpcPlanId;
	}

	public void setCpcPlanId(Long cpcPlanId) {
		this.cpcPlanId = cpcPlanId;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
