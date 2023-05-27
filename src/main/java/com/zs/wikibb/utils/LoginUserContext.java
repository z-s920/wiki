package com.zs.wikibb.utils;


import com.zs.wikibb.resp.UserwikiLoginResp;

import java.io.Serializable;

public class LoginUserContext implements Serializable {

    private static ThreadLocal<UserwikiLoginResp> user = new ThreadLocal<>();

    public static UserwikiLoginResp getUser() {
        return user.get();
    }

    public static void setUser(UserwikiLoginResp user) {
        LoginUserContext.user.set(user);
    }

}
