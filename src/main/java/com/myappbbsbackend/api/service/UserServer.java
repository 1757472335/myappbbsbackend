package com.myappbbsbackend.api.service;

import com.myappbbsbackend.api.dao.CsGlsqbMapper;
import com.myappbbsbackend.api.dao.CsUserinfoMapper;
import com.myappbbsbackend.api.dao.ViewuserinfoMapper;
import com.myappbbsbackend.api.entity.CsGlsqb;
import com.myappbbsbackend.api.entity.CsUserinfo;
import com.myappbbsbackend.api.entity.Viewuserinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Description:
 * @ Author: 小火锅
 * @ Date: 2020/11/19 12:55
 */
@Service
public class UserServer implements UserServerInt {

    @Autowired
    private CsUserinfoMapper csUserinfoMapper;
    @Autowired
    private ViewuserinfoMapper viewuserinfoMapper;
    @Autowired
    private CsGlsqbMapper csGlsqbMapper;

    /**
     * 用户是否登录成功
     * @param userinfo
     * @return
     */
    @Override
    public boolean userLogin(CsUserinfo userinfo) {
        if (userinfo.getUsername().equals("") || userinfo.getUserpassword().equals("")|| userinfo.getUserpassword()==null || userinfo.getUsername()==null )
        {
            return false;
        }
        else {
            return (csUserinfoMapper.userLogin(userinfo.getUsername(),userinfo.getUserpassword())==1);
        }
    }

    /**
     *  getUserInfoByuserName
     * @param username
     * @return
     */
    @Override
    public CsUserinfo getUserInfoByuserName(String username) {
        return csUserinfoMapper.selectByUserName(username);
    }

    /**
     * getUserInfoViewByUserName
     * @param username 用户名
     * @return
     */
    @Override
    public Viewuserinfo getUserInfoViewByUserName(String username) {
        return viewuserinfoMapper.selectUserInfoByUserName(username);
    }


    /***
     *  判断用户是否可以注册
     * @param userinfo
     * @return
     */
    @Override
    public String isRegister(CsUserinfo userinfo) {
        String isRegister ;
        if ( csUserinfoMapper.isRegisterName(userinfo)>=1){
            isRegister ="Name";
        }else if(csUserinfoMapper.isRegisterEmail(userinfo)>=1){
            isRegister ="Email";
        }else if(csUserinfoMapper.isRegisterPhone(userinfo)>=1){
            isRegister ="Phone";
        }else {
            isRegister ="true";
        }
        return isRegister;
    }

    @Override
    public boolean userRegister(CsUserinfo userinfo) {

        return (csUserinfoMapper.userRegister(userinfo)==1);
    }

    @Override
    public int selectUserByUserId(int id) {

        return csUserinfoMapper.selectUserByUserId(id);
    }

    @Override
    public CsUserinfo getUserInfoByuserid(int id) {
        return csUserinfoMapper.selectUserId(id);
    }

    /**
     * 修改用户信息
     * @param csUserinfo
     * @return
     */
    @Override
    public int updateUserInfo(CsUserinfo csUserinfo) {
        return csUserinfoMapper.updateByPrimaryKey(csUserinfo);
    }

    @Override
    public int updateUserPaw(String password,int userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userpassword",password);
        data.put("id",userId);
        return csUserinfoMapper.updateUserPasw(data);
    }


    @Override
    public int forbidden(int id) {
        return csUserinfoMapper.forbidden(id);
    }

    @Override
    public int nuforbidden(int id) {
        return csUserinfoMapper.nuforbidden(id);
    }

    @Override
    public int superUser(int id) {
        return csUserinfoMapper.superUser(id);
    }

    @Override
    public int adminUser(int id) {
         return csUserinfoMapper.adminUser(id);
    }

    @Override
    public int appTaAdmin(int userid) {
        return csGlsqbMapper.appTaAdmin(userid);
    }


    @Override
    public List<CsGlsqb> getAlllist() {
        return csGlsqbMapper.getAlllist();
    }

    /**
     * 提交
     * @param csGlsqb
     * @return
     */
    @Override
    public int submitAdmin(CsGlsqb csGlsqb) {
        return csGlsqbMapper.insert(csGlsqb);
    }

    /**
     * 驳回
     * @param id
     * @return
     */
    @Override
    public int rejectAdmin(int id) {
        return csGlsqbMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int selectIsSubmit(int userid) {
        return csGlsqbMapper.selectIsSubmit(userid);
    }


}
