package com.inn.foresight.customercare.service;

import java.util.List;

import com.inn.core.generic.service.IGenericService;
import com.inn.foresight.customercare.model.ChannelCode;
import com.inn.foresight.customercare.utils.wrapper.ParentChannelCodeWrapper;

public interface IChannelCodeService extends IGenericService<Integer, ChannelCode> {

	List<ParentChannelCodeWrapper> getAllChannelCodeDetail();

}
