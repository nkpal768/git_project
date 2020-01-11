package com.inn.foresight.customercare.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ChannelCode")
@XmlRootElement(name = "ChannelCode")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class ChannelCode implements Serializable {

	private static final long serialVersionUID = 5314813624661012827L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "channelcodeid_pk")
	private Integer id;

	@Basic
	private String channelname;
	@Basic
	private String code;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "channelcodeid_fk", nullable = false)
	private ChannelCode channelCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChannelname() {
		return channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ChannelCode getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(ChannelCode channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "ChannelCode [id=" + id + ", channelname=" + channelname + ", code=" + code + ", channelCode=" + channelCode + "]";
	}

}
