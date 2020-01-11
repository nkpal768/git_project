package com.inn.foresight.customercare.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inn.core.generic.service.impl.AbstractService;
import com.inn.foresight.core.generic.utils.Utils;
import com.inn.foresight.customercare.dao.IChannelCodeDao;
import com.inn.foresight.customercare.model.ChannelCode;
import com.inn.foresight.customercare.service.IChannelCodeService;
import com.inn.foresight.customercare.utils.wrapper.ChildChannelCodeWrapper;
import com.inn.foresight.customercare.utils.wrapper.ParentChannelCodeWrapper;

@Service("ChannelCodeServiceImpl")
public class ChannelCodeServiceImpl extends AbstractService<Integer, ChannelCode> implements IChannelCodeService {

	private Logger logger = LogManager.getLogger(ChannelCodeServiceImpl.class);

	@Autowired
	private IChannelCodeDao iChannelCodeDao;

	@Autowired
	public void setDao(IChannelCodeDao dao) {
		super.setDao(dao);
		this.iChannelCodeDao = dao;
	}

	@Override
	public List<ParentChannelCodeWrapper> getAllChannelCodeDetail() {
		logger.info("Going to get AllChannel Code Detail");
		List<ParentChannelCodeWrapper> wrappers = new ArrayList<>();
		try {
			List<ChannelCode> channelCodeList = iChannelCodeDao.getAllChannelDetail();
			if (channelCodeList != null && !channelCodeList.isEmpty()) {
				logger.info("Total Channel Code Data found {} ", channelCodeList.size());
				populateChannelCodeData(wrappers, channelCodeList);
			}
		} catch (Exception exception) {
			logger.error("Error in getting All ChannelCode Detail Exception {} ", Utils.getStackTrace(exception));
		}
		return wrappers;
	}

	private void populateChannelCodeData(List<ParentChannelCodeWrapper> wrappers, List<ChannelCode> channelCodeList) {
		ParentChannelCodeWrapper parentChannelCodeWrapper = null;
		for (ChannelCode channelCode : channelCodeList) {
			try {
				parentChannelCodeWrapper = new ParentChannelCodeWrapper();
				List<ChildChannelCodeWrapper> channelCodeWrappers = new ArrayList<>();
				parentChannelCodeWrapper.setChannelName(channelCode.getChannelname() != null ? channelCode.getChannelname() : null);
				parentChannelCodeWrapper.setChannelCode(channelCode.getCode() != null ? channelCode.getCode() : null);
				List<ChannelCode> channelCodes = iChannelCodeDao.getChannelDetailByChannelId(channelCode.getId());
				if (channelCodes != null && !channelCodes.isEmpty()) {
					populateSubChannelCode(channelCode, channelCodeWrappers, channelCodes);
				}
				parentChannelCodeWrapper.setChildChannelCodeWrappers(channelCodeWrappers);
				wrappers.add(parentChannelCodeWrapper);
			} catch (Exception exception) {
				logger.warn("Error in fetching Channel Code {} ", exception.getMessage());
			}
		}
	}

	private void populateSubChannelCode(ChannelCode channelCode, List<ChildChannelCodeWrapper> channelCodeWrappers, List<ChannelCode> channelCodes) {
		for (ChannelCode subChannel : channelCodes) {
			try {
				ChildChannelCodeWrapper childChannelCodeWrapper = new ChildChannelCodeWrapper();
				childChannelCodeWrapper.setChannelCode(subChannel.getCode() != null ? subChannel.getCode() : null);
				childChannelCodeWrapper.setChannelName(subChannel.getChannelname() != null ? subChannel.getChannelname() : null);
				channelCodeWrappers.add(childChannelCodeWrapper);
			} catch (Exception exception) {
				logger.warn("Error in fetching data from ChannelCode for {} {} ", channelCode.getId(), exception.getMessage());
			}
		}
	}
}
