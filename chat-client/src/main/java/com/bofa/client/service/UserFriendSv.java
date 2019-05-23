package com.bofa.client.service;

import com.ai.nbs.common.spring.service.BaseSv;
import com.bofa.client.dao.userfriends.UserFriendsMapper;
import com.bofa.client.dao.userfriends.entity.UserFriendsExample;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.service
 * @date 2019/4/20
 */
@Service
public class UserFriendSv extends BaseSv<UserFriendsMapper, UserFriend, Integer, UserFriendsExample> {

    /**
     * update userFriend which message(like remark, or userFriendName) is changed
     * so delete all and insert all it.
     *
     * @param userFriends
     */
    public void save(Integer userId, List<UserFriend> userFriends) {
        if (userFriends != null && userFriends.size() != 0) {
            super.mapper.deleteAll(userId);
            super.mapper.insertAll(userFriends);
        }
    }

    // TODO: 2019/5/24 h2-database userFriend表结构修改了一下，save业务有问题，需要调试
    // TODO: 2019/5/24 另外, 验证回调reMapper 修改了userFriend对象，结果h2 保存对象获取的是修改后的对象
    // TODO: 2019/5/24 以上几点，尽快完成. 明天目标，好友申请功能（在线->离线, 在线->在线) 再测试下单聊功能(在线->离线,在线->在线) 
    // TODO: 2019/5/24 另外好友备注功能，尽快加进去(修改备注，查找好友信息（根据备注信息), 单聊） 
    // TODO: 2019/5/24 群聊功能，可以的话先设计方案思路, (群聊房间创建，群聊房间邀请，群聊房间申请，群聊）
    // TODO: 2019/5/24 继群聊之后，需考虑netty-server的集群部署，需要介入软负载做反向代理分发请求，需要集成redis记录请求对象的ip地址
    

    public void save(UserFriend userFriend) {
        assert userFriend != null;
        UserFriendsExample ex = new UserFriendsExample();
        UserFriendsExample.Criteria cs = ex.createCriteria();
        cs.andUserfriendidEqualTo(userFriend.getUserFriendId());
        cs.andUseridEqualTo(userFriend.getUserId());
        if (getOnlyOne(super.mapper.selectByExample(ex)) != null) {
            PrintStreamDelegate.delegate(() -> {
                System.out.println("你和对方 [" + userFriend.getUserFriendName() + "] 已是好友");
            });
        }else {
            super.mapper.insert(userFriend);
        }
    }

    public List<UserFriend> getUserFriends(Integer userId) {
        assert userId != null;
        UserFriendsExample ex = new UserFriendsExample();
        ex.createCriteria().andUseridEqualTo(userId);
        return super.mapper.selectByExample(ex);
    }

    public void delete(String userFriendName) {
        assert StringUtils.isNotEmpty(userFriendName);
        UserFriendsExample ex = new UserFriendsExample();
        ex.createCriteria().andUserfriendnameLike("%" + userFriendName + "%");
        super.mapper.deleteByExample(ex);
    }

    public void delete(Integer userFriendId) {
        assert userFriendId != null;
        UserFriendsExample ex = new UserFriendsExample();
        ex.createCriteria().andUserfriendidEqualTo(userFriendId);
        super.mapper.deleteByExample(ex);
    }
}
