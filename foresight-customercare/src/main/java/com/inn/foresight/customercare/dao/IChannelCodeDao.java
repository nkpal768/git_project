package com.inn.foresight.customercare.dao;

import java.util.List;

import com.inn.core.generic.dao.IGenericDao;
import com.inn.foresight.customercare.model.ChannelCode;

public interface IChannelCodeDao extends IGenericDao<Integer, ChannelCode> {

	List<ChannelCode> getAllChannelDetail();

	List<ChannelCode> getChannelDetailByChannelId(Integer channelId);

}
