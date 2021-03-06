package com.myappbbsbackend.api.controller;
import com.alibaba.fastjson.JSONObject;

import com.myappbbsbackend.Interceptor.PassToken;
import com.myappbbsbackend.Interceptor.UserLoginToken;
import com.myappbbsbackend.api.entity.CsShool;
import com.myappbbsbackend.api.entity.CsUserinfo;
import com.myappbbsbackend.api.entity.Viewuserinfo;
import com.myappbbsbackend.api.service.ShoolServer;
import com.myappbbsbackend.api.service.TokenService;
import com.myappbbsbackend.api.service.UserServer;
import com.myappbbsbackend.myutil.MyUtil;
import com.myappbbsbackend.planningcontrol.ApiResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.ApiInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * @ Description:
 * @ Author: 小火锅
 * @ Date: 2020/11/19 12:20
 */
@RestController
@RequestMapping(value={"/api/user"})
@CrossOrigin(
        origins = {"http://localhost:8100","http://*/*","http://localhost"},
        allowedHeaders = "*",
        allowCredentials = "true",
        methods = {RequestMethod.GET, RequestMethod.POST,
                RequestMethod.PUT, RequestMethod.DELETE,
                RequestMethod.OPTIONS, RequestMethod.HEAD}
)//允许跨域请求
public class UserController {

    @PassToken
    @GetMapping("/hello")
    public ApiResp apiHello(HttpServletResponse response)
    {
        response.addCookie(new Cookie("hello","11111111"));
        ApiResp res = new ApiResp();
        res.setMsg("Hello");
        return res;
    }



    @Autowired(required = false)
    private  UserServer userServer;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired(required = false)
    private ShoolServer shoolServer;

    //    用户登录

    @PostMapping("/login")
    @PassToken
//    @CrossOrigin
    public ApiResp userLogin(@RequestBody JSONObject jsonObject, HttpServletResponse response, HttpServletRequest request){
        /**
         *  获取请求参数
         */
        System.out.println(jsonObject.getString("username"));
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        CsUserinfo csUserinfo = new CsUserinfo();
        csUserinfo.setUsername(username);
        csUserinfo.setUserpassword(new MyUtil().mD5Hash(password));
        HttpSession httpSession =request.getSession();
        /**
         * 判断是否能登陆
         */
        if (userServer.userLogin(csUserinfo))
        {
            httpSession.setAttribute("user",username);

            csUserinfo = userServer.getUserInfoByuserName(username);
            Viewuserinfo viewuserinfo = new Viewuserinfo();
            viewuserinfo = userServer.getUserInfoViewByUserName(username);
            //下发token、签名、
            String token = tokenService.getToken(csUserinfo,(boolean)jsonObject.get("isRemember"));
            Cookie cookie = new Cookie("token",token);

            String signature = new MyUtil().mD5Hash(password);
            Cookie cookie1 = new Cookie("signature", signature);
            cookie.setPath("/");
            cookie1.setPath("/");
            cookie.setMaxAge(86400*10);
            cookie1.setMaxAge(86400*10);
            response.addCookie(cookie);
            response.addCookie(cookie1);
         return ApiResp.retOK(viewuserinfo);
        }
        else {
            return ApiResp.retFail(505,"登录失败！账号或密码错误");
        }
    }

    /**
     * 退出登录
     * @ return
     */
    @GetMapping("/loginout")
    @ResponseBody
    @UserLoginToken
    public ApiResp loginOut(HttpServletRequest request)
    {
        request.getSession().invalidate();
        return ApiResp.retOK();
    }

    /**
     * 免登陆验证
     * @return
     */
    @GetMapping("/verification")
    @ResponseBody
    @UserLoginToken
    public ApiResp userVerification(HttpServletRequest Request)
    {
    //        @CookieValue("signature")  String signature ,
    //        @CookieValue("token")String token
        return ApiResp.retOK();
    }


    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    @PassToken
    public ApiResp userRegister(@RequestBody JSONObject jsonObject)
    {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String schoolId = jsonObject.getString("schoolid");
        String phone = jsonObject.getString("phone");
//        String vcode = jsonObject.getString("vcode");
        String email = jsonObject.getString("email");
        CsUserinfo csUserinfo = new CsUserinfo();
        csUserinfo.setUsername(username);

        csUserinfo.setUserpassword(new MyUtil().mD5Hash(password));
        csUserinfo.setSchoolid(Integer.parseInt(schoolId));
        csUserinfo.setUserphone(phone);
        csUserinfo.setUseremail(email);
        csUserinfo.setUserregtime(new Date());
        String isregister =  userServer.isRegister(csUserinfo);
        if (isregister.equals("true"))
        {
            //执行注册
            if (userServer.userRegister(csUserinfo))
            {
                return ApiResp.retOK();
            }
            return ApiResp.retFail(505,"未知错误请联系管理员");
        }
        else {
            return ApiResp.retFail(500,isregister+"已经存在");
        }
    }

    @PassToken
    @ResponseBody
    @GetMapping("/getallschool")
    public ApiResp getAllSchool(){
        List<CsShool> csShool ;
        csShool = shoolServer.getShool();
        return ApiResp.retOK(csShool);
    }

    /**
     * 通过用户id 查询用户信息
     * @param id
     * @return
     */
    @UserLoginToken
    @GetMapping("/getuserinfobyuserid")
    public  ApiResp getuserInfoById(
            @RequestParam("userid") int id
            ){
        CsUserinfo csUserinfo;
        csUserinfo = userServer.getUserInfoByuserid(id);
        return ApiResp.retOK(csUserinfo);
    }


    @UserLoginToken
    @PostMapping("/updateuserinfo")
    public ApiResp updateUserInfo(@RequestBody CsUserinfo csUserinfo){
        if(userServer.updateUserInfo(csUserinfo)==1){
            return ApiResp.retOK();
        }
        return ApiResp.retFail(400,"错的操作");

    }

    @UserLoginToken
    @PostMapping("/updateuserpasw")
    public ApiResp updateUserPass(
            @RequestBody JSONObject jsonObject
        ){

        CsUserinfo csUserinfo = new CsUserinfo();
        csUserinfo.setUsername(jsonObject.getString("username"));
        String ypassword = jsonObject.getString("password");

        csUserinfo.setUserpassword( new MyUtil().mD5Hash(ypassword));

        if(!userServer.userLogin(csUserinfo)){
            return ApiResp.retFail(400,"原密码错误");
        }
        else {
            String npassword = jsonObject.getString("newpassword");
            int id = (int)jsonObject.get("userid");
            if(userServer.updateUserPaw(new MyUtil().mD5Hash(npassword)
                    ,id)==1)
            {
                return ApiResp.retOK();
            }
        }

        return ApiResp.retFail(400,"错的操作");

    }

}
