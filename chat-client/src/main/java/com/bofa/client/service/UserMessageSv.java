package com.bofa.client.service;

import com.ai.nbs.common.spring.service.BaseSv;
import com.bofa.client.dao.usermessage.UserMessageMapper;
import com.bofa.client.dao.usermessage.entity.UserMessageExample;
import com.bofa.entity.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.service
 * @date 2019/4/20
 */
@Service
public class UserMessageSv extends BaseSv<UserMessageMapper, UserMessage, Integer, UserMessageExample> {

    public void save(UserMessage message) {
        super.mapper.insert(message);
    }

    public List<UserMessage> findByFriendName(String friendName, String userName) {
        UserMessageExample ex = new UserMessageExample();
        UserMessageExample.Criteria cs = ex.createCriteria();
        cs.andFromusernameLike("%" + friendName + "%");
        cs.andTousernameEqualTo(userName);
        UserMessageExample.Criteria cs2 = ex.or();
        cs2.andFromusernameEqualTo(userName);
        cs2.andTousernameEqualTo("%" + friendName + "%");
        return super.mapper.selectByExample(ex);
    }

    public List<UserMessage> findByFriendId(Integer friendId, String userName){
        UserMessageExample ex = new UserMessageExample();
        UserMessageExample.Criteria cs = ex.createCriteria();
        cs.andFromusernameEqualTo(userName);
        cs.andTouseridEqualTo(friendId);
        UserMessageExample.Criteria cs2 = ex.or();
        cs2.andFromuseridEqualTo(friendId);
        cs2.andTousernameEqualTo(userName);
        return super.mapper.selectByExample(ex);
    }
}
