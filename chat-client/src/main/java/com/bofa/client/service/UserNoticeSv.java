package com.bofa.client.service;

import com.ai.nbs.common.spring.service.BaseSv;
import com.bofa.attribute.NoticeStatus;
import com.bofa.client.dao.userNotice.UserNoticeMapper;
import com.bofa.client.dao.userNotice.entity.UserNoticeExample;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.exception.ChatException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.service
 * @date 2019/4/19
 */
@Service
public class UserNoticeSv extends BaseSv<UserNoticeMapper, UserNotice, String, UserNoticeExample> {

    @Autowired
    private UserSv userSv;

    public void save(List<UserNotice> userNotices) {
        if (userNotices != null && userNotices.size() != 0) {
            userNotices.forEach(
                    userNotice -> {
                        userNotice.setNoticestatus(NoticeStatus.UNREAD.status);
                    }
            );
            super.mapper.saveAll(userNotices);
        }
    }

    public void save(UserNotice userNotice){
        if (StringUtils.isEmpty(userNotice.getId())){
            userNotice.setId(UUID.randomUUID().toString());
        }
        super.mapper.insert(userNotice);
    }

    public void update(List<UserNotice> userNotices) {
        userNotices.forEach(userNotice -> super.mapper.updateByPrimaryKey(userNotice));
    }

    List<UserNotice> getUserNotices(Integer userId) {
        UserNoticeExample ex = new UserNoticeExample();
        ex.createCriteria().andNoticeidEqualTo(userId);
        return super.mapper.selectByExample(ex);
    }

    List<UserNotice> getUserNoticesByType(Integer userId, Integer noticeType) {
        UserNoticeExample ex = new UserNoticeExample();
        UserNoticeExample.Criteria cs = ex.createCriteria();
        cs.andNoticeidEqualTo(userId);
        cs.andNoticetypeEqualTo(noticeType);
        return super.mapper.selectByExample(ex);
    }

    List<UserNotice> getUserNonReadNotices(Integer userId) {
        UserNoticeExample ex = new UserNoticeExample();
        UserNoticeExample.Criteria cs = ex.createCriteria();
        cs.andNoticeidEqualTo(userId);
        cs.andNoticestatusEqualTo(NoticeStatus.UNREAD.status);
        return super.mapper.selectByExample(ex);
    }

    List<UserNotice> getUserNonReadNoticesByType(Integer userId, Integer noticeType){
        UserNoticeExample ex = new UserNoticeExample();
        UserNoticeExample.Criteria cs = ex.createCriteria();
        cs.andNoticeidEqualTo(userId);
        cs.andNoticestatusEqualTo(NoticeStatus.UNREAD.status);
        cs.andNoticetypeEqualTo(noticeType);
        return super.mapper.selectByExample(ex);
    }
}